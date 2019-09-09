package nl.uvt.slu.truncator

import nl.uvt.slu.balance.{BreakBalancer, MergeBalancer}
import nl.uvt.slu.parser.{Document, Paragraph, Sentence, Word}

import scala.collection.immutable.SortedSet

class SyntacticTruncator(mergeBalancer: MergeBalancer, breakBalancer: BreakBalancer) extends Truncator {

  import SyntacticTruncator._

  override def truncate(sent: Sentence): Seq[Line] = {
    val words = sent.words

    val headWord = words.find(w => w.arguments.getOrElse(Seq.empty).nonEmpty && w.relate == "HED")

    val breakerIndex = headWord.map { h =>
      val pars = h.arguments.get.map(arg => (arg.beg.toInt, arg.end.toInt)).sortWith(
        (left, right) => left._1 <= right._1
      )

      if (pars.isEmpty) Seq.empty
      else if (pars.size == 1) Seq(pars.head._1, pars.head._2)
      else {
        pars.sliding(2).flatMap { s =>
          val (left, right) = (s.head, s(1))
          if (left._2 == right._1 - 1) Seq(left._2, right._2)
          else Seq(left._2, right._1 - 1, right._2)
        }.toSet.toSeq.sorted
      }
    }.getOrElse(Seq.empty)

    val punctIndexes = words.filter(w => isBreakPunt(w)).map(_.id)

    val indexes: SortedSet[Int] = (breakerIndex ++ punctIndexes).to[SortedSet]

    val lines = divide(words, indexes)


    val mergedLine = mergeBalancer(lines)
    val breakLine = breakBalancer(mergedLine)
    lines
//    breakBalancer(mergedLine)
  }

  override def truncate(para: Paragraph): Seq[Line] = {
    val lines = para.sentences.flatMap(
      s => truncate(s)
    )
    mergeBalancer(lines)
  }

  override def truncate(doc: Document): Seq[Line] = {
    doc.paragraphs.flatMap(
      p =>
        truncate(p)
    )
  }
}

object SyntacticTruncator {
  private val MIN_CHAR = 4
  private val MAX_CHAR = 30
  private val END_PUNCTS = Seq("。", "!", "?", "……")
  private val SEPERATE_PUNCTS = Seq("，", "；", "：")
  private val BREAK_PUNCTS = END_PUNCTS ++ SEPERATE_PUNCTS

  def divide(words: Seq[Word], indexes: SortedSet[Int]): Seq[Seq[Word]] = {
    words match {
      case Nil => Seq.empty
      case head :: Nil => Seq(words)
      case _ => words.span(w => !indexes.contains(w.id)) match {
        case (left, Nil) => Seq(left)
        case (left, right) => (left :+ right.head) +: divide(right.tail, indexes)
      }
    }
  }.filter(_.nonEmpty)

  def shouldBreak(line: Line): Boolean = line.show.size >= MAX_CHAR

  private def isBreakPunt(word: Word) = {
    word.pos == "wp" && BREAK_PUNCTS.contains(word.content)
  }

  private def isNotBreakPunt(word: Word) = !isBreakPunt(word)

}
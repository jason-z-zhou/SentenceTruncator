package nl.uvt.slu.truncator

import nl.uvt.slu.parser.{Document, Paragraph, Sentence, Word}

import scala.collection.mutable

class SyntacticTruncator extends Truncator {

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

    val indexes = (breakerIndex ++ punctIndexes).sorted.toSet

    val lines = divide(words, indexes)
    lines
//    balance(lines)
  }

  override def truncate(para: Paragraph): Seq[Line] = {
    para.sentences.flatMap(
      s => truncate(s)
    )
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
  private val MAX_CHAR = 25
  private val END_PUNCTS = Seq("。", "!", "?", "……")
  private val SEPERATE_PUNCTS = Seq("，", "；", "：")
  private val BREAK_PUNCTS = END_PUNCTS ++ SEPERATE_PUNCTS

  def divide(words: Seq[Word], indexes: Set[Int]): Seq[Seq[Word]] = {
    words match {
      case Nil => Seq.empty
      case head :: Nil => Seq(words)
      case _ => words.span(w => !indexes.contains(w.id)) match {
        case (left, Nil) => Seq(left)
        case (left, right) => (left :+ right.head) +: divide(right.tail, indexes)
      }
    }
  }.filter(_.nonEmpty)

  def balance(lines: Seq[Line]): Seq[Line] = {
    if(lines.size <= 2) lines
    else {
      val tripleLines = lines.sliding(3).toSeq

      val tupleLines: Seq[Seq[Line]] = tripleLines.map { triple: Seq[Line] =>
        val (prev, curr, next) = (triple.head, triple(1), triple(2))
        if (!shouldMerge(curr)) triple
        else {
          if (parent(curr) <= curr.head.id) Seq(prev ++ curr, next)
          else Seq(prev, curr ++ next)
        }
      }

      val result: mutable.Seq[Line] = mutable.Seq.empty[Line]

      tupleLines.foreach { lines: Seq[Line] =>
        lines.foreach { line =>
          if (result.isEmpty) result +: line
          else if (result.last.last.id < line.head.id) result +: line
        }
      }
      result
    }
  }

  def shouldMerge(line: Line): Boolean = line.show.size <= MIN_CHAR

  def shouldBreak(line: Line): Boolean = line.show.size >= MAX_CHAR

  def parent(line: Line): Int = {
    parent(line.head, line)
  }

  def parent(word: Word, line: Line): Int = {
    val p = word.parent
    if (p < 0) p
    else {
      val maybeParentNode = line.find(w => w.id == p)
      maybeParentNode match {
        case None => p
        case Some(word) => parent(word, line)
      }
    }
  }

  private def isBreakPunt(word: Word) = {
    word.pos == "wp" && BREAK_PUNCTS.contains(word.content)
  }

  private def isNotBreakPunt(word: Word) = !isBreakPunt(word)

}

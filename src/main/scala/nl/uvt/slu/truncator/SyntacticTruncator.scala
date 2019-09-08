package nl.uvt.slu.truncator

import nl.uvt.slu.parser.{Document, Paragraph, Sentence, Word}

//case class Para(id: String, sent: Seq[Sent])
//case class Sent(id: String, cont: String, words: Seq[Word])
//case class Word(id: String, cont: String, pos: String, ne: String, parent: String, relate: String, args: Option[Seq[Arg]])
class SyntacticTruncator extends Truncator {

  import SyntacticTruncator._

  /**
    *   - 最高级别要求：单行最少和最多字符数
    *   - 句与句：句终分开：句号，感叹号，分号。。
    *   - 并列句：从连词前分开 and， or， but
    *   - 复合句：从从句引导词前分开 6个w， that
    *   - 简单句：主语+谓语， 从谓语前分开
    *   - 如果划分后少于最少字符数，合并前后句
    *   - 如果划分后多于最多字符数，从此行根结点或者级别最高的节点之前分开
    *
    * @param sent
    * @return
    */
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

    divide(words, indexes)
  }

  override def truncate(para: Paragraph): Seq[Line] = {
    para.sentences.flatMap(
      s => truncate(s)
    )
    // If a wordbag only contains a single word punctuation, merge it with previous wordbag
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

//  def balance(lines: Seq[Line]): Seq[Line] = {
//    lines
//  }
//
//  def merge(forward: Option[Line], right: Line, backward: Option[Line]) = {
//
//
//  }
//
//  def rootWord(line: Line): Word = {
//    line.map(_.parent)
//  }

  def shouldMerge(line: Line):Boolean = false

  def shouldBreak(line: Line):Boolean = false


  private def isBreakPunt(word: Word) = {
    word.pos == "wp" && BREAK_PUNCTS.contains(word.content)
  }

  private def isNotBreakPunt(word: Word) = !isBreakPunt(word)

}

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
  override def truncate(sent: Sentence): Seq[WordBag] = {
    val words = sent.words
    val rawWordBag = divide(words)(isNotBreakPunt)
    rawWordBag.flatMap { words =>
      val headWord: Option[Word] = words.find(w => w.arguments.getOrElse(Seq.empty).nonEmpty && w.relate == "HED")

      val indexes: Set[Int] = headWord match {
        case None => Set.empty
        case Some(w) => {
          val pars: Seq[(Int, Int)] = w.arguments.get.map(arg => (arg.beg.toInt, arg.end.toInt)).sortWith(
            (left, right) => left._1 <= right._1
          )
          pars.sliding(2).flatMap { s =>
            val (left, right) = (s.head, s(1))
            if (left._2 == right._1 - 1) Seq(left._2, right._2)
            else Seq(left._2, right._1 - 1, right._2)
          }.toSet
        }
      }
      if (indexes.nonEmpty) {
        divide(words)(w => !indexes.contains(w.id.toInt))
      } else {
        Seq(words)
      }
    }
  }

  override def truncate(para: Paragraph): Seq[WordBag] = {
    para.sentences.flatMap(
      s => truncate(s)
    )
    // If a wordbag only contains a single word punctuation, merge it with previous wordbag
  }

  override def truncate(doc: Document): Seq[WordBag] = {
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
  private val BREAK_PUNCTS = Seq("，", "；", "：")

  private def divide[T](elements: Seq[T])(condition: T => Boolean): Seq[Seq[T]] = {
    if (elements.isEmpty) Seq.empty
    else
      elements.span(condition) match {
        case (Nil, Nil) => Seq.empty
        case (left, Nil) => Seq(left)
        case (Nil, right) => divide(right)(condition)
        case (left, right) => (left :+ right.head) +: divide(right.tail)(condition)
      }
  }.filterNot(_.isEmpty)

  private def isBreakPunt(word: Word) = {
    word.pos == "wp" && BREAK_PUNCTS.contains(word.content)
  }

  private def isNotBreakPunt(word: Word) = !isBreakPunt(word)

}

package nl.uvt.slu.truncator

import nl.uvt.slu.balance.MergeBalancer
import nl.uvt.slu.parser.{Sentence, Word}

import scala.collection.immutable.SortedSet
import scala.util.Random

class RandomTruncator(mergeBalancer: MergeBalancer, random: Random) extends Truncator {

  import RandomTruncator._

  override def truncate(sent: Sentence): Seq[Line] = {

    val sentenceStr = sent.words.show

    val randomInt = (1 until 30).map(i => random.nextInt(MAX_CHAR - MIN_CHAR) + MIN_CHAR).scanLeft(0)(
      (sum,
       rand) => sum + rand
    ).takeWhile(_ < sentenceStr.size)


    val indexes: Seq[Int] = randomInt.map { r =>
      if (BREAK_PUNCTS.contains(sentenceStr.indexOf(r))) r + 1
      else r
    }

    val begins = 0 +: indexes
    val ends = indexes :+ sentenceStr.size


    val lines: Seq[Line] = begins.zip(ends).map { case (begin, end) =>
      sentenceStr.substring(begin, end)
    }.filterNot(_.isEmpty)
    lines
  }

}

object RandomTruncator {
  val MIN_CHAR = 5
  val MAX_CHAR = 30
  private val END_PUNCTS = Seq("。", "!", "?", "……")
  private val SEPERATE_PUNCTS = Seq("，", "；", "：")
  private val RIGHT_PUNCTS = Seq("”", "》", "、", "）")
  private val LEFT_PUNCTS = Seq("“", "《", "（")
  private val BREAK_PUNCTS = END_PUNCTS ++ SEPERATE_PUNCTS

  def divide(line: wordBag, indexes: SortedSet[Int]): Seq[wordBag] = {
    line match {
      case Nil => Seq.empty
      case head :: Nil => Seq(line)
      case _ => line.span(w => !indexes.contains(w.id)) match {
        case (left, Nil) => Seq(left)
        case (left, right) => (left :+ right.head) +: divide(right.tail, indexes)
      }
    }
  }.filter(_.nonEmpty)

  def shouldBreak(line: wordBag): Boolean = line.show.size >= MAX_CHAR

  private def isBreakPunt(word: Word) = {
    word.pos == "wp" && BREAK_PUNCTS.contains(word.content)
  }

  private def isNotBreakPunt(word: Word) = !isBreakPunt(word)

  private def fixPunct(lines: Seq[wordBag]): Seq[wordBag] = {
    lines.zip(lines.tail :+ lines.head).map { case (curr: wordBag, next: wordBag) =>
      if ((BREAK_PUNCTS ++ RIGHT_PUNCTS).exists(b => next.head.content.contains(b))) {
        curr :+ next.head
      } else if ((BREAK_PUNCTS ++ RIGHT_PUNCTS).exists(b => curr.head.content.contains(b))) {
        curr.drop(1)
      } else curr

    }
  }

}
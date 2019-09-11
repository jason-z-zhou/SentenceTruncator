package nl.uvt.slu.truncator

import nl.uvt.slu.balance.MergeBalancer
import nl.uvt.slu.parser.{Sentence, Word}

import scala.collection.immutable.SortedSet
import scala.util.Random

class RandomTruncator(mergeBalancer: MergeBalancer, random: Random) extends Truncator {

  import RandomTruncator._

  override def truncate(sent: Sentence): Seq[Line] = {
    val randomInt = (1 until 20).map(i => random.nextInt(MAX_CHAR - MIN_CHAR) + MIN_CHAR).scanLeft(0)(
      (sum,
       rand) => sum + rand
    )
    val sizeSumScan = sent.words.scanLeft(0)((sum, word) => sum + word.content.size).zipWithIndex


    val indexes = randomInt.flatMap(
      r =>
        sizeSumScan.find(p => p._1 > r)
    ).map(_._2).to[SortedSet]

    val wordBags = fixPunct(mergeBalancer(divide(sent.words, indexes)))

    wordBags.map(_.show)
  }

}

object RandomTruncator {
  val MIN_CHAR = 5
  val MAX_CHAR = 30
  private val END_PUNCTS = Seq("。", "!", "?", "……")
  private val SEPERATE_PUNCTS = Seq("，", "；", "：")
  private val OTHER_PUNCTS = Seq("”", "》", "、", "）")
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
      if ((BREAK_PUNCTS ++ OTHER_PUNCTS).exists(b => next.head.content.contains(b))) {
        curr :+ next.head
      } else if ((BREAK_PUNCTS ++ OTHER_PUNCTS).exists(b => curr.head.content.contains(b))) {
        curr.drop(1)
      } else curr

    }
  }

}
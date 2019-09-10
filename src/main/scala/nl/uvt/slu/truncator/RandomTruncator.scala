package nl.uvt.slu.truncator

import nl.uvt.slu.balance.MergeBalancer
import nl.uvt.slu.parser.{Sentence, Word}

import scala.collection.immutable.SortedSet
import scala.util.Random

class SyntacticTruncator(mergeBalancer: MergeBalancer, random: Random) extends Truncator {

  override def truncate(sent: Sentence): Seq[Line] = {
    val length = sent.words.show.size
    val indexChar = (1 until 10).map(i => random.nextInt(26) + 4)

  }

}

object RandomTruncator {
  private val MIN_CHAR = 4
  private val MAX_CHAR = 30
  private val END_PUNCTS = Seq("。", "!", "?", "……")
  private val SEPERATE_PUNCTS = Seq("，", "；", "：")
  private val BREAK_PUNCTS = END_PUNCTS ++ SEPERATE_PUNCTS

  def divide(line: Line, indexes: SortedSet[Int]): Seq[Line] = {
    line match {
      case Nil => Seq.empty
      case head :: Nil => Seq(line)
      case _ => line.span(w => !indexes.contains(w.id)) match {
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
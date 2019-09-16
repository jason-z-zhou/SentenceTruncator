package nl.uvt.slu.truncator

import nl.uvt.slu.balance.MergeBalancer
import nl.uvt.slu.parser.{Sentence, Word}
import nl.uvt.slu.semantic.{SemanticWord, WordDistanceCalculator}

import scala.collection.immutable.SortedSet

class SemanticTruncator(wordDistanceCalculator: WordDistanceCalculator, mergeBalancer: MergeBalancer)
  extends Truncator {

  import SemanticTruncator._

  override def truncate(sent: Sentence): Seq[Line] = {
    val leftWords: Seq[Word] = sent.words
    val rightWords: Seq[Word] = leftWords.tail :+ leftWords.head

    val semanticWords = leftWords.zip(rightWords).map { case (left: Word, right: Word) =>
      val distance = wordDistanceCalculator.calculate(left.content, right.content)
      SemanticWord(left, distance)
    }

    val indexes = for {
      semanticWord <- semanticWords
      distance <- semanticWord.distance
      if distance <= THRESHOLD
    } yield semanticWord.word.id

    val lines = divide(leftWords, indexes.to[SortedSet])

    lines.map(_.show)
//    val merged = mergeBalancer(lines)
//    merged.map(_.show)
  }
}


object SemanticTruncator {
  val THRESHOLD = 1

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

}

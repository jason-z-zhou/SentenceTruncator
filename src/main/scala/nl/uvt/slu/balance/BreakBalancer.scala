package nl.uvt.slu.balance

import nl.uvt.slu.parser.Word
import nl.uvt.slu.truncator.Line
import nl.uvt.slu.truncator.WordBagString

import scala.collection.mutable

class BreakBalancer extends Balancer {
  import BreakBalancer._
  override def apply(lines: Seq[Line]): Seq[Line] = {
    val litr = lines.iterator
    var result: mutable.SortedSet[Line] = mutable.SortedSet.empty[Line](LineOrdering)

    while (litr.hasNext) {
      val current = litr.next()
      if (result.isEmpty) result = result + current
      else if (shouldBreak(current)) {
        val rootId = root(current.head, current)
        val sublines: Seq[Line] = divide(current, mutable.SortedSet[Int](rootId))

        sublines.foreach(line => result = result + line)
      } else {
        result = result + current
      }
    }
    result.toSeq
  }

}

object BreakBalancer{
  private val MAX_CHAR = 10

  object LineOrdering extends Ordering[Line] {
    def compare(a:Line, b:Line) = a.head.id compare b.head.id
  }

  def shouldBreak(line: Line): Boolean = line.show.size >= MAX_CHAR

  def divide(words: Seq[Word], indexes: mutable.SortedSet[Int]): Seq[Seq[Word]] = {
    words match {
      case Nil => Seq.empty
      case head :: Nil => Seq(words)
      case _ => words.span(w => !indexes.contains(w.id)) match {
        case (left, Nil) => Seq(left)
        case (left, right) => (left :+ right.head) +: divide(right.tail, indexes)
      }
    }
  }.filter(_.nonEmpty)


  def root(word: Word, line: Line): Int = {
    val p = word.parent
    if (p < 0) p
    else {
      val maybeParentNode = line.find(w => w.id == p)
      maybeParentNode match {
        case None => word.id
        case Some(word) => root(word, line)
      }
    }
  }

}


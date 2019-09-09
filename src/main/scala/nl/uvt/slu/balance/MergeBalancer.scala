package nl.uvt.slu.balance

import nl.uvt.slu.parser.Word
import nl.uvt.slu.truncator.{Line, WordBagString}
import scala.collection.mutable

class MergeBalancer extends Balancer {
  import MergeBalancer._
  override def apply(v1: Seq[Line]): Seq[Line] = {
    mergeBalance(v1)
  }

  def mergeBalance(lines: Seq[Line]): Seq[Line] = {
    val litr = lines.iterator
    var result: mutable.SortedSet[Line] = mutable.SortedSet.empty[Line](LineOrdering)

    while (litr.hasNext) {
      val current = litr.next()
      if (result.isEmpty) result = result + current
      else if (shouldMergeBackward(current)) {
        val prev = result.last
        result = result - prev + (prev ++ current)
      }
      else if (shouldMergeForward(current)) {
        val next = litr.next()
        result = result + (current ++ next)
      }else{
        result = result + current
      }
    }
    result.toSeq
  }

  def shouldMergeForward(line: Line): Boolean = shouldMerge(line) && parent(line) >= line.head.id

  def shouldMergeBackward(line: Line): Boolean = shouldMerge(line) && parent(line) < line.head.id

  def shouldMerge(line: Line): Boolean = line.show.size <= MIN_CHAR


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
}

object MergeBalancer{
  private val MIN_CHAR = 4
  private val MAX_CHAR = 30

  object LineOrdering extends Ordering[Line] {
    def compare(a:Line, b:Line) = a.head.id compare b.head.id

  }
}


package nl.uvt.slu.balance

import nl.uvt.slu.parser.Word
import nl.uvt.slu.truncator.{wordBag, LineString}
import scala.collection.mutable

class MergeBalancer extends Balancer {

  import MergeBalancer._

  override def apply(lines: Seq[wordBag]): Seq[wordBag] = {
    val litr = lines.iterator
    var result: mutable.SortedSet[wordBag] = mutable.SortedSet.empty[wordBag](LineOrdering)

    while (litr.hasNext) {
      val current = litr.next()
      if (result.isEmpty || !shouldMerge(current)) result = result + current
      else if (shouldMergeBackward(current)) {
        val prev = result.last
        result = result - prev + (prev ++ current)
      }
      else {
        result = result + (current ++ litr.next())
      }
    }
    result.toSeq
  }

}

object MergeBalancer {
  private val MIN_CHAR = 4
  private val MAX_CHAR = 30

  object LineOrdering extends Ordering[wordBag] {
    def compare(a: wordBag, b: wordBag) = a.head.id compare b.head.id

  }

  def shouldMergeForward(line: wordBag): Boolean = shouldMerge(line) && parent(line) > line.head.id

  def shouldMergeBackward(line: wordBag): Boolean = shouldMerge(line) && !shouldMergeForward(line)

  def shouldMerge(line: wordBag): Boolean = line.show.size < MIN_CHAR

  def parent(line: wordBag): Int = {
    parent(line.head, line)
  }

  def parent(word: Word, line: wordBag): Int = {
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


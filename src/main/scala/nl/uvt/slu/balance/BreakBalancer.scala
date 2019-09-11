package nl.uvt.slu.balance

import nl.uvt.slu.parser.Word
import nl.uvt.slu.truncator.{wordBag, LineString}

class BreakBalancer extends Balancer {

  import BreakBalancer._

  override def apply(lines: Seq[wordBag]): Seq[wordBag] = {
    lines.flatMap { line =>
      if (!shouldBreak(line)) Seq(line)
      else divide(line)
    }
  }

}

object BreakBalancer {
  private val MAX_CHAR = 10

  object LineOrdering extends Ordering[wordBag] {
    def compare(a: wordBag, b: wordBag) = a.head.id compare b.head.id
  }

  def shouldBreak(line: wordBag): Boolean = line.show.size >= MAX_CHAR

  def divide(line: wordBag): Seq[wordBag] = {
    divide(line, root(line))
  }

  def divide(line: wordBag, index: Int): Seq[wordBag] = {
    line match {
      case Nil => Seq.empty
      case head :: Nil => Seq(line)
      case _ => line.span(w => w.id != index) match {
        case (left, Nil) => Seq(left)
        case (Nil, right) => Seq(right)
        case (left, right) => Seq(left, right)
      }
    }
  }.filter(_.nonEmpty)


  def root(line: wordBag): Int = root(line.head, line)

  def root(word: Word, line: wordBag): Int = {
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


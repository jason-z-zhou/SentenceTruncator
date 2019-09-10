package nl.uvt.slu.balance

import nl.uvt.slu.parser.Word
import nl.uvt.slu.truncator.{Line, LineString}

class BreakBalancer extends Balancer {

  import BreakBalancer._

  override def apply(lines: Seq[Line]): Seq[Line] = {
    lines.flatMap { line =>
      if (!shouldBreak(line)) Seq(line)
      else divide(line)
    }
  }

}

object BreakBalancer {
  private val MAX_CHAR = 10

  object LineOrdering extends Ordering[Line] {
    def compare(a: Line, b: Line) = a.head.id compare b.head.id
  }

  def shouldBreak(line: Line): Boolean = line.show.size >= MAX_CHAR

  def divide(line: Line): Seq[Line] = {
    divide(line, root(line))
  }

  def divide(line: Line, index: Int): Seq[Line] = {
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


  def root(line: Line): Int = root(line.head, line)

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


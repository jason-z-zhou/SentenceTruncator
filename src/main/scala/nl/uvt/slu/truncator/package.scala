package nl.uvt.slu

import nl.uvt.slu.parser.Word

package object truncator {
  type Line = Seq[Word]

  implicit class WordBagString(val wordBag: Line) {
    def show: String = {
      wordBag.map(_.content).reduce(_ + _)
    }
  }

  implicit class WordBagsString(val wordBags: Seq[Line]) {
    def show = {
      wordBags.map(_.show).reduce((left, right) => left + "\n" + right)
    }
  }

}

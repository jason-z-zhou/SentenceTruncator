package nl.uvt.slu

import nl.uvt.slu.parser.Word

package object truncator {
  type WordBag = Seq[Word]

  implicit class WordBagString(val wordBag: WordBag) {
    def show: String = {
      wordBag.map(_.content).reduce(_ + _)
    }
  }

  implicit class WordBagsString(val wordBags: Seq[WordBag]) {
    def show = {
      wordBags.map(_.show).reduce((left, right) => left + "\n" + right)
    }
  }

}

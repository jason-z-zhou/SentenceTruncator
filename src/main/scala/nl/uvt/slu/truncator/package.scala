package nl.uvt.slu

import nl.uvt.slu.parser.Word

package object truncator {
  type wordBag = Seq[Word]
  type Line = String
  type Para = Seq[Line]
  type Doc = Seq[Para]

  implicit class LineString(val wordBag: wordBag) {
    def show: String = {
      wordBag.map(_.content).reduce(_ + _)
    }
  }

  implicit class ParaString(val para: Para) {
    def show = {
      para.reduce((left, right) => left + "\n" + right) + "\n"
    }
  }


  implicit class DocString(val paras: Seq[Para]) {
    def show = {
      paras.map(_.show).reduce((left, right) => left + "\n" + right)
    }
  }

}

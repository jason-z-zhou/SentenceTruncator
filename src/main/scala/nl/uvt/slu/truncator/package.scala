package nl.uvt.slu

import nl.uvt.slu.parser.Word

package object truncator {
  type Line = Seq[Word]
  type Para = Seq[Line]
  type Doc = Seq[Para]

  implicit class LineString(val line: Line) {
    def show: String = {
      line.map(_.content).reduce(_ + _)
    }
  }

  implicit class ParaString(val para: Para) {
    def show = {
      para.map(_.show).reduce((left, right) => left + "\n" + right) + "\n"
    }
  }


  implicit class DocString(val paras: Seq[Para]) {
    def show = {
      paras.map(_.show).reduce((left, right) => left + "\n" + right)
    }
  }

}

package nl.uvt.slu.parser

import java.io.File
import com.lucidchart.open.xtract.XmlReader
import scala.io.Source
import scala.xml.XML
/**
  * This class provide functionality to parse xml data into scala case classes
  */
trait XmlHelper {
  def xtract(filePath: String): Option[Xml4Nlp] = {
    val xmlData = Source.fromFile(new File(filePath)).getLines().mkString("\n")
    val xml = XML.loadString(xmlData)
    XmlReader.of[Xml4Nlp].read(xml).toOption
  }
}

package nl.uvt.slu.parser
object XtractSampleApp extends App with XmlHelper {
  val path = "src/main/sample/xml/A1.xml"
  val xml4nlp = xtract(path)
  println("***RESPONSE: " + xml4nlp)
}
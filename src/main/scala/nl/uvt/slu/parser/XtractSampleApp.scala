package nl.uvt.slu.parser

import java.io.{File, PrintWriter}

import nl.uvt.slu.truncator.{SyntacticTruncator, WordBagsString}

object XtractSampleApp extends App with XmlHelper {
  val inPath = "src/main/resources/xml/A1.xml"
  val outPath = "src/main/resources/result/A1.txt"

  val truncator = new SyntacticTruncator
  // Read xml into object
  val xml4nlp = xtract(inPath)

  private val content = xml4nlp.get.doc.paragraphs(0).sentences(1)
  //truncation
  val wordBags = truncator.truncate(content)
  private val result = wordBags.show
  println(result)

  //write to file
  val printWriter = new PrintWriter(new File(outPath))
  printWriter.write(result)
  printWriter.close()
}
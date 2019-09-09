package nl.uvt.slu.parser

import java.io.{File, PrintWriter}

import nl.uvt.slu.balance.MergeBalancer
import nl.uvt.slu.truncator.{SyntacticTruncator, WordBagsString}

object XtractSampleApp extends App with XmlHelper {
  val fileName = "A9"
  val inPath = s"src/main/resources/xml/${fileName}.xml"
  val outPath = s"src/main/resources/result/${fileName}.txt"

  val truncator = new SyntacticTruncator(new MergeBalancer)
  // Read xml into object
  val xml4nlp = xtract(inPath)

  private val content = xml4nlp.get.doc
  //truncation
  val wordBags = truncator.truncate(content)
  private val result = wordBags.show
  println(result)

  //write to file
  val printWriter = new PrintWriter(new File(outPath))
  printWriter.write(result)
  printWriter.close()
}
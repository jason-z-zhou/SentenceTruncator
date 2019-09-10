package nl.uvt.slu.parser

import java.io.{File, PrintWriter}

import nl.uvt.slu.balance.{BreakBalancer, MergeBalancer}
import nl.uvt.slu.truncator.{DocString, SyntacticTruncator}

object XtractSampleApp extends App with XmlHelper {
  val fileName = "A9"
  val inPath = s"src/main/resources/xml/${fileName}.xml"
  val outPath = s"src/main/resources/syntactic_result/${fileName}.txt"

  val truncator = new SyntacticTruncator(new MergeBalancer, new BreakBalancer)
  // Read xml into object
  val xml4nlp = xtract(inPath)

  private val content = xml4nlp.get.doc
  //truncation
  val doc = truncator.truncate(content)
  val result = doc.show
  println(result)

  //write to file
  val printWriter = new PrintWriter(new File(outPath))
  printWriter.write(result)
  printWriter.close()
}
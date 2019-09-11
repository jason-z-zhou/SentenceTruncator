package nl.uvt.slu.parser

import java.io.{File, PrintWriter}

import nl.uvt.slu.balance.{BreakBalancer, MergeBalancer}
import nl.uvt.slu.truncator.{DocString, RandomTruncator, SyntacticTruncator}

import scala.util.Random

object XtractSampleApp extends App with XmlHelper {
  val fileName = "A8"
  val inPath = s"src/main/resources/xml/${fileName}.xml"
  val syntacticOutPath = s"src/main/resources/syntactic_result/${fileName}.txt"
  val randomOutPath = s"src/main/resources/random_result/${fileName}.txt"

  private val mergeBalancer = new MergeBalancer
  val syntacticTruncator = new SyntacticTruncator(mergeBalancer, new BreakBalancer)

  val randomTruncator = new RandomTruncator(mergeBalancer, new Random)
  // Read xml into object
  val xml4nlp = xtract(inPath)

  private val content = xml4nlp.get.doc

  //syntactic truncation
  val syntacticDoc = syntacticTruncator.truncate(content)
  val syntacticResult = syntacticDoc.show
//  println(syntacticResult)
  val syntacticPrintWriter = new PrintWriter(new File(syntacticOutPath))
  syntacticPrintWriter.write(syntacticResult)
  syntacticPrintWriter.close()


  //random truncation
  val randomDoc = randomTruncator.truncate(content)
  val randomResult = randomDoc.show
  println(randomResult)
  val randomPrintWriter = new PrintWriter(new File(randomOutPath))
  randomPrintWriter.write(randomResult)
  randomPrintWriter.close()
}
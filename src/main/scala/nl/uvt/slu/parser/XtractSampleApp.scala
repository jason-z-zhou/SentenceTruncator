package nl.uvt.slu.parser

import java.io.{File, PrintWriter}

import nl.uvt.slu.balance.{BreakBalancer, MergeBalancer}
import nl.uvt.slu.semantic.WordDistanceCalculator
import nl.uvt.slu.truncator.{DocString, RandomTruncator, SemanticTruncator, SyntacticTruncator}

import scala.io.Source
import scala.util.Random

object XtractSampleApp extends App with XmlHelper {
  val fileName = "A9"
  val inPath = s"src/main/resources/xml/${fileName}.xml"
  val syntacticOutPath = s"src/main/resources/syntactic_result/${fileName}.txt"
  val randomOutPath = s"src/main/resources/random_result/${fileName}.txt"
  val semanticOutPath = s"src/main/resources/semantic_result/${fileName}.txt"

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
  //  println(randomResult)
  val randomPrintWriter = new PrintWriter(new File(randomOutPath))
  randomPrintWriter.write(randomResult)
  randomPrintWriter.close()

  //semantic truncation
  val dictionaryPath = "/Users/jaszhou/dev/ltp/word-coocurrence/word-coocurrence-dim300"
  val dictionary: Map[String, Array[Float]] = Source.fromFile(dictionaryPath).getLines()
    .map { line =>
      val pars = line.split(" ")
      (pars.head, pars.tail.map(_.toFloat))
    }.toMap

  val wordDistanceCalculator = new WordDistanceCalculator(dictionary)

  val semanticTruncator = new SemanticTruncator(wordDistanceCalculator, mergeBalancer)
  val semanticDoc = semanticTruncator.truncate(content)
  val semanticResult = semanticDoc.show
  println(semanticResult)
  val semanticPrintWriter = new PrintWriter(new File(semanticOutPath))
  semanticPrintWriter.write(semanticResult)
  semanticPrintWriter.close()

}
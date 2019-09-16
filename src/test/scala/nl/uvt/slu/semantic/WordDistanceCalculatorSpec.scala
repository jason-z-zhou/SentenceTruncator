package nl.uvt.slu.semantic

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class WordDistanceCalculatorSpec extends FlatSpec with Matchers {

  private val dictionary: Map[String, Array[Float]] = Source.fromFile("./src/test/resources/sample.txt").getLines().map {
    line =>
      val pars = line.split(" ")
      (pars.head, pars.tail.map(_.toFloat))
  }.toMap

  private val wordDistanceCalculator = new WordDistanceCalculator(dictionary)

  it should "calculate distance" ignore {
    wordDistanceCalculator.calculate("和", "在")
  }

}

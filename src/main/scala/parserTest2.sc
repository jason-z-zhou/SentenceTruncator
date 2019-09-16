import nl.uvt.slu.semantic.WordDistanceCalculator

import scala.io.Source

case class Word(id: Int, content: String, isBreak: Boolean = false)


private val dictionary: Map[String, Array[Float]] = Source.fromFile("/Users/jaszhou/dev/ltp/word-coocurrence/word-coocurrence-dim300").getLines().map{ line =>
  val pars = line.split(" ")
  (pars.head, pars.tail.map(_.toFloat))
}.toMap

private val wordDistanceCalculator = new WordDistanceCalculator(dictionary)

println(dictionary.size)
wordDistanceCalculator.calculate("和","在")

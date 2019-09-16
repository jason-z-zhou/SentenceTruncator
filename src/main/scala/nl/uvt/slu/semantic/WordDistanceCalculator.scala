package nl.uvt.slu.semantic

class WordDistanceCalculator(dictionary: Map[String, Array[Float]]) {
  def calculate(left: String, right: String): Option[Float] = {
    calculate(getWordVector(left), getWordVector(right))
  }

  private def calculate(left: Array[Float], right: Array[Float]): Option[Float] = {
    if (left.nonEmpty && right.nonEmpty) {
      Option(left.zip(right).map { case (l, r) => l * r }.sum)
    } else {
      None
    }
  }

  private def getWordVector(content: String): Array[Float] = {
    val vector = dictionary.get(content)
    vector.getOrElse(Array.empty)
  }

}

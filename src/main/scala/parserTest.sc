case class Word(id: Int, content: String, isBreak: Boolean = false)

val words = Seq(
  Word(1, "apple"),
  Word(2, "banana"),
  Word(3, "cherry", true),
  Word(4, "Dewberries"),
  Word(5, "Eggfruit"),
  Word(6, "Fig", true),
  Word(7, "Grapefruit", true),
  Word(8, "Hackberry")
)

def divide(words: Seq[Word]): Seq[Seq[Word]] = {
  words match {
    case Nil => Seq.empty
    case head :: Nil => Seq(words)
    case _ => words.span(w => !w.isBreak) match {
      case (left, Nil) => Seq(left)
      case (left, right) => (left :+ right.head) +: divide(right.tail)
    }
  }
}

divide(words)




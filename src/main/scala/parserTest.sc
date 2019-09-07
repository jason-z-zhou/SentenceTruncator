import scala.io.Source
import scala.xml.pull._

class Main {
  val xml = new XMLEventReader(Source.fromFile("/Users/jaszhou/dev/aurora/SentenceTruncator/src/main/sample/xml/sample.xml"))

  def printText(text: String, currNode: List[String]) {
    currNode match {
      case List("firstname", "staff", "company") => println("First Name: " + text)
      case List("lastname", "staff", "company") => println("Last Name: " + text)
      case List("nickname", "staff", "company") => println("Nick Name: " + text)
      case List("salary", "staff", "company") => println("Salary: " + text)
      case _ => ()
    }
  }

  def parse(xml: XMLEventReader) {
    def loop(currNode: List[String]) {
      if (xml.hasNext) {
        xml.next match {
          case EvElemStart(_, label, _, _) =>
            println("Start element: " + label)
            loop(label :: currNode)
          case EvElemEnd(_, label) =>
            println("End element: " + label)
            loop(currNode.tail)
          case EvText(text) =>
            printText(text, currNode)
            loop(currNode)
          case _ => loop(currNode)
        }
      }
    }
    loop(List.empty)
  }

  def main(args: Array[String]): Unit = {
    parse(xml)
  }
}
package nl.uvt.slu.parser

import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.functional.syntax._

case class Xml4Nlp(note: Note, doc: Document)

case class Note(send: String, word: String, pos: String, ne: String, parser: String, wsd: String, srl: String)

case class Document(paragraphs: Seq[Paragraph])

case class Paragraph(id: String, sentences: Seq[Sentence])

case class Sentence(id: String, content: String, words: Seq[Word])

case class Word(id: String, content: String, pos: String, ne: String, parent: String, relate: String, arguments: Option[Seq[Arguments]])

case class Arguments(id: String, `type`: String, beg: String, end: String)

object Xml4Nlp {
  implicit val reader: XmlReader[Xml4Nlp] = (
    (__ \ "note").read[Note] and
      (__ \ "doc").read[Document]
    ) (apply _)
}

object Note {
  implicit val reader: XmlReader[Note] = (
    attribute[String](name = "sent") and
      attribute[String](name = "word") and
      attribute[String](name = "pos") and
      attribute[String](name = "ne") and
      attribute[String](name = "parser") and
      attribute[String](name = "wsd") and
      attribute[String](name = "srl")
    ) (apply _)
}


object Document {
  implicit val reader: XmlReader[Document] = (__ \ "para").read(seq[Paragraph]).default(Nil) map (apply _)
}

object Paragraph {
  implicit val reader: XmlReader[Paragraph] =
    (
      attribute[String]("id") and
        (__ \ "sent").read(seq[Sentence]).default(Nil)
      ) (apply _)

}

object Sentence {
  implicit val reader: XmlReader[Sentence] =
    (
      attribute[String](name = "id") and
        attribute[String](name = "cont") and
        (__ \ "word").read(seq[Word]).default(Nil)
      ) (apply _)
}

object Word {
  implicit val reader: XmlReader[Word] =
    (
      attribute[String](name = "id") and
        attribute[String](name = "cont") and
        attribute[String](name = "pos") and
        attribute[String](name = "ne") and
        attribute[String](name = "parent") and
        attribute[String](name = "relate") and
        (__ \ "arg").read(seq[Arguments]).optional
      ) (apply _)
}

object Arguments {
  implicit val reader: XmlReader[Arguments] =
    (
      attribute[String](name = "id") and
        attribute[String](name = "type") and
        attribute[String](name = "beg") and
        attribute[String](name = "end")
      ) (apply _)
}


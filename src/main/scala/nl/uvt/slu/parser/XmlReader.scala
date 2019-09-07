package nl.uvt.slu.parser

import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.functional.syntax._

case class Xml4Nlp(note: Note, doc: Doc)

case class Note(send: String, word: String, pos: String, ne: String, parser: String, wsd: String, srl: String)

case class Doc(paras: Seq[Para])

case class Para(id: String, sent: Seq[Sent])

case class Sent(id: String, cont: String, words: Seq[Word])

case class Word(id: String, cont: String, pos: String, ne: String, parent: String, relate: String, args: Seq[Arg])

case class Arg(id: String, `type`: String, beg: String, end: String)

object Xml4Nlp {
  implicit val reader: XmlReader[Xml4Nlp] = (
    (__ \ "note").read[Note].default(Nil) and
      (__ \ "doc").read[Doc].optional
    ) (apply _)
}

object Note {
  implicit val reader: XmlReader[Note] = (
    attribute[String](name = "name") and
      attribute[String](name = "word") and
      attribute[String](name = "pos") and
      attribute[String](name = "ne") and
      attribute[String](name = "parser") and
      attribute[String](name = "wsd") and
      attribute[String](name = "srl")
    ) (apply _)
}


object Doc {
  implicit val reader: XmlReader[Doc] = (__ \ "send").read(seq[Para]).default(Nil) map (apply _)
}

object Para {
  implicit val reader: XmlReader[Para] =
    (
      attribute[String]("id") and
        (__ \ "sent").read(seq[Sent]).default(Nil)
      ) (apply _)

}

object Sent {
  implicit val reader: XmlReader[Sent] =
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
        attribute[String](name = "parent") and
        attribute[String](name = "relate") and
        (__ \ "arg").read(seq[Arg]).default(Nil).optional
      ) (apply _)
}

object Arg {
  implicit val reader: XmlReader[Arg] =
    (
      attribute[String](name = "id") and
        attribute[String](name = "type") and
        attribute[String](name = "beg") and
        attribute[String](name = "end")
      ) (apply _)
}


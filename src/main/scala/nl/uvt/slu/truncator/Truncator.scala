package nl.uvt.slu.truncator

import nl.uvt.slu.parser.{Document, Paragraph, Sentence}

trait Truncator {
  def truncate(sent: Sentence):Seq[Line]

  def truncate(para: Paragraph): Seq[Line] = {
    val lines = para.sentences.map(
      s => truncate(s)
    ).reduce(_ ++ _)
    lines
  }


  def truncate(doc: Document): Seq[Para] = {
    doc.paragraphs.map(
      p => truncate(p)
    )
  }
}
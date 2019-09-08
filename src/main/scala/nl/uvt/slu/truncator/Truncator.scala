package nl.uvt.slu.truncator

import nl.uvt.slu.parser.{Document, Paragraph, Sentence}

trait Truncator {
  def truncate(doc: Document): Seq[Line]
  def truncate(para: Paragraph):Seq[Line]
  def truncate(sent: Sentence):Seq[Line]
}
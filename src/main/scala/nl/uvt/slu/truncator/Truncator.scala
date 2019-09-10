package nl.uvt.slu.truncator

import nl.uvt.slu.parser.{Document, Paragraph, Sentence}

trait Truncator {
  def truncate(doc: Document): Seq[Para]
  def truncate(para: Paragraph):Para
  def truncate(sent: Sentence):Seq[Line]
}
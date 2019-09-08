package nl.uvt.slu.truncator

import nl.uvt.slu.parser.{Document, Paragraph, Sentence}

trait Truncator {
  def truncate(doc: Document): Seq[WordBag]
  def truncate(para: Paragraph):Seq[WordBag]
  def truncate(sent: Sentence):Seq[WordBag]
}
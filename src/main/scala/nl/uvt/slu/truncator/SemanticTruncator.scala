package nl.uvt.slu.truncator
import nl.uvt.slu.parser.Sentence

class SemanticTruncator extends Truncator {
  override def truncate(sent: Sentence): Seq[Line] = {
    Seq.empty
  }
}


object SemanticTruncator {

}

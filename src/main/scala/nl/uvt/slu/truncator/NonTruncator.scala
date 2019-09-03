package nl.uvt.slu.truncator

class NonTruncator extends Truncator {
  override def apply(v1: String): Seq[String] = Seq(v1)
}

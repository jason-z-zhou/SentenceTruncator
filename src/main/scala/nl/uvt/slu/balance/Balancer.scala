package nl.uvt.slu.balance

import nl.uvt.slu.truncator.Line

trait Balancer extends (Seq[Line] => Seq[Line])
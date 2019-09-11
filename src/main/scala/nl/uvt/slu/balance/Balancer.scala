package nl.uvt.slu.balance

import nl.uvt.slu.truncator.wordBag

trait Balancer extends (Seq[wordBag] => Seq[wordBag])
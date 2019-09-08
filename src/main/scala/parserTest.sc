val index = Seq(2, 4, 5)

val elements = Seq("a", "b", "c", "d", "e", "f", "g", "h")

elements.sliding(2).foreach(s => println(s))
elements.sliding(2).foreach(s => println(s(1)))


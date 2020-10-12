package com.indoorvivants.http4s

object Main extends App {

  import AsUrlForm._

  case class SomethingElse(test1: String, test2: String)
  case class Test(
      hello: String,
      bla: Int,
      opty: Option[String],
      items: List[SomethingElse] = Nil
  )

  println(Test("bla", 5, Some("what")).asUrlForm)
  println(Test("bla", 5, None).asUrlForm)
  println(
    Test(
      "bla",
      5,
      None,
      List(SomethingElse("a", "b"), SomethingElse("c", "d"))
    ).asUrlForm
  )
}

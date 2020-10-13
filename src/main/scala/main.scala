package test

object Main extends App {

  import com.indoorvivants.http4s.generic_urlform._

  case class SomethingElse(test1: String, test2: String)
  case class Test(
      hello: String,
      bla: Int,
      opty: Option[String],
      items: List[SomethingElse] = Nil
  )

  println(Test("bla", 5, Some("what")).asUrlForm)
  println(Test("blaSSSS", 5, None).asUrlForm)
  println(
    Test(
      "bla",
      5,
      None,
      List(SomethingElse("a", "b"), SomethingElse("c", "d"))
    ).asUrlForm
  )
}

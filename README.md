## Generic derivation of UrlForm

Say you have this data model:

```scala mdoc
case class SomethingElse(test1: String, test2: String)
case class Test(
    hello: String,
    bla: Int,
    opty: Option[String],
    items: List[SomethingElse] = Nil
)
```

And you can produce a http4s `UrlForm` object of those using this:

```scala mdoc
import com.indoorvivants.http4s.generic_urlform._

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
```

And the results will be:

```scala
Map(hello -> Chain(bla), bla -> Chain(5), opty -> Chain(what))
Map(hello -> Chain(bla), bla -> Chain(5))
HashMap(items[1][test2] -> Chain(d), items[0][test2] -> Chain(b), items[1][test1] -> Chain(c), hello -> Chain(bla), items[0][test1] -> Chain(a), bla -> Chain(5))
```

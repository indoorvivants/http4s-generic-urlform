package com.indoorvivants.http4s

import scala.reflect.ClassTag

import cats.Show
import org.http4s.UrlForm
import org.scalacheck.Gen
import org.scalacheck.Gen.Choose
import weaver.SimpleMutableIOSuite
import weaver.SourceLocation
import weaver.scalacheck._

object AsUrlFormTest
    extends SimpleMutableIOSuite
    with IOCheckers
    with Generators {

  import generic_urlform._

  private val emptyForm = UrlForm.empty

  def primitiveTest[T: AsUrlForm: Show](
      gen: Gen[T]
  )(implicit ct: ClassTag[T]) = {
    simpleTest(
      s"Primitve: AsUrlForm[${ct.runtimeClass.getSimpleName()}] produces an empty form"
    ) {
      forall(gen) { a =>
        expect(a.asUrlForm == emptyForm)
      }
    }
  }

  // Primitive tests
  primitiveTest[Boolean](AnyBoolean)
  primitiveTest[String](AnyString)
  primitiveTest[Int](AnyNumber[Int])
  primitiveTest[Double](AnyNumber[Double])
  primitiveTest[Float](AnyNumber[Float])

  def listTest[T: AsUrlForm: Show](
      gen: Gen[T]
  )(implicit ct: ClassTag[T], sl: SourceLocation) = {
    val testName =
      s"Lists: AsUrlForm[List[${ct.runtimeClass.getSimpleName()}]]" +
        s" has same number of elements as the original list"

    simpleTest(testName) {
      forall(Gen.listOfN[T](10, gen)) { a =>
        expect(a.asUrlForm.values.size == a.size).traced(sl)
      }
    }
  }

  // flat list tests
  listTest[Boolean](AnyBoolean)
  listTest[String](AnyString)
  listTest[Int](AnyNumber[Int])
  listTest[Double](AnyNumber[Double])
  listTest[Float](AnyNumber[Float])

  // case class tests
  simpleTest("Optional field added to form when present") {
    case class Data(field: Option[String])

    expect.all(
      Data(None).asUrlForm == emptyForm,
      Data(Some("hello")).asUrlForm == UrlForm("field" -> "hello")
    )
  }

  simpleTest("Nested List fields added with indices") {
    case class Data(listField: List[String])

    expect.all(
      Data(Nil).asUrlForm == emptyForm,
      Data(List("hello")).asUrlForm == UrlForm("listField[0]" -> "hello"),
      Data(List("hello", "world")).asUrlForm == UrlForm(
        "listField[0]" -> "hello",
        "listField[1]" -> "world"
      )
    )
  }

  simpleTest("Nested List fields with complex objects have string indices") {
    case class Subdata(field1: String, field2: String)
    case class Data(listField: List[Subdata])

    expect.all(
      Data(Nil).asUrlForm == emptyForm,
      Data(List(Subdata("hello", "world"))).asUrlForm == UrlForm(
        "listField[0][field1]" -> "hello",
        "listField[0][field2]" -> "world"
      ),
      Data(List(Subdata("a", "b"), Subdata("c", "d"))).asUrlForm == UrlForm(
        "listField[0][field1]" -> "a",
        "listField[0][field2]" -> "b",
        "listField[1][field1]" -> "c",
        "listField[1][field2]" -> "d"
      )
    )
  }
}

trait Generators {
  def AnyNumber[T: Numeric: Choose]: Gen[T] =
    Gen.oneOf(Gen.posNum[T], Gen.negNum[T])

  val AnyBoolean = Gen.oneOf(true, false)
  val AnyString  = Gen.alphaNumStr
}

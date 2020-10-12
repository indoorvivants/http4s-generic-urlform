package com.indoorvivants.http4s

import cats.data.Chain
import magnolia._
import org.http4s.UrlForm

trait AsUrlForm[T] {
  import AsUrlForm.Path
  protected def convert(value: T): Map[Path, String]

  def asUrlForm(value: T) = {
    UrlForm.apply {
      convert(value).collect {
        case (h :: Nil, v) => h -> Chain(v)
        case (h :: other, v) =>
          (h + other.map(e => s"[$e]").mkString) -> Chain(v)
      }
    }
  }
}

trait AsUrlFormOpsInstances {
  implicit final class AsUrlFormOps[T: AsUrlForm](value: T) {
    def asUrlForm: UrlForm = AsUrlForm[T].asUrlForm(value)
  }
}

object AsUrlForm extends AsUrlFormOpsInstances {

  def apply[T](implicit asf: AsUrlForm[T]) = asf

  type Path = List[String]

  type Typeclass[T] = AsUrlForm[T]

  implicit val stringConvert: AsUrlForm[String] = new AsUrlForm[String] {
    def convert(value: String) = Map(Nil -> value)
  }

  implicit def optConvert[T](implicit asf: AsUrlForm[T]): AsUrlForm[Option[T]] =
    new AsUrlForm[Option[T]] {
      def convert(v: Option[T]): Map[Path, String] =
        v.fold(Map.empty[Path, String])(asf.convert)
    }

  implicit def listConvert[T](implicit
      asf: AsUrlForm[T]
  ): AsUrlForm[List[T]] =
    new AsUrlForm[List[T]] {
      override def convert(value: List[T]): Map[Path, String] = {
        value.zipWithIndex.flatMap {
          case (item, idx) =>
            asf.convert(item).map { case (k, v) => (idx.toString :: k) -> v }
        }.toMap
      }
    }

  implicit val intConvert: AsUrlForm[Int] = new AsUrlForm[Int] {
    def convert(value: Int) = Map(Nil -> value.toString)
  }

  def combine[T](caseClass: CaseClass[AsUrlForm, T]): AsUrlForm[T] =
    new AsUrlForm[T] {
      def convert(t: T): Map[Path, String] = {
        caseClass.parameters.flatMap { p =>
          p.typeclass.convert(p.dereference(t)).map {
            case (k, v) =>
              (p.label :: k) -> v
          }
        }.toMap
      }
    }

  def dispatch[T](ctx: SealedTrait[AsUrlForm, T]): AsUrlForm[T] =
    new AsUrlForm[T] {
      def convert(value: T): Map[Path, String] =
        ctx.dispatch(value) { sub =>
          sub.typeclass.convert(sub.cast(value))
        }
    }

  implicit def gen[T]: AsUrlForm[T] = macro Magnolia.gen[T]
}

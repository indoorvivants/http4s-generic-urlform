/*
 * Copyright 2020 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indoorvivants.http4s

import cats.data.Chain
import magnolia._
import org.http4s.UrlForm

import AsUrlForm.Path

private[http4s] trait AsUrlForm[T] {
  protected[http4s] def convert(value: T): Map[Path, String]

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

private[http4s] trait AsUrlFormOpsInstances {

  implicit final class AsUrlFormOps[T: AsUrlForm](value: T) {
    def asUrlForm: UrlForm = AsUrlForm[T].asUrlForm(value)
  }
}

private[http4s] trait AsUrlFormBaseInstances {
  private def primitive(s: String): Map[AsUrlForm.Path, String] = Map(Nil -> s)

  implicit val stringConvert: AsUrlForm[String] = new AsUrlForm[String] {
    def convert(value: String) = primitive(value)
  }

  implicit val boolConvert: AsUrlForm[Boolean] = new AsUrlForm[Boolean] {
    def convert(value: Boolean) = primitive(value.toString)
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

  implicit val IntConvert: AsUrlForm[Int] = new AsUrlForm[Int] {
    def convert(value: Int) = primitive(value.toString)
  }

  implicit val DoubleConvert: AsUrlForm[Double] = new AsUrlForm[Double] {
    def convert(value: Double) = primitive(value.toString)
  }

  implicit val FloatConvert: AsUrlForm[Float] = new AsUrlForm[Float] {
    def convert(value: Float) = primitive(value.toString)
  }

}

object AsUrlForm {
  type Path = List[String]

  def apply[T](implicit asf: AsUrlForm[T]) = asf
}

object generic_urlform
    extends AsUrlFormOpsInstances
    with AsUrlFormBaseInstances {

  type Typeclass[T] = AsUrlForm[T]

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

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

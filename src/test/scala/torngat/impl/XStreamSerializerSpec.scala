/*
 * Copyright (C) 2011 Julien Letrouit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package torngat
package impl

import org.specs2.mutable._
import com.eaio.uuid.UUID

case class SomeObjectGraph(idValue : UUID, stringValue : String, collectionValue : List[Int], genericValue: SomeGenericClass[String])
case class SomeGenericClass[T <: AnyRef](tValue : T)

class XStreamSerializerSpec extends SpecificationWithJUnit
{
    val sut = new XStreamSerializer

    "a serializer" should {
        "serialize and deserialize null" in {
            sut.deserialize[String](sut.serialize((null: String))) must beEqualTo(null: String)
        }

        "serialize and deserialize simple types" in {
            sut.deserialize[String](sut.serialize("abc")) must beEqualTo("abc")
        }

        "serialize object graphs with generics" in {
            val value = SomeObjectGraph(new UUID, "Hello World", List(-123000, 100000), SomeGenericClass("Generic Value"))
            sut.deserialize[SomeObjectGraph](sut.serialize(value)) must beEqualTo(value)
        }
    }
}
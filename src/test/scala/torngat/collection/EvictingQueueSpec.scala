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
package torngat.collection

import org.specs2.mutable._

class EvictingQueueSpec extends SpecificationWithJUnit
{
    "The evicting queue should" should {
        "throw an IllegalArgumentException when trying to build a negative capacity" in {
            new EvictingQueue[String](-1) must throwA[IllegalArgumentException](message = "The capacity can not be negative")
        }

        "return the enqueued value when the capacity is zero" in {
            new EvictingQueue[String](0).enqueue("Hello") must be equalTo(Some("Hello"))
        }

        "return None when it is not full yet" in {
            new EvictingQueue[String](3).enqueue("Hello") must be(None)
        }

        "return the first value when it is full" in {
            val sut = new EvictingQueue[String](1)
            sut.enqueue("Hello")

            sut.enqueue("World") must be equalTo(Some("Hello"))
        }

        "return the next value when the first one is already evicted" in {
            val sut = new EvictingQueue[String](2)
            sut.enqueue("To be")
            sut.enqueue("or")
            sut.enqueue("not to be")

            sut.enqueue("That is the question") must be equalTo(Some("or"))
        }
    }
}
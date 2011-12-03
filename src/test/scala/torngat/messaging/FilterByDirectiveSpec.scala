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
package messaging

import org.specs2.mutable._
import com.eaio.uuid.UUID

class FilterByDirectiveSpec extends SpecificationWithJUnit with MessageDirectives
{
    val msg = new Message { val header = MessageHeader(new UUID) }
    val ctx = MessageContext(msg) {}

    "filter by directive" should {
        "throw a NullPointerException when the inner route is null" in {
            filterBy(_ => true)(null) must throwA[NullPointerException](message = "innerRoute must not be null")
        }

        "throw a NullPointerException when the predicate is null" in {
            filterBy(null) {_ => () } must throwA[NullPointerException](message = "predicate must not be null")
        }

        "call its inner route if predicate is true" in {
            val sut = filterBy(_ => true) { _ =>
                throw new Exception("Message passed")
            }

            sut(ctx) must throwA[Exception](message = "Message passed")
        }

        "not call its inner route if predicate is false" in {
            val sut = filterBy(_ => false) { _ =>
                throw new Exception("Message passed")
            }

            sut(ctx)
            success
        }
    }

    "A filter with a chained route" should {
        "throw a NullPointerException when the second route is null" in {
            filterBy(_ => true) { _  => () } ~(null) must throwA[NullPointerException](message = "chainedRoute must not be null")
        }

        "call its first route if predicate is true" in {
            val sut = filterBy(_ => true) { _ =>
                throw new Exception("Message passed to first route")
            } ~ { _ =>
                throw new Exception("Message passed to second route")
            }

            sut(ctx) must throwA[Exception](message = "Message passed to first route")
        }

        "call its second route if predicate is false" in {
            val sut = filterBy(_ => false) { _ =>
                throw new Exception("Message passed to first route")
            } ~ { _ =>
                throw new Exception("Message passed to second route")
            }

            sut(ctx) must throwA[Exception](message = "Message passed to second route")
        }
    }
}
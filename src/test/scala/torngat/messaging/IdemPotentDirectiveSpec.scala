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

class IdemPotentDirectiveSpec extends SpecificationWithJUnit with MessageDirectives
{
    val msg1 = new Message { val header = MessageHeader(new UUID) }
    val ctx1 = MessageContext(msg1) {}
    val msg2 = new Message { val header = MessageHeader(new UUID) }
    val ctx2 = MessageContext(msg2) {}

    "idemPotent directive" should {
        "throw an IllegalArgumentException when a negative capacity is passed" in {
            idemPotent(-1) { _ => () } must throwA[IllegalArgumentException](message = "memoryCapacity must be a positive integer")
        }

        "throw a NullPointerException when the passed inner route is null" in {
            idemPotent(1)(null) must throwA[NullPointerException](message = "innerRoute must not be null")
        }

        "call its inner route for each first instance of a message" in {
            val sut = idemPotent(1) { _ =>
                throw new Exception("Message passed")
            }

            sut(ctx1) must throwA[Exception](message = "Message passed")
        }

        "not call its inner route for each first instance of a message" in {
            val sut = idemPotent(1) { _ =>
                throw new Exception("Message passed")
            }
            try { sut(ctx1) } catch { case _ => () }

            sut(ctx1)
            success
        }

        "call its inner route for a second instance of a message when its capacity is exceeded" in {
            val sut = idemPotent(1) { _ =>
                throw new Exception("Message passed")
            }
            try { sut(ctx1) } catch { case _ => () }
            try { sut(ctx2) } catch { case _ => () }

            sut(ctx1) must throwA[Exception](message = "Message passed")
        }
    }
}
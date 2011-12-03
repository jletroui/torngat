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

class AndOperatorSpec extends SpecificationWithJUnit with MessageDirectives
{
    val msg = new Message { val header = MessageHeader(new UUID) }
    val ctx = MessageContext(msg) { throw new Exception("Context has been acked")}

    "routes joined by an 'AND' operator" should {
        "throw a NullPointerException when the first one is null" in {
            (nullRoute & doNothingRoute) must throwA[NullPointerException](message = "routes must not be null")
        }

        "throw a NullPointerException when the second one is null" in {
            ((doNothingRoute _) & nullRoute) must throwA[NullPointerException](message = "routes must not be null")
        }

        "all be called" in {
            var tag = 0
            val sut =
                ((ctx : MessageContext) => tag |= 1) &
                ((ctx : MessageContext) => tag |= 2) &
                ((ctx : MessageContext) => tag |= 4)
            sut(ctx)

            tag must be equalTo(7)
        }

        "not ack when not all routes yet acked" in {
            val sut = (doNothingRoute _) & ackImmediatelyRoute

            sut(ctx)
            success
        }

        "ack when all routes acked" in {
            val sut = (ackImmediatelyRoute _) & ackImmediatelyRoute

            sut(ctx) must throwA[Exception](message = "Context has been acked")
        }

    }

    def ackImmediatelyRoute(ctx : MessageContext) { ctx.acknowledge() }
    def doNothingRoute(ctx : MessageContext) {}
    val nullRoute : MessageRoute = null
}
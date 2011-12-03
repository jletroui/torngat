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
package torngat.messaging

import org.specs2.mutable._
import com.eaio.uuid.UUID
import akka.dispatch.Future
import java.util.concurrent.atomic.AtomicInteger

class WithHistoryDirectiveSpec extends SpecificationWithJUnit with MessageDirectives
{
    val msg0 = new Message { val header = MessageHeader(new UUID) }
    val msg1 = new Message { val header = MessageHeader(new UUID) }
    val ctx = MessageContext(msg1) { throw new Exception("Context has been acked")}
    val historyProvider = new MessageHistoryProvider {
        def loadHistory(callback: (Message) => Unit) = Future {
            callback(msg0)
        }
    }

    "withHistory directive" should {
        "throw a NullPointerException when the given historyProvider is null" in {
            withHistory(null) must throwA[NullPointerException](message = "historyProvider must not be null")
        }

        "throw a NullPointerException when the given inner route is null" in {
            withHistory(historyProvider)(null) must throwA[NullPointerException](message = "innerRoute must not be null")
        }

        "pass the historic messages before passing on the first incoming message" in {
            val marker = new AtomicInteger(0)
            withHistory(historyProvider) { ctx => if (msg0 == ctx.msg) marker.incrementAndGet()}
            marker.get must equalTo(1)
        }

        "pass the messages normally after the history has been loaded" in {
            val sut = withHistory(historyProvider) { ctx =>
                if (msg1 == ctx.msg) throw new Exception("Message passed")
            }

            sut(ctx) must throwA[Exception](message = "Message passed")
        }
    }
}
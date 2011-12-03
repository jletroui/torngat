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
import java.util.concurrent.atomic.AtomicInteger
import akka.dispatch.Future
import akka.actor.Scheduler
import java.util.concurrent.TimeUnit

class LimitConcurrencyDirectiveSpec extends SpecificationWithJUnit with MessageDirectives with SpecificationWithActors
{
    val msg = new Message { val header = MessageHeader(new UUID) }

    "limitConcurrencyTo directive" should {
        "throw a IllegalArgumentException when the given limit is lesser than 1" in {
            limitConcurrencyTo(0) must throwA[IllegalArgumentException](message = "limit must be a strictly positive integer")
        }

        "throw a NullPointerException when the given inner route is null" in {
            limitConcurrencyTo(1)(null) must throwA[NullPointerException](message = "innerRoute must not be null")
        }

        "limit the number of simultaneous message treatment" in {
            val (executingCounter, lock, limit) = (new AtomicInteger(0), new Object, 2)
            var max = 0
            val sut = limitConcurrencyTo(limit) { ctx =>
                val currentlyExecuting = executingCounter.incrementAndGet()

                // Check if we are currently executing more than max at a time
                lock.synchronized {
                    if (currentlyExecuting > max) max = currentlyExecuting
                }

                // Introduce a bit of delay
                Scheduler.scheduleOnce({ () =>
                    executingCounter.decrementAndGet()
                    ctx.acknowledge()
                }, 5, TimeUnit.MILLISECONDS)
            }

            Future.traverse(0 until 25, 5000){_ =>
                val f = Future.empty[Unit](5000)
                sut(MessageContext(msg) {
                    f.completeWithResult(())
                })
                f
            }.await

            max must be_<=(limit)
        }
    }
}
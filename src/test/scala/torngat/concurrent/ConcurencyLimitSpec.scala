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
package concurrent

import org.specs2.mutable._
import akka.dispatch.Future
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit
import akka.actor.Scheduler

class ConcurencyLimitSpec extends SpecificationWithJUnit with SpecificationWithActors
{
    "a concurrency limit" should {
        "throw a IllegalArgumentException when the given limit is lower than 1" in {
            new ConcurrencyLimit(0) must throwA[IllegalArgumentException](message = "limit must be a strictly positive integer")
        }

        "should be initialized with a limit of 1 by default" in {
            new ConcurrencyLimit().limit must beEqualTo(1)
        }

        "limit the number of simultaneous processes" in {
            val limit = 2
            val (executingCounter, lock, sut) = (new AtomicInteger(0), new Object, new ConcurrencyLimit(limit))
            var max = 0

            Future.traverse(0 until 25, 5000){_ =>
                Future {
                    sut.throttle { complete =>
                        val currentlyExecuting = executingCounter.incrementAndGet()

                        // Check if we are currently executing more than max at a time
                        lock.synchronized {
                            if (currentlyExecuting > max) max = currentlyExecuting
                        }

                        // Introduce a bit of delay
                        Scheduler.scheduleOnce({ () =>
                            executingCounter.decrementAndGet()
                            complete()
                        }, 5, TimeUnit.MILLISECONDS)
                    }
                }
            }.await

            max must be_<=(limit)
        }
    }
}
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
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.TimeUnit
import akka.dispatch.Future
import java.util.Date

class RateLimitSpec extends SpecificationWithJUnit with SpecificationWithActors
{
    "a rate limit" should {
        "throw a NullPointerException when the given unit is null" in {
            new RateLimit(1, null) must throwA[NullPointerException](message = "unit must not be null")
        }

        "throw a IllegalArgumentException when the given rate is lower than 1" in {
            new RateLimit(0) must throwA[IllegalArgumentException](message = "maxRate must be a strictly positive integer")
        }

        "should be initialized with a limit of 1 by default" in {
            new RateLimit().maxRate must beEqualTo(1)
        }

        "should be initialized with SECONDS for the period unit by default" in {
            new RateLimit().unit must beEqualTo(TimeUnit.SECONDS)
        }

        "limit the number of processes executed within a period" in {
            val limit = 3
            val sut = new RateLimit(limit)

            val results = Future.traverse((0 until 7).toList, 5000) { i =>
                val f = Future.empty[Date](5000)
                sut.throttle {
                    f.completeWithResult(new Date())
                }
                f
            }.get

            val maxProcessesPerPeriod = results
                .map(_.getTime / 1000) // Gets the second each process executed at
                .groupBy(second => second) // Group by second
                .map(_._2.size) // Extract how many there was for each second
                .max

            maxProcessesPerPeriod must beEqualTo(limit)
        }
    }
}
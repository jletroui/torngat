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
import concurrent.RateLimit
import java.util.Date

class LimitRateDirectiveSpec extends SpecificationWithJUnit with MessageDirectives with SpecificationWithActors
{
    val msg = new Message { val header = MessageHeader(new UUID) }

    "limitRateTo directive" should {
        "throw a IllegalArgumentException when the given max rate is lesser than 1" in {
            limitRateTo(0) must throwA[IllegalArgumentException](message = "maxRate must be a strictly positive integer")
        }

        "throw a NullPointerException when the given unit is null" in {
            limitRateTo(1, null) must throwA[NullPointerException](message = "unit must not be null")
        }

        "throw a NullPointerException when the given inner route is null" in {
            limitRateTo(1)(null) must throwA[NullPointerException](message = "innerRoute must not be null")
        }

        "limit the number of processes executed within a period" in {
            val limit = 3
            val sut = limitRateTo(limit) { ctx =>
                ctx.acknowledge()
            }

            val results = Future.traverse((0 until 7).toList, 5000) { i =>
                val f = Future.empty[Date](5000)
                sut(MessageContext(msg) {
                    // Record the time it has been executed / completed
                    f.completeWithResult(new Date())
                })
                f
            }.get

            results
                .map(_.getTime / 1000) // Gets the second each process executed at
                .groupBy(second => second) // Group by second
                .map(_._2.size) // Extract how many there was for each second
                .max must beEqualTo(limit)
        }
    }
}
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

import org.specs2.mutable._
import java.util.concurrent.TimeUnit
import java.util.{Calendar, GregorianCalendar, Date}

class RichDateSpec extends SpecificationWithJUnit
{
    val cal = new GregorianCalendar(2011, 11, 21, 13, 24, 25)
    cal.set(Calendar.MILLISECOND, 26)
    val sut = cal.getTime

    "A rich date" should {
        "reject unsupported units when computing the start of a period" in {
            sut.startOfCurrentPeriod(TimeUnit.MILLISECONDS) must throwA[IllegalArgumentException](message = "unit must be SECONDS, MINUTES, or HOURS")
        }

        "compute the start of a period for hours" in {
            val expected = new GregorianCalendar(2011, 11, 21, 13, 0, 0).getTime.getTime

            sut.startOfCurrentPeriod(TimeUnit.HOURS) must beEqualTo(expected)
        }

        "compute the start of a period for minutes" in {
            val expected = new GregorianCalendar(2011, 11, 21, 13, 24, 0).getTime.getTime

            sut.startOfCurrentPeriod(TimeUnit.MINUTES) must beEqualTo(expected)
        }

        "compute the start of a period for seconds" in {
            val expected = new GregorianCalendar(2011, 11, 21, 13, 24, 25).getTime.getTime

            sut.startOfCurrentPeriod(TimeUnit.SECONDS) must beEqualTo(expected)
        }

        "reject unsupported units when computing the remaining time of a period" in {
            sut.remainingTimeUntilNextPeriod(TimeUnit.MILLISECONDS) must throwA[IllegalArgumentException](message = "unit must be SECONDS, MINUTES, or HOURS")
        }

        "compute the remaining time of a period for hours" in {
            val expected = (60 * 60 * 1000) - (26 + 25 * 1000 + 24 * 60 * 1000)

            sut.remainingTimeUntilNextPeriod(TimeUnit.HOURS) must beEqualTo(expected)
        }

        "compute the remaining time of a period for minutes" in {
            val expected = (60 * 1000) - (26 + 25 * 1000)

            sut.remainingTimeUntilNextPeriod(TimeUnit.MINUTES) must beEqualTo(expected)
        }

        "compute the remaining time of a period for seconds" in {
            val expected = (1000) - (26)

            sut.remainingTimeUntilNextPeriod(TimeUnit.SECONDS) must beEqualTo(expected)
        }
    }
}
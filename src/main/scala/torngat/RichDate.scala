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

import java.util.Date
import java.util.concurrent.TimeUnit

class RichDate(enriched: Date)
{
    import RichDate._

    /**
     * Gets the number of milliseconds of the enriched date, floored to the nearest period start of the given unit
     */
    def startOfCurrentPeriod(periodUnit: TimeUnit) = enriched.getTime - (enriched.getTime % intervalFor(periodUnit))

    /**
     * Gets the number of milliseconds remaining until the end of the current period of the given unit for the enriched date
     */
    def remainingTimeUntilNextPeriod(periodUnit: TimeUnit) =
    {
        val interval = intervalFor(periodUnit)
        interval - (enriched.getTime % interval)
    }
}

object RichDate
{
    private [RichDate] val SECOND = 1000L
    private [RichDate] val MINUTE = 60 * SECOND
    private [RichDate] val HOUR = 60 * MINUTE
    private [RichDate] val intervalMap = Map[TimeUnit, Long](
        TimeUnit.SECONDS -> SECOND,
        TimeUnit.MINUTES -> MINUTE,
        TimeUnit.HOURS -> HOUR)

    private [RichDate] def intervalFor(unit: TimeUnit) =
    {
        if (!intervalMap.contains(unit)) throw new IllegalArgumentException("unit must be SECONDS, MINUTES, or HOURS")
        intervalMap(unit)
    }
}
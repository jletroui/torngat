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

import java.util.concurrent.TimeUnit
import concurrent.RateLimit

private [messaging] class RateLimitFilter(maxRate : Int, unit : TimeUnit) extends (MessageRoute => MessageRoute)
{
    maxRate.assertStrictlyPositive("maxRate")
    unit.assertNotNull("unit")

    def apply(innerRoute: MessageRoute) = new MessageRoute
    {
        innerRoute.assertNotNull("innerRoute")

        private val rateLimit = new RateLimit(maxRate, unit)

        def apply(ctx: MessageContext)
        {
            rateLimit.throttle {
                innerRoute(ctx)
            }
        }
    }
}
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

import java.util.concurrent.atomic.AtomicInteger

class RichMessageRoute(enriched: MessageRoute)
{
    /**
     * Allows to chain routes together. All the routes will be called, and the message will be acknowledged when all routes would have acknowledged.
     */
    def &(otherRoute : MessageRoute) : MessageRoute = {
        if (enriched.isInstanceOf[FanoutRoute]) enriched.asInstanceOf[FanoutRoute].append(otherRoute)
        else if (otherRoute.isInstanceOf[FanoutRoute]) otherRoute.asInstanceOf[FanoutRoute].insert(enriched)
        else new FanoutRoute(enriched, otherRoute)
    }

    private class FanoutRoute(routes: MessageRoute*) extends MessageRoute
    {
        if (routes.size < 1) throw new IllegalArgumentException("There must be at least one route")
        routes.find(_ == null).foreach(_.assertNotNull("routes"))

        def apply(ctx : MessageContext)
        {
            val counter = new AtomicInteger(routes.size)
            routes.foreach { route =>
                route(ctx.withNewAck {
                    if (0 == counter.decrementAndGet()) ctx.acknowledge()
                })
            }
        }

        def append(otherRoute: MessageRoute) = new FanoutRoute((routes :+ otherRoute) : _*)
        def insert(otherRoute: MessageRoute) = new FanoutRoute((otherRoute +: routes) : _*)
    }
}

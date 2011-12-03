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

import collection.EvictingQueue
import com.eaio.uuid.UUID
import scala.collection.immutable.HashSet

private [messaging] class IdemPotentFilter(private val memoryCapacity : Int = 100000) extends (MessageRoute => MessageRoute)
{
    memoryCapacity.assertPositive("memoryCapacity")

    def apply(innerRoute : MessageRoute) = new MessageRoute
    {
        innerRoute.assertNotNull("innerRoute")

        private val queue = new EvictingQueue[UUID](memoryCapacity)
        private var set = new HashSet[UUID]

        def apply(ctx : MessageContext)
        {
            if (ctx.msg != null && !set.contains(ctx.msg.header.id)) {
                set = set + ctx.msg.header.id
                queue.enqueue(ctx.msg.header.id).foreach { evicted =>
                    set = set - evicted
                }

                innerRoute(ctx)
            }
            else
                ctx.acknowledge()
        }
    }
}

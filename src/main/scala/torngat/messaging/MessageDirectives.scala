package torngat
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
package messaging

import akka.actor.ActorRef
import java.util.concurrent.TimeUnit

/**
 * Message route building directives
 */
trait MessageDirectives
{
    /**
     * Selectively pass messages to its inner route
     */
    def filterBy(predicate: Message => Boolean) = new PredicateFilter(predicate)

    /**
     * Pass messages of the given type to the inner route
     */
    def isInstance[A: Manifest] = new TypeFilter[A]

    /**
     * Pass messages only once with the default memory capacity of 100 000 message ids
     */
    def idemPotent: (MessageRoute => MessageRoute) = new IdemPotentFilter()
    /**
     * Pass messages only once using a given memory capacity
     * @param memoryCapacity the maximum number of message ids to remember. You should tune this number depending on the volume of messages you are treating between 2 possible duplicates.
     */
    def idemPotent(memoryCapacity: Int): (MessageRoute => MessageRoute) = new IdemPotentFilter(memoryCapacity)

    /**
     * Delegates this message context to an actor
     */
    def toActor(actor : => ActorRef) : MessageRoute =
    {
        actor.assertNotNull("actor")
        (ctx : MessageContext) => actor ! ctx
    }

    /**
     * Pass all the historic messages to the inner route before passing on the incoming new messages.
     * Usually best used in combination with an idem potent directive.
     */
    def withHistory(historyProvider: MessageHistoryProvider): (MessageRoute => MessageRoute) = new HistoryFilter(historyProvider)

    /**
     * Allows only limit messages to be processed at the same time.
     * This is useful for expensive message treatment that are requiring a lot of memory for example.
     */
    def limitConcurrencyTo(limit: Int): (MessageRoute => MessageRoute) = new ConcurrencyLimitFilter(limit)

    /**
     * Allows only maxRate messages by unit of time to be processed.
     * This is useful for message treatment that requires access to a rate limited API like facebook for example.
     */
    def limitRateTo(maxRate : Int = 1, unit : TimeUnit = TimeUnit.SECONDS): (MessageRoute => MessageRoute) = new RateLimitFilter(maxRate, unit)
}


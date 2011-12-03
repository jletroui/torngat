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
import akka.actor.Actor
import akka.dispatch.Future

class ToActorDirectiveSpec extends SpecificationWithJUnit with MessageDirectives with SpecificationWithActors
{
    val msg = new Message { val header = MessageHeader(new UUID) }
    val ctx = MessageContext(msg) {}

    "toActor directive" should {
        "throw a NullPointerException if the given actor is null" in {
            toActor(null) must throwA[NullPointerException](message = "actor must not be null")
        }

        "delegate the message treatment to the actor" in {
            val future = Future.empty[Message]()
            val actor = Actor.actorOf(new Actor {
                protected def receive =
                {
                    case MessageContext(m, ack) => future.completeWithResult(m)
                }
            }).start()
            val sut = toActor(actor)

            sut(ctx)
            future.get must be(msg)
        }
    }
}
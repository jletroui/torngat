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

import akka.amqp._
import akka.amqp.AMQP._
import akka.actor.{ActorRef, Actor}

/**
 * Consumes messages coming from an AMQP queue using the given message route
 */
class AMQPConsumer(val route : MessageRoute, val serializer: Serializer) extends Actor
{
    protected def receive =
    {
        case Delivery(payload, _, deliveryTag, _, props, sender) =>
            val msg = serializer.deserialize[Message](props.getContentType, payload)
            route(MessageContext(msg) {
                sender.foreach(_ ! Acknowledge(deliveryTag))
            })
        case Acknowledged(_) => ()
    }
}
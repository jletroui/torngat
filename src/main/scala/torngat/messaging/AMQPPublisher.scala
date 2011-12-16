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

import com.rabbitmq.client.AMQP.BasicProperties
import akka.actor.{ActorRef, Actor}
import akka.amqp.{AMQP}
import utils.Logging

/**
 * Notification that is being sent to optional monitoring actor.
 */
case class PublisherPublishedMessage(msg: akka.amqp.Message)

/**
 * Published messages to an AMQP exchange
 */
class AMQPPublisher(serializer: Serializer,
                    connection: ActorRef,
                    exchange: AMQP.ExchangeParameters,
                    mandatory: Boolean = false,
                    immediate: Boolean = false,
                    routingKey : String = "",
                    monitoringActor: Option[ActorRef] = None) extends Actor with Logging
{
    private val amqpProducer = AMQP.newProducer(
        connection,
        AMQP.ProducerParameters(Some(exchange)))

    protected def receive =
    {
        case msg : Message =>
            try {

                val SerializedData(contentType, payload) = serializer.serialize(msg)
                val props = new BasicProperties.Builder().contentType(contentType).build()
                val amqpMessage = akka.amqp.Message(payload, routingKey, mandatory, immediate, Some(props))

                amqpProducer ! amqpMessage
                monitoringActor.foreach(_ ! PublisherPublishedMessage(amqpMessage))
            }
            catch {
                case e : Exception => log error(e, "Can not publish %s", msg.toString)
            }
    }
}
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
package torngat.examples

import akka.amqp.AMQP.ActiveDeclaration
import torngat.impl.XStreamSerializer
import com.rabbitmq.client.Address
import akka.amqp.{Fanout, AMQP}
import akka.config.Supervision.{SupervisorConfig, Permanent}
import torngat.messaging._
import com.eaio.uuid.UUID
import akka.actor.{Scheduler, ActorRef, SupervisorFactory, Actor}
import java.util.concurrent.TimeUnit

object HelloWorldExample extends MessageDirectives
{
    def main(args: Array[String])
    {
        val serializer = new XStreamSerializer

        val (exchangeName, queueName) = ("TorngatHelloWorld", "TorngatHelloWorld")
        val connection = AMQP.newConnection(AMQP.ConnectionParameters(
            Array(new Address("localhost")),
            "guest",
            "guest",
            "/"))
        val exchange =AMQP.ExchangeParameters(
            exchangeName,
            Fanout,
            AMQP.ActiveDeclaration(true, false, false))

        val publisher = Actor.actorOf(new AMQPPublisher(serializer, connection, exchange)).start()
        val consumer = Actor.actorOf(new AMQPConsumer(messageRoute _, serializer)).start()

        AMQP.newConsumer(connection, AMQP.ConsumerParameters("",
            consumer,
            Some(queueName),
            Some(exchange),
            selfAcknowledging=false,
            queueDeclaration = ActiveDeclaration(true, false, false)))

        sayHello(publisher)
    }

    def sayHello(publisher: ActorRef)
    {
        publisher ! PersonTalked(MessageHeader(new UUID), "John", "Hello World!")

        Scheduler.scheduleOnce(() => Actor.registry.shutdownAll(), 1, TimeUnit.SECONDS)
    }

    def messageRoute(ctx: MessageContext)
    {
        ctx.msg match {
            case PersonTalked(_, person, said) => println("%s said '%s'".format(person, said))
        }

        ctx.acknowledge()
    }
}
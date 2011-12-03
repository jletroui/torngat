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
package torngat.messaging

import com.eaio.uuid.UUID
import java.util.Date

/**
 * Base trait for messages passed by the underlying messaging system
 */
trait Message
{
    /**
     * Gets the message metadata
     */
    def header: MessageHeader
}

/**
 * Minimal metadata for a message
 */
case class MessageHeader(id: UUID, correlationId: UUID, date: Long, properties: Map[String, String])

object MessageHeader
{
    def apply(correlationId: UUID, properties: Map[String, String] = Map.empty[String, String]): MessageHeader = MessageHeader(new UUID, correlationId, new Date().getTime, properties)
}
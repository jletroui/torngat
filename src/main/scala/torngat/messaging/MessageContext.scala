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

/**
 * Message consumption context. Allow client to acknowledge the consumption of the given message.
 */
class MessageContext(val msg: Message, val acknowledge: () => Unit)
{
    def withNewAck(newAcknowledge: => Unit) = new MessageContext(msg, () => newAcknowledge)
}

object MessageContext
{
    def apply(msg: Message)(acknowledge: => Unit) = new MessageContext(msg, () => acknowledge)
    def unapply(ctx: MessageContext): Option[(Message, () => Unit)] = if (null == ctx) None else Some((ctx.msg, ctx.acknowledge))
}
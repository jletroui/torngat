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
import torngat.messaging.{RichMessageRoute, MessageContext}
import java.util.Date

package object torngat
{
    type MessageRoute = MessageContext => Unit
    type CompletableTask = (() => Unit) => Unit
    type Task = () => Unit

    implicit def toRichMessageRoute(enriched: MessageRoute) = new RichMessageRoute(enriched)
    implicit def toRichAnyRef(enriched: AnyRef) = new RichAnyRef(enriched)
    implicit def toRichDate(enriched: Date) = new RichDate(enriched)
    implicit def toRichInt(enriched: Int) = new RichInt(enriched)
}
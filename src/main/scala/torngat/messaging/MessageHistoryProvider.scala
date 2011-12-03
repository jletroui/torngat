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

import akka.dispatch.Future

/**
 * Implement this trait to be able to use the withHistory directive.
 */
trait MessageHistoryProvider
{
    /**
     * Loads history and call the callback for each message. Completes the future when all messages are loaded.
     * Messages must be loaded in chronological order of appearance within the history.
     */
    def loadHistory(callback: Message => Unit): Future[Unit]
}
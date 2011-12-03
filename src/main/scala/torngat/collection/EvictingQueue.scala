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
package torngat.collection

/**
 * A fixed size queue that is evicting the older items if it is full.
 */
class EvictingQueue[T: Manifest](private val capacity: Int)
{
    if (capacity < 0) throw new IllegalArgumentException("The capacity can not be negative")

    private var innerQueue = scala.collection.immutable.Queue.fill[Option[T]](capacity)(None)

    /**
     * Enqueue the given item, and return an element that have been evicted, if the queue is full.
     */
    def enqueue(item : T): Option[T] =
    {
        val (evicted, newQueue) = innerQueue.enqueue(Some(item)).dequeue
        innerQueue = newQueue
        evicted
    }

}
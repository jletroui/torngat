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

case class SerializedData(className : String, serialized : Array[Byte])

/**
 * Simple serializer trait.
 */
trait Serializer
{
    /**
     * Serializes the given object
     * @param obj the object to serialize
     */
    def serialize[T: Manifest](obj: T) : SerializedData

    /**
     * Deserializes the given byte array.
     * @param className the canonical name of the concrete class of the object that have been serialized into the given byte array
     * @param serialized the serialized payload
     */
    def deserialize[T: Manifest](className : String, serialized : Array[Byte]): T
    def deserialize[T: Manifest](data: SerializedData): T = deserialize[T](data.className, data.serialized)
}
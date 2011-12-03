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
package impl

import com.thoughtworks.xstream.XStream

/**
 * Basic serializer implementation based on the XStream library
 */
class XStreamSerializer extends Serializer
{
    private val xstream = new XStream

    def serialize[T: Manifest](obj: T) = SerializedData(if (obj == null) manifest[T].erasure.getCanonicalName else obj.getClass.getCanonicalName, xstream.toXML(obj).getBytes)

    def deserialize[T: Manifest](className: String, serialized: Array[Byte]) : T = xstream.fromXML(new String(serialized)).asInstanceOf[T]
}
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

import org.specs2.mutable._

class RichAnyRefSpec extends SpecificationWithJUnit
{
    "A rich AnyRef" should {
        "reject a null value when asserting non nullity" in {
            (null : AnyRef).assertNotNull("value") must throwA[NullPointerException](message = "value must not be null")
        }

        "not reject a defined value when asserting non nullity" in {
            "abc".assertNotNull("value")
            success
        }
    }
}
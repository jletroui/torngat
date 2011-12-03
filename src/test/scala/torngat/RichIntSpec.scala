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

class RichIntSpec extends SpecificationWithJUnit
{
    "A rich Int" should {
        "reject a stricktly negative value when asserting a positive value" in {
            -1.assertPositive("value") must throwA[IllegalArgumentException](message = "value must be a positive integer")
        }

        "not reject a positive value when asserting a positive value" in {
            1.assertPositive("value")
            success
        }

        "not reject 0 when asserting a positive value" in {
            0.assertPositive("value")
            success
        }

        "reject a negative value when asserting a strictly positive value" in {
            -1.assertStrictlyPositive("value") must throwA[IllegalArgumentException](message = "value must be a strictly positive integer")
        }

        "reject 0 when asserting a strictly positive value" in {
            0.assertStrictlyPositive("value") must throwA[IllegalArgumentException](message = "value must be a strictly positive integer")
        }

        "not reject a positive value when asserting a strictly positive value" in {
            1.assertStrictlyPositive("value")
            success
        }

    }
}
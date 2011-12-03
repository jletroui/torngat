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

class RichInt(enriched: Int)
{
    /**
     * Validates that the parameter with the given label is positive
     */
    def assertPositive(label: String)
    {
        if (enriched < 0) throw new IllegalArgumentException("%s must be a positive integer".format(label))
    }

    /**
     * Validates that the parameter with the given label is strictly positive
     */
    def assertStrictlyPositive(label: String)
    {
        if (enriched < 1) throw new IllegalArgumentException("%s must be a strictly positive integer".format(label))
    }
}
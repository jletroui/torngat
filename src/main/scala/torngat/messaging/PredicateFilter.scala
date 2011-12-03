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
package messaging

private [messaging] class PredicateFilter(val predicate : Message => Boolean) extends (MessageRoute => PredicateRoute)
{
    predicate.assertNotNull("predicate")

    def apply(innerRoute : MessageRoute) = new PredicateRoute(predicate, innerRoute)

    def ||(orFilter: PredicateFilter) = new PredicateFilter(msg => predicate(msg) || orFilter.predicate(msg))
    def &&(andFilter: PredicateFilter) = new PredicateFilter(msg => predicate(msg) && andFilter.predicate(msg))
}

private [messaging] class TypeFilter[A: Manifest] extends PredicateFilter(manifest[A].erasure.isInstance(_))

private [messaging] class PredicateRoute(private val predicate : Message => Boolean,
                                         private val innerRoute : MessageRoute) extends MessageRoute
{
    predicate.assertNotNull("predicate")
    innerRoute.assertNotNull("innerRoute")

    def apply(ctx : MessageContext)
    {
        if (predicate(ctx.msg)) innerRoute(ctx)
        else ctx.acknowledge()
    }

    def ~(chainedRoute : MessageRoute) = new PredicateWithChainedRoute(predicate, innerRoute, chainedRoute)
}

private [messaging] class PredicateWithChainedRoute(private val predicate : Message => Boolean,
                                                    private val innerRoute : MessageRoute,
                                                    private val chainedRoute : MessageRoute) extends PredicateRoute(predicate, innerRoute)
{
    chainedRoute.assertNotNull("chainedRoute")

    override def apply(ctx : MessageContext)
    {
        if (predicate(ctx.msg)) innerRoute(ctx)
        else chainedRoute(ctx)
    }
}

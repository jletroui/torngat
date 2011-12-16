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
package concurrent

import akka.dispatch.Future
import akka.config.Supervision.Permanent
import akka.actor.{ActorRef, Actor}

/**
 * Notification that is being sent to optional monitoring actor.
 */
case class ConcurrencyLimitStateChanged(queuedCount: Int, executingCount: Int)

/**
 * Allows to throttle tasks so that a maximum of limit are running at the same time.
 * It means tasks are responsible to signal when they are done.
 * Typical usage is to limit the number of simultaneous memory intensive tasks.
 */
class ConcurrencyLimit(val limit: Int = 1, monitoringActor: Option[ActorRef] = None)
{
    limit.assertStrictlyPositive("limit")

    private val actor = Actor.actorOf(new Actor {
        self.lifeCycle = Permanent

        private case class TaskExecuted(task: CompletableTask)

        private var waitingTasks = List.empty[CompletableTask]
        private var executingTasks = Set.empty[CompletableTask]

        protected def receive =
        {
            case t: CompletableTask =>
                if (executingTasks.size < limit) {
                    executingTasks = executingTasks + t
                    execute(t)
                }
                else
                    waitingTasks = waitingTasks :+ t

                updateMonitoring()
            case TaskExecuted(task) =>
                executingTasks = executingTasks - task
                waitingTasks match {
                    case t :: ts =>
                        waitingTasks = ts
                        execute(t)
                    case _ => ()
                }
                updateMonitoring()
        }

        private def updateMonitoring()
        {
            monitoringActor.foreach(_ ! ConcurrencyLimitStateChanged(waitingTasks.size, executingTasks.size))
        }

        private def execute(task : CompletableTask)
        {
            Future {
                task(() => self ! TaskExecuted(task))
            }
        }
    }).start()

    /**
     * Throttle the given task.
     */
    def throttle(task: CompletableTask)
    {
        actor ! task
    }
}
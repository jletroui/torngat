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

import java.util.concurrent.TimeUnit
import java.util.Date
import akka.actor.{Scheduler, Actor}
import akka.config.Supervision.Permanent
import akka.dispatch.Future

/**
 * Allows to throttle tasks so that a maximum number of them are executed by a given period.
 * Typical usage is to limit the number of calls to rate limited APIs.
 */
class RateLimit(val maxRate : Int = 1, val unit : TimeUnit = TimeUnit.SECONDS)
 {
     maxRate.assertStrictlyPositive("maxRate")
     unit.assertNotNull("unit")

    val actor = Actor.actorOf(new Actor {
        self.lifeCycle = Permanent

        private case object PeriodElapsed
        private case class PerPeriodCounter(periodStart : Long = new Date().startOfCurrentPeriod(unit), count : Int = 0)
        {
            def increment(task: Task, onOverLimit: () => Unit, onLimitReached: () => Unit): PerPeriodCounter =
            {
                val current = new Date().startOfCurrentPeriod(unit)
                if (current != periodStart) {
                    task()
                    PerPeriodCounter(current, 1)
                }
                else if (count < maxRate) {
                    task()
                    copy(count = count + 1)
                }
                else if (count == maxRate) {
                    onLimitReached()
                    onOverLimit()
                    copy(count = count + 1)
                }
                else {
                    onOverLimit()
                    copy(count = count + 1)
                }

            }
        }

        private var waitingTasks = List.empty[Task]
        private var counter = PerPeriodCounter(0)

        protected def receive =
        {
            case t: Task =>
                tryExecute(t)
            case PeriodElapsed =>
                counter = PerPeriodCounter()
                val toExecuteNow = waitingTasks
                waitingTasks = List.empty[Task]
                toExecuteNow.foreach(tryExecute _)
        }

        private def tryExecute(t : Task)
        {
            counter = counter.increment(
                () => execute(t),
                () => waitingTasks = waitingTasks :+ t,
                () => scheduleForNextPeriod())
        }

        private def execute(t: Task)
        {
            Future {
                try {
                    t()
                }
                catch {
                    case e: Exception => ()
                }
            }
        }

        private def scheduleForNextPeriod()
        {
            Scheduler.scheduleOnce(self,
                PeriodElapsed,
                new Date().remainingTimeUntilNextPeriod(unit),
                TimeUnit.MILLISECONDS)
        }
    }).start()

     /**
      * Throttle the given task within the given period.
      */
    def throttle(task: => Unit)
    {
        actor ! (() => task)
    }
}

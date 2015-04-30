/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.supertribe.schedulelimit;

import com.tomitribe.tribestream.governance.api.ApplicationLimit;
import com.tomitribe.tribestream.governance.api.Concurrent;

import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Path("concurrency")
@Singleton
@Lock(LockType.READ)
public class WorkConcurrencyLimit {

    /**
     * Timer service, injected by the EJB container
     */
    @Resource
    private TimerService timerService;

    /**
     * Thread-safe counter to tracke the number of times the @Timeout method is called
     */
    private final AtomicInteger timerCount = new AtomicInteger(0);

    /**
     * Starts the timer. The timer calls the @Timeout method every 500 ms.
     */
    @GET
    @Path("start")
    public void start() {
        timerCount.set(0);
        timerService.createTimer(500, 500, "testtimer");
    }

    /**
     * Stops the timer and returns the number of times the @Timeout method was called
     *
     * @return the number of times the @Timeout method was called
     */
    @GET
    @Path("stop")
    public int stop() {
        final Collection<Timer> timers = timerService.getTimers();
        for (final Timer timer : timers) {
            if ("testtimer".equals(timer.getInfo())) {
                timer.cancel();
            }
        }

        return timerCount.get();
    }

    /**
     * Simulatates some work being done. This method can only be called twice at once
     */
    @Timeout
    @AccessTimeout(0)
    @ApplicationLimit(concurrent = @Concurrent(limit = 2))
    public void doWork() {
        final int count = timerCount.incrementAndGet();

        System.out.println("Work item " + count + " starting");
        try {
            Thread.sleep(10000);
        } catch (final InterruptedException e) {
            // interrupted, not much we can do
        }

        System.out.println("Work item " + count + " complete");
    }
}

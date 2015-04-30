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

import org.apache.ziplock.maven.Mvn;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class RateLimitWindowTest {

    /**
     * Builds the .war file for Arquillian to deploy to Tribestream to test
     *
     * @return A .war file build in the same way as if a Maven build had been run on the project.
     * @throws Exception if an error occurs building or deploying the .war archive
     */
    @Deployment
    public static WebArchive war() throws Exception {
        return new Mvn.Builder()
                .name("work.war")
                .build(WebArchive.class);
    }

    @EJB
    private WorkRateLimit work;

    /**
     * Calls the @Timeout method 20 times via a timer. The method allows three invocations per minute, so we
     * check the number of calls that have been successful after 10 seconds.
     *
     * @throws Exception on error or test failure
     */
    @Test
    public void testRateLimitWindow() throws Exception {
        work.start();
        Thread.sleep(10000);
        final int successfulCalls = work.stop();
        Assert.assertEquals(3, successfulCalls);
    }

}

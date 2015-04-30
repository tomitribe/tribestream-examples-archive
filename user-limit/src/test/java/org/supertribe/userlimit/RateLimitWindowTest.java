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
package org.supertribe.userlimit;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.ziplock.maven.Mvn;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.util.IO;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;

@RunWith(Arquillian.class)
public class RateLimitWindowTest {

    private static final int HTTP_OK = 200;
    private static final int HTTP_LIMIT_EXCEEDED = 429;

    /**
     * Builds the .war file for Arquillian to deploy to Tribestream to test
     *
     * @return A .war file build in the same way as if a Maven build had been run on the project.
     * @throws Exception if an error occurs building or deploying the .war archive
     */
    @Deployment(testable = false)
    public static WebArchive war() throws Exception {
        return new Mvn.Builder()
                .name("colors.war")
                .build(WebArchive.class);
    }

    /**
     * Arquillian will boot an instance of Tribestream with a random port. The URL with the random port is injected
     * into this field.
     */
    @ArquillianResource
    private URL webapp;

    /**
     * Calls the /api/colors/preferred endpoint, and checks the HTTP status code and (optionally) response body match the
     * expected response code and string.
     *
     * @param username           Username to use for basic auth
     * @param password           Password to use for basic auth
     * @param expectedStatusCode The status code that the endpoint is expected to return. Typically this will be 200 (OK)
     *                           for calls within the limit, and 429 (limit exceeded) once the limit has been exceeded.
     * @param expectedBody       The expected response body. This can be null. If null is specified, the response body is not
     *                           checked.
     * @throws Exception when an error occurs
     */
    private void call(final String username, final String password, final int expectedStatusCode, final String expectedBody) throws Exception {
        final Response response = WebClient.create(webapp.toExternalForm(), username, password, null)
                .path("api/colors")
                .path("preferred")
                .get();

        Assert.assertEquals(expectedStatusCode, response.getStatus());

        if (expectedBody != null) {
            try (final InputStream is = InputStream.class.cast(response.getEntity())) {
                final String body = IO.slurp(is);
                Assert.assertEquals(expectedBody, body);
            }
        }
    }

    /**
     * This test checks the rate limiting on the endpoint by calling the endpoint 10 times sequentially in a loop,
     * checking the correct response body and status code (200) are returned. The 11th call is over the limit, and should
     * return a 429 error.
     *
     * @throws Exception when an error occurs or the test fails.
     */
    @Test
    public void testRateLimitWindow() throws Exception {

        for (int i = 0; i < 5; i++) {
            call("user1", "user1", HTTP_OK, "orange");
        }

        // rate limit should now have been exceeded
        call("user1", "user1", HTTP_LIMIT_EXCEEDED, null);

        for (int i = 0; i < 5; i++) {
            call("user2", "user2", HTTP_OK, "orange");
        }

        // rate limit should now have been exceeded
        call("user2", "user2", HTTP_LIMIT_EXCEEDED, null);

        // sleep for 12 seconds. The window should now have been reset and the resource should be available to consume
        // again.

        Thread.sleep(12000);
        call("user1", "user1", HTTP_OK, "orange");
        call("user2", "user2", HTTP_OK, "orange");
    }

}

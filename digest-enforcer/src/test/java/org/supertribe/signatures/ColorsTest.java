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
package org.supertribe.signatures;

import com.tomitribe.tribestream.security.signatures.Base64;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.ziplock.maven.Mvn;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.util.IO;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class ColorsTest {

    /**
     * Build the web archive to test. This adds in the KeystoreInitializer class from the test sources,
     * which would otherwise be excluded.
     *
     * @return The archive to deploy for the test
     * @throws Exception
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
     * Simple example calling a REST endpoint with a POST, adding a SHA digest header
     *
     * @throws Exception when an error occurs, or the test fails
     */
    @Test
    public void successPost() throws Exception {
        final String actual = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("preferred")
                .header("Digest", "SHA=" + digest("SHA1", "Hello"))
                .post("Hello", String.class);

        assertEquals("Hello", actual);
    }

    /**
     * Simple example demonstrating the server rejecting the request with a HTTP 400 error when an incorrect digest
     * is added
     *
     * @throws Exception when an error occurs of the test fails
     */
    @Test
    public void invalidSHA() throws Exception {
        final Response response = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("preferred")
                .header("Digest", "SHA=An invalid SHA")
                .post("Hello");

        assertEquals(400, response.getStatus());
    }

    /**
     * Demonstrates requesting a digest of the response payload. It is possible to request multiple digests with
     * different algorithms, each with a different priority.
     *
     * @throws Exception when an error occurs or the test fails
     */
    @Test
    public void testWantDigest() throws Exception {
        final Response response = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("preferred")
                .header("Digest", "SHA=" + digest("SHA1", "Hello"))
                .header("Want-Digest", "SHA;q=0.2,MD5;q=0.21,Unixsum;q=0.22,Unixcksum;q=0.23")
                .post("Hello");

        assertEquals("Hello", getResonseBody(response));
        final MultivaluedMap<String, Object> metadata = response.getMetadata();
        final List<Object> digestList = metadata.get("digest");
        for (final Object digest : digestList) {
            final String digestString = digest.toString();

            if (!digestString.contains("=")) {
                throw new IllegalArgumentException("Invalid digest response header: " + digestString);
            }

            final String algo = digestString.substring(0, digestString.indexOf("="));
            final String value = digestString.substring(digestString.indexOf("=") + 1);

            if ("unixcksum".equals(algo)) {
                final String expected = "2880899316";
                assertEquals(expected, value);
            } else if ("unixsum".equals(algo)) {
                final String expected = "8401";
                assertEquals(expected, value);
            } else if ("sha".equals(algo)) {
                final String expected = new String(Base64.encodeBase64(MessageDigest.getInstance("SHA1").digest("Hello".getBytes())));
                assertEquals(expected, value);
            } else if ("md5".equals(algo)) {
                final String expected = new String(Base64.encodeBase64(MessageDigest.getInstance("MD5").digest("Hello".getBytes())));
                assertEquals(expected, value);
            } else {
                throw new IllegalArgumentException("Unexpected algorithm: " + algo);
            }
        }
    }

    /**
     * Helper method to convert a response body to a string
     *
     * @param response Response from a JAX-RS call
     * @return The entire response body as a String.
     * @throws Exception when an error occurs
     */
    private String getResonseBody(final Response response) throws Exception {
        try (final InputStream is = (InputStream) response.getEntity()) {
            return IO.slurp(is);
        }
    }

    /**
     * Helper method to create a Base64 encoded digest of the payload using the algorithm specified
     *
     * @param algorithm the MessageDigest algorithm to use
     * @param payload   the payload to digest
     * @return a base64 encoded string of the digest
     * @throws Exception when an error occurs
     */
    private String digest(final String algorithm, final String payload) throws Exception {
        return new String(Base64.encodeBase64(MessageDigest.getInstance(algorithm).digest(payload.getBytes())));
    }
}

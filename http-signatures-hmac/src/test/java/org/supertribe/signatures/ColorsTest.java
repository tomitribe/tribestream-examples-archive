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

import com.tomitribe.tribestream.security.signatures.Signature;
import com.tomitribe.tribestream.security.signatures.Signer;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.ziplock.IO;
import org.apache.ziplock.maven.Mvn;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

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
                .build(WebArchive.class)
                .addClass(KeystoreInitializer.class);
    }

    /**
     * Arquillian will boot an instance of Tribestream with a random port. The URL with the random port is injected
     * into this field.
     */
    @ArquillianResource
    private URL webapp;

    /**
     * Tests accessing a signatures protected method with a GET request
     *
     * @throws Exception when test fails or an error occurs
     */
    @Test
    public void success() throws Exception {

        final String actual = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("preferred")
                .header("Authorization", sign("GET", "/colors/api/colors/preferred"))
                .get(String.class);

        assertEquals("orange", actual);
    }

    /**
     * Tests accessing a signatures protected method with a POST request
     *
     * @throws Exception when test fails or an error occurs
     */
    @Test
    public void successPost() throws Exception {
        final String actual = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("preferred")
                .header("Authorization", sign("POST", "/colors/api/colors/preferred"))
                .post("Hello", String.class);

        assertEquals("Hello", actual);
    }

    /**
     * Tests accessing a signatures protected method with a PUT request
     *
     * @throws Exception when test fails or an error occurs
     */
    @Test
    public void successPut() throws Exception {
        final String actual = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("preferred")
                .header("Authorization", sign("PUT", "/colors/api/colors/preferred"))
                .put("World", String.class);

        assertEquals("World", actual);
    }

    /**
     * Tests accessing a signatures protected method with a key that doesn't not have access to the resource
     *
     * @throws Exception when test fails or an error occurs
     */
    @Test
    public void fail() throws Exception {
        final Response response = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("refused")
                .header("Authorization", sign("GET", "/colors/api/colors/refused"))
                .get();
        assertEquals(403, response.getStatus());
    }

    /**
     * Tests accessing a signatures protected method with a GET request to a resource that requires a role
     *
     * @throws Exception when test fails or an error occurs
     */
    @Test
    public void authorized() throws Exception {
        final Response response = WebClient.create(webapp.toExternalForm())
                .path("api/colors")
                .path("authorized")
                .header("Authorization", sign("GET", "/colors/api/colors/authorized"))
                .get();
        assertEquals("you rock guys", IO.slurp(InputStream.class.cast(response.getEntity())));
    }

    /**
     * Create a digital signature using the HTTP method and request URI. This uses the shared secret constant
     * from the KeystoreInitializer
     *
     * @param method HTTP method for the request (e.g. GET, POST, PUT etc)
     * @param uri    The URI of the request, e.g. "/colors/api/colors/preferred"
     * @return The signature to set on the Authorization header.
     * @throws Exception
     */
    private Signature sign(final String method, final String uri) throws Exception {
        final Signature signature = new Signature(KeystoreInitializer.KEY_ALIAS, "hmac-sha256", null, "(request-target)");

        final Key key = new SecretKeySpec(KeystoreInitializer.SECRET.getBytes(), KeystoreInitializer.ALGO);
        final Signer signer = new Signer(key, signature);
        final Map<String, String> headers = new HashMap<>();
        return signer.sign(method, uri, headers);
    }
}

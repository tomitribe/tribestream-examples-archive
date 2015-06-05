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

import com.tomitribe.tribestream.security.signatures.store.StoreManager;
import org.apache.openejb.loader.SystemInstance;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;

@Singleton
@Startup
public class KeystoreInitializer {

    public static final String SECRET = "this is supposed to be the shared secret between client and server. " +
            "Not supposed to be in a constant.";

    public static final String KEY_ALIAS = "support";
    private static final String PWD = "this is sensible ;-)";
    public static final String ALGO = "HmacSHA256";
    private static File KS;

    /**
     * Initializes the keystore at ${TRIBESTREAM_HOME}/conf/test.jks, creating a keystore (with this is sensible ;-) as
     * the keystore password), with a secret key of "this is supposed to be the shared secret between client and server.
     * Not supposed to be in a constant." with an alias of "support".
     * <p/>
     * This is stored in the keystore with using the HmacSHA256 algorithm.
     * <p/>
     * This method is a @PostConstruct on a @Singleton @Startup EJB so the keystore is created as soon as the
     * application starts up. This works great for a test, but is not the recommended approach in production.
     *
     * @throws Exception if an error occurs.
     */
    @PostConstruct
    public void init() throws Exception {
        // init and generate a key
        final File conf = SystemInstance.get().getBase().getDirectory("conf");
        KS = new File(conf, "test.jks");
        StoreManager.get(KS.getAbsolutePath(), PWD.toCharArray(), true);
        StoreManager.get(KS.getAbsolutePath(), PWD.toCharArray()).addKey(KEY_ALIAS, PWD.toCharArray(), new SecretKeySpec(SECRET.getBytes(), ALGO));
    }
}

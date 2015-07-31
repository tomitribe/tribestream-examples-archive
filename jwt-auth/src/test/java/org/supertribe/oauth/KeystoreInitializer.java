/*
 * Tomitribe Confidential
 *
 * Copyright(c) Tomitribe Corporation. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package org.supertribe.oauth;

import com.tomitribe.tribestream.security.signatures.store.StoreManager;
import org.apache.openejb.loader.SystemInstance;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;

@Singleton
@Startup
public class KeystoreInitializer {

    private static final String PWD = "this is sensible ;-)";
    private static File KS;

    /**
     * Initializes the keystore at ${TRIBESTREAM_HOME}/conf/tag.jks, creating a keystore (with this is sensible ;-) as
     * the keystore password), and imports the TAG public certificate.
     *
     * This method is a @PostConstruct on a @Singleton @Startup EJB so the keystore is created as soon as the
     * application starts up. This works great for a test, but is not the recommended approach in production.
     *
     * @throws Exception if an error occurs.
     */
    @PostConstruct
    public void init() throws Exception {
        final File conf = SystemInstance.get().getBase().getDirectory("conf");
        KS = new File(conf, "tag.jks");
        StoreManager.get(KS.getAbsolutePath(), PWD.toCharArray(), true);
        final StoreManager storeManager = StoreManager.get(KS.getAbsolutePath(), PWD.toCharArray());
        storeManager.importPrivateKey("tag", PWD, "RSA", classLoader().getResourceAsStream("private.der"), classLoader().getResourceAsStream("cacert.pem"));
    }

    private ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}


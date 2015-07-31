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

import java.io.File;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TestKeyStoreSetup {

    public static final String PASSWORD = "password";
    public static final String PRIVATE_KEY_ALIAS = "tag-private";

    private static final TestKeyStoreSetup INSTANCE = new TestKeyStoreSetup();
    private StoreManager STORE_MANAGER;
    private File tempFile;

    private TestKeyStoreSetup() {

        try {
            tempFile = File.createTempFile("ks-", ".jks");
            STORE_MANAGER = StoreManager.get(tempFile.getAbsolutePath(), PASSWORD.toCharArray(), true);

            STORE_MANAGER.importPrivateKey(PRIVATE_KEY_ALIAS, PASSWORD, "RSA", getClass().getResourceAsStream("/private.der"), getClass().getResourceAsStream("/cacert.pem"));
        } catch (final Exception e) {
            // hopefully won't happen
        }
    }

    public Key getKey(final String alias) throws Exception {
        return STORE_MANAGER.getKey(alias, PASSWORD.toCharArray());
    }

    public static TestKeyStoreSetup getInstance() {
        return INSTANCE;
    }

    public PublicKey publicKey() throws Exception {
        return STORE_MANAGER.getCertificate(PRIVATE_KEY_ALIAS, PASSWORD.toCharArray()).getPublicKey();
    }

    public PrivateKey privateKey() throws Exception {
        return (PrivateKey) getKey(PRIVATE_KEY_ALIAS);
    }

    public String getKeyStoreFile() {
        return tempFile.getAbsolutePath();
    }
}

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

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import org.tomitribe.util.Base64;

import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class JWTUtil {

    private JWTUtil() {
        // prevent direct instantiation
    }

    public static String createValidJwtAccessToken() throws Exception {
        final RSAPrivateKey privateKey = (RSAPrivateKey) TestKeyStoreSetup.getInstance().privateKey();

        // Create RSA-signer with the private key
        final JWSSigner signer = new RSASSASigner(privateKey);
        final JWTClaimsSet claimsSet = createValidJwtClaimsSet();


        final JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        // TODO: set typ

        final SignedJWT signedJWT = new SignedJWT(
                header,
                claimsSet);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    private static JWTClaimsSet createValidJwtClaimsSet() {
        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        claimsSet.setExpirationTime(new Date(new Date().getTime() + 1800000));
        claimsSet.setIssuer("https://soa.tomitribe.com/soa-iag/oauth");
        claimsSet.setJWTID(UUID.randomUUID().toString());
        claimsSet.setIssueTime(new Date());
        claimsSet.setNotBeforeTime(new Date(new Date().getTime() - 2 * 60 * 1000));
        claimsSet.setClaim("idm-provider-access-token", new String(Base64.encodeBase64("this is an access token".getBytes())));
        claimsSet.setClaim("idm-provider-id", "something specific goes here");
        claimsSet.setClaim("partner-number", "1234567890");
        claimsSet.setClaim("partner-market-identifier", "12345678900");
        claimsSet.setClaim("role", "ruler of the known universe");
        claimsSet.setClaim("token-type", "access-token");

        final JSONObject tagInternal = new JSONObject();
        tagInternal.put("version", "1.0");
        tagInternal.put("grant-type", "access-token");
        tagInternal.put("jwt-access-token", UUID.randomUUID().toString());
        tagInternal.put("refresh-times", "1");
        tagInternal.put("grant-time-to-live", "1 day");

        claimsSet.setClaim("tag-internal", tagInternal);
        return claimsSet;
    }

    public static String createBadTypeJwtToken() throws Exception {
        final RSAPrivateKey privateKey = (RSAPrivateKey) TestKeyStoreSetup.getInstance().privateKey();

        // Create RSA-signer with the private key
        final JWSSigner signer = new RSASSASigner(privateKey);

        final JWTClaimsSet claimsSet = createValidJwtClaimsSet();
        claimsSet.setClaim("token-type", "bad");

        final JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        // TODO: set typ

        SignedJWT signedJWT = new SignedJWT(
                header,
                claimsSet);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public static String createExpiredJwtAccessToken() throws Exception {

        final Calendar oldDate = GregorianCalendar.getInstance();
        oldDate.set(Calendar.YEAR, 1980);
        oldDate.set(Calendar.MONTH, Calendar.APRIL);
        oldDate.set(Calendar.DATE, 21);

        final Calendar expiryDate = GregorianCalendar.getInstance();
        expiryDate.set(Calendar.YEAR, 1980);
        expiryDate.set(Calendar.MONTH, Calendar.APRIL);
        expiryDate.set(Calendar.DATE, 22);

        final RSAPrivateKey privateKey = (RSAPrivateKey) TestKeyStoreSetup.getInstance().privateKey();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(privateKey);

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        claimsSet.setExpirationTime(expiryDate.getTime());
        claimsSet.setIssuer("https://soa.tomitribe.com/soa-iag/oauth");
        claimsSet.setJWTID(UUID.randomUUID().toString());
        claimsSet.setIssueTime(oldDate.getTime());
        claimsSet.setNotBeforeTime(oldDate.getTime());
        claimsSet.setClaim("idm-provider-access-token", new String(Base64.encodeBase64("this is an access token".getBytes())));
        claimsSet.setClaim("idm-provider-id", "something specific goes here");
        claimsSet.setClaim("partner-number", "1234567890");
        claimsSet.setClaim("partner-market-identifier", "12345678900");
        claimsSet.setClaim("role", "ruler of the known universe");
        claimsSet.setClaim("token-type", "access-token");

        final JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        // TODO: set typ

        SignedJWT signedJWT = new SignedJWT(
                header,
                claimsSet);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

}

package org.supertribe.noauth;

import com.tomitribe.tribestream.examples.Claim;
import com.tomitribe.tribestream.examples.ClaimsLoad;
import javax.ejb.Singleton;
import java.util.HashMap;

/**
 * Singleton bean used for loading the hashmap of claims used by the Colors endpoint.
 */
@Singleton
public class ClaimStore {

    HashMap<String,Claim> claimsHashMap;

    public ClaimStore() {
        ClaimsLoad  cl = new ClaimsLoad();
        claimsHashMap = cl.getClaims();
    }

    public HashMap<String, Claim> getClaimsHashMap() {
        return claimsHashMap;
    }

    public void setClaimsHashMap(HashMap<String, Claim> claimsHashMap) {
        this.claimsHashMap = claimsHashMap;
    }

}

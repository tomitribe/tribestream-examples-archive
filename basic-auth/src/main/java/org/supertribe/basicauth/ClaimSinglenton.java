package org.supertribe.basicauth;

import com.tomitribe.tribestream.examples.Claim;
import com.tomitribe.tribestream.examples.ClaimsLoad;
import javax.ejb.Singleton;
import java.util.HashMap;

@Singleton
public class ClaimSinglenton {

    HashMap<String,Claim> claimsHashMap;

    public ClaimSinglenton() {
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

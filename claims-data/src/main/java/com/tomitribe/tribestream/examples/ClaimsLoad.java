package com.tomitribe.tribestream.examples;


import java.io.*;
import java.util.HashMap;

/**
 * Helper class to load the claim data from a cvs file hosted in the project jar.
 */
public class ClaimsLoad {


    public ClaimsLoad() {
    }

    /**
     * Read from the jar a CVS file containing a set of claims.
     * @return HashMap<String, Claim>
     */
    public HashMap<String, Claim> getClaims(){

        HashMap<String,Claim> claimsHashMap = new HashMap<String,Claim>();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("claims.cvs");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String lineStr = null;
        String line = "";
        String cvsSplitBy = ",";
        try {
            while((lineStr = in.readLine()) != null) {
                String[] claimLine = lineStr.split(cvsSplitBy);
                claimsHashMap.put(claimLine[0].toUpperCase(), new Claim(claimLine[0],claimLine[1]));
                //System.out.println("name " + claimLine[0] + " email" + claimLine[1] + "]");
            }
        } catch (IOException e) {
            System.out.println("File containing the claims can't be localted inside the jar.");
            e.printStackTrace();
        }


        return claimsHashMap;
    }
}

package com.tomitribe.tribestream.examples;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ClaimsLoad {


    public ClaimsLoad() {
    }

    public HashMap<String, Claim> getClaims(){
       HashMap<String,Claim> claimsHashMap = new HashMap<String,Claim>();
        String csvFile = "/Users/cesar/git/tribestream-examples/claims-data/src/main/resources/claims.json";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] claimLine = line.split(cvsSplitBy);
                claimsHashMap.put(claimLine[0].toUpperCase(), new Claim(claimLine[0],claimLine[1]));

                //System.out.println("name " + claimLine[0] + " email" + claimLine[1] + "]");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File with claims not found.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return claimsHashMap;
    }
}

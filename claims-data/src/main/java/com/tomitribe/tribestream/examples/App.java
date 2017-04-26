package com.tomitribe.tribestream.examples;

//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;


import java.util.HashMap;

public class App
{
    public static void main( String[] args )
    {

        ClaimsLoad cl = new ClaimsLoad();
        HashMap<String,Claim> claimsHashMap = cl.getClaims();

        for (Claim claim : claimsHashMap.values()) {
            System.out.println(claim.getName()+" - "+claim.getEmail());
        }

        System.out.println("Veronica email:"+claimsHashMap.get("Veronica").getEmail());

//        String csvFile = "/Users/cesar/git/tribestream-examples/claims-data/src/main/resources/claims.json";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//        try {
//
//            br = new BufferedReader(new FileReader(csvFile));
//            while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] country = line.split(cvsSplitBy);
//
//                System.out.println("name " + country[0] + " email" + country[1] + "]");
//
//            }
//
//        } catch (FileNotFoundException e) {
//            System.out.println("File with claims not found.");
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}

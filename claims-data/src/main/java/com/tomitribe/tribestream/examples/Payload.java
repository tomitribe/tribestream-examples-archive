package com.tomitribe.tribestream.examples;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="payload")
@XmlAccessorType(XmlAccessType.FIELD)
public class Payload {

    String claim_source_id;
    String grant_type;
    String username;
    String client_id;

    public Payload() {
    }

//    public Payload(String username) {
//        this.username = username;
//    }
//
//    public Payload(String claim_source_id, String grant_type, String client_id, String username) {
//        this.claim_source_id = claim_source_id;
//        this.grant_type = grant_type;
//        this.client_id = client_id;
//        this.username = username;
//    }

    public String getClaim_source_id() {
        return claim_source_id;
    }

    public void setClaim_source_id(String claim_source_id) {
        this.claim_source_id = claim_source_id;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

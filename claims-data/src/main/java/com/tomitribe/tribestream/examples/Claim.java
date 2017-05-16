package com.tomitribe.tribestream.examples;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for Claim.
 */
@XmlRootElement
public class Claim {
    String name;

    String email;

    public Claim() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Claim(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

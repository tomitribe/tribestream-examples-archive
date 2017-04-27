/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.supertribe.basicauth;

import com.tomitribe.tribestream.examples.Claim;
import com.tomitribe.tribestream.examples.Payload;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("colors")
@Singleton
@Lock(LockType.READ)
public class Colors {

    @Inject
    public ClaimSinglenton claimSinglenton;

    @GET
    @Path("preferred")
    public String preferred() {
        return "orange";
    }

    @POST
    @Path("preferred")
    public String preferredPost(final String c) {
        return c;
    }

    @PUT
    @Path("preferred")
    public String preferredPut(final String c) {
        return c;
    }

    @GET
    @Path("refused")
    @RolesAllowed("not usable role")
    public String refused() {
        throw new IllegalStateException("Should never reach this exception");
    }

    @GET
    @Path("authorized")
    @RolesAllowed("exploitation")
    public String onlyIfAllowed() {
        return "you rock guys";
    }

    @Path("claim")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML })
    public Response getClaim(Payload payload){
        HashMap<String,Claim> chm =claimSinglenton.getClaimsHashMap();

        //Managed responses based on usernames:
        switch (payload.getUsername().toUpperCase()){
            case "ALEXA":     return Response.status(500).entity("This is a managed 500 error.").build();
            case "DOLAN":     return Response.status(400).entity("This is a managed 400 error.").build();
            case "MACY":      return Response.status(300).entity("This is a managed 300 error.").build();
            case "RUBY":      return Response.status(200).entity("This is a 200 xml media type managed response.").type(MediaType.APPLICATION_XML).build();
            case "JAMES":     return Response.status(200).entity("This is a 200 html media type managed response.").type(MediaType.TEXT_HTML_TYPE).build();
            case "NAIDA":     return Response.status(200).entity("{\"ACCOUNT_EXIST\": 1}").build();
            case "JORDAN":    return Response.status(200).entity("{\"ACCOUNT_EXIST\": true}").build();
            case "SEPTEMBER": return Response.status(200).entity("{\"ACCOUNT_EXIST\": \"yes\"}").build();
            case "PRICE":     return Response.status(200).entity("notAJsonMap").build();
        }


        //return Response.ok(chm.get(payload.getUsername())).build();
        Claim claimObj = chm.get(payload.getUsername().toUpperCase());
        return Response.status(200).entity("{\"displayName\":\""+claimObj.getName()+"\",\"mail\":\""+claimObj.getEmail()+"\"}").build();

    }

    @Path("claim")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Claim getClaim(){
        HashMap<String,Claim> chm =claimSinglenton.getClaimsHashMap();
        return chm.get("Veronica");
    }

}

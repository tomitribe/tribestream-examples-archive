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
package org.supertribe.signatures;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("colors")
@Singleton
@Lock(LockType.READ)
public class Colors {

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
    @RolesAllowed("user")
    public String onlyIfAllowed() {
        return "you rock guys";
    }
}

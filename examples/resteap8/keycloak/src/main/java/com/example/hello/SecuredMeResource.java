package com.example.hello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("secured")
public class SecuredMeResource {
    
  @GET
  @Path("me")
  public String me() {

  }

}

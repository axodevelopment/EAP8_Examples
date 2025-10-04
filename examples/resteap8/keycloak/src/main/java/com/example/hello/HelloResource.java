package com.example.hello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("hello")
public class HelloResource {
    
    @GET
    public String sayHello() {
        return "Test 8.0.keycloak.a"
    }

    @GET
    @Path("me")
    public String me() {
        
    }

}
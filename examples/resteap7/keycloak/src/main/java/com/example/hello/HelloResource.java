package com.example.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("hello")
public class HelloResource {

    @GET
    public String sayHello() {
        return "Hello from EAP 7!";
    }
}
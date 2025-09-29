package com.example.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/public")
@Produces(MediaType.APPLICATION_JSON)
public class PublicEndpoint {

    @GET
    @Path("/hello")
    public Map<String, String> publicHello() {
        Map<String, String> response = new HashMap<>();

        response.put("message", "Public endpoint - OIDC - No Access Checked");

        return response;
    }

    @GET
    @Path("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();

        response.put("status", "UP");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return response;
    }
}
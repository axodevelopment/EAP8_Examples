package com.example.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.Map;

@Path("/secured")
@Produces(MediaType.APPLICATION_JSON)
public class SecuredEndpoint {

    @GET
    @Path("/user")
    @RolesAllowed("user")
    public Map<String, String> userOnly(@Context SecurityContext securityContext) {
        Map<String, String> response = new HashMap<>();

        response.put("message", "OIDC - User Role Access");
        response.put("user", securityContext.getUserPrincipal().getName());
        response.put("authenticated", String.valueOf(securityContext.getUserPrincipal() != null));

        return response;
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    public Map<String, String> adminOnly(@Context SecurityContext securityContext) {
        Map<String, String> response = new HashMap<>();
        
        response.put("message", "OIDC - Admin Role Access");
        response.put("user", securityContext.getUserPrincipal().getName());
        
        return response;
    }

    @GET
    @Path("/info")
    @RolesAllowed({"user", "admin"})
    public Map<String, Object> getUserInfo(@Context SecurityContext securityContext) {
        Map<String, Object> response = new HashMap<>();

        response.put("username", securityContext.getUserPrincipal().getName());
        response.put("isSecure", securityContext.isSecure());
        response.put("authScheme", securityContext.getAuthenticationScheme());
        
        response.put("isUserInUserRole", securityContext.isUserInRole("user"));
        response.put("isUserInAdminRole", securityContext.isUserInRole("admin"));

        return response;
    }
}
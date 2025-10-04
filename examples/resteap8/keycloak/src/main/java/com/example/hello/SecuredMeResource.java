package com.example.hello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("secured")
public class SecuredMeResource {

  @Context
  SecurityContext security;

  @Context
  HttpHeaders headers;

  @GET
  @Path("me")
  @Produces(MediaType.TEXT_PLAIN)
  public String me() {
    /*
     * Note about jwtutils in HelloResource.java
     */
    Map<String, Object> claims = JwtUtils.decodeBearerClaims(headers.getHeaderString("Authorization"));

    String preferred = JwtUtils.getOptionalStringClaim(claims, "preferred_username");
    String email     = JwtUtils.getOptionalStringClaim(claims, "email");
    String audience  = JwtUtils.joinAudience(claims.get("aud"));

    boolean isUser  = security.isUserInRole("user");
    boolean isAdmin = security.isUserInRole("admin");

    return "user=" + (preferred != null ? preferred :
            (security.getUserPrincipal() != null ? security.getUserPrincipal().getName() : "anonymous"))
        + ", email=" + (email != null ? email : "n/a")
        + ", aud=" + audience
        + ", inRole(user)=" + isUser
        + ", inRole(admin)=" + isAdmin;
  }
}

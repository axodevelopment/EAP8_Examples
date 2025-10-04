package com.example.hello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("hello")
public class HelloResource {

  @Context
  SecurityContext security;

  @Context
  HttpHeaders headers;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayHello() {
    return "Test 8.0.elytron.a.1";
  }

  @GET
  @Path("me")
  @Produces(MediaType.TEXT_PLAIN)
  public String me() {
    Map<String, Object> claims = JwtUtils.decodeBearerClaims(headers.getHeaderString("Authorization"));

    String user  = JwtUtils.getOptionalStringClaim(claims, "preferred_username");
    if (user == null) {
      user = (security.getUserPrincipal() != null) ? security.getUserPrincipal().getName() : "anonymous";
    }
    String email = JwtUtils.getOptionalStringClaim(claims, "email");

    /*
    Note all Jwt activities are pushed into the JwtUtils class.
    The point being is that while these features were available in keycloak adapter
      they are not directly available through oidc and ould be pulled from microprofile
     */
    List<String> realmRoles = JwtUtils.realmRoles(claims);

    /*
     * Note above
     */
    String audience = JwtUtils.joinAudience(claims.get("aud"));

    return "user=" + user
        + ", email=" + (email != null ? email : "n/a")
        + ", realmRoles=" + (realmRoles != null ? realmRoles : Collections.emptyList())
        + ", audience=" + audience;
  }
}

package com.example.hello;

import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;

@Path("secured/token-play")
public class TokenPlaygroundResource {

  @GET
  @Path("inspect")
  @Produces(MediaType.TEXT_PLAIN)
  public String inspect(@HeaderParam("Authorization") String authz) {
    
    if (authz == null || !authz.startsWith("Bearer ")) {
      return "No bearer token";
    }

    /*
     * Note about jwtutils in HelloResource.java
     */

    String raw = authz.substring("Bearer ".length()).trim();
    String[] parts = raw.split("\\.");

    if (parts.length < 2) return "Malformed JWT";

    JsonObject hdr = JwtUtils.decodeBase64UrlToJson(parts[0]);
    JsonObject pl  = JwtUtils.decodeBase64UrlToJson(parts[1]);

    String typ = hdr.getString("typ", "JWT");
    String alg = hdr.getString("alg", "unknown");
    String sub = pl.getString("sub", "n/a");
    long   iat = pl.containsKey("iat") && !pl.isNull("iat") ? pl.getJsonNumber("iat").longValue() : -1L;
    long   exp = pl.containsKey("exp") && !pl.isNull("exp") ? pl.getJsonNumber("exp").longValue() : -1L;

    return "typ=" + typ + ", alg=" + alg + ", sub=" + sub + ", iat=" + iat + ", exp=" + exp;
  }

  /**
   * DEMO-ONLY: mints a local JWT with an in-memory keypair.
   * This is NOT part of OIDC resource protection; it just shows signing.
   
  @GET
  @Path("mint-local-jwt")
  @Produces(MediaType.APPLICATION_JSON)
  public String mintLocalJwt() throws Exception {

    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair kp = kpg.generateKeyPair();

    Instant now = Instant.now();
    String signed = Jwt
        .issuer("demo-issuer")
        .subject("demo-user")
        .audience("eap8-api")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(300))
        .jws().keyId("demo-kid").sign(kp.getPrivate());

    return "{\"access_token\":\"" + signed + "\"," +
           "\"expires_in\":300," +
           "\"refresh_expires_in\":0," +
           "\"not-before-policy\":0," +
           "\"note\":\"locally signed demo jwt\"}";
  }*/
}

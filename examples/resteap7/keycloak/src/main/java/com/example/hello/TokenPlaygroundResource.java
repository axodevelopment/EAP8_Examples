package com.example.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.util.JsonSerialization;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Path("secured/token-play")
public class TokenPlaygroundResource {

  @Context SecurityContext security;

  @GET
  @Path("inspect")
  public String inspectBearer() throws Exception {
    String raw = ((org.keycloak.KeycloakPrincipal<?>) security.getUserPrincipal())
        .getKeycloakSecurityContext().getTokenString();
    try {
      JWSInput in = new JWSInput(raw);
      JsonWebToken jwt = JsonSerialization.readValue(in.getContent(), JsonWebToken.class);
      return "typ=" + in.getHeader().getType()
          + ", alg=" + in.getHeader().getAlgorithm()
          + ", sub=" + jwt.getSubject()
          + ", iat=" + jwt.getIssuedAt()
          + ", exp=" + jwt.getExpiration();
    } catch (JWSInputException e) {
      return "Bad token: " + e.getMessage();
    }
  }

  @GET
  @Path("mint-local-jwt")
  public String mintLocalJwt() throws Exception {
    KeyPair kp = generateRsaKeyPair();

    AccessToken tok = new AccessToken();
    tok.type("Bearer");
    tok.issuer("demo-issuer");
    tok.addAudience("eap8-api");
    tok.subject("demo-user");

    long now = System.currentTimeMillis() / 1000L;
    tok.iat(Long.valueOf(now));
    tok.exp(Long.valueOf(now + 300L));

    String signed = new JWSBuilder()
        .type("JWT")
        .kid("demo-kid")
        .jsonContent(tok)
        .rsa256(kp.getPrivate());

    AccessTokenResponse resp = new AccessTokenResponse();
    resp.setToken(signed);
    resp.setExpiresIn(300);
    resp.setOtherClaims("note", "locally signed demo jwt");

    return JsonSerialization.writeValueAsString(resp);
  }

  private static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    return kpg.generateKeyPair();
  }
}

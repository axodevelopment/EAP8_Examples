package com.example.hello;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Path("secured/token-play")
public class TokenPlagroundResource {
    
  @GET
  @Path("inspect")
  public String inspectBearer() throws Exception {

  }

  @GET
  @Path("mint-local-jwt")
  public String mintLocalJwt() throws Exception {

  }

  private static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);

    return kpg.generateKeyPair();
  }

}

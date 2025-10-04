package com.example.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

@Path("secured")
public class SecuredMeResource {

  @Context
  SecurityContext security;

  @GET
  @Path("me")
  public String me() {
    KeycloakPrincipal<?> kp = (KeycloakPrincipal<?>) security.getUserPrincipal();
    KeycloakSecurityContext ksc = kp.getKeycloakSecurityContext();
    AccessToken at = ksc.getToken();
    IDToken idt = ksc.getIdToken();

    return "user=" + at.getPreferredUsername()
        + ", email=" + at.getEmail()
        + ", realmRoles=" + at.getRealmAccess().getRoles()
        + ", clientRoles@" + ksc.getToken().getIssuedFor() + "="
        + ksc.getToken().getResourceAccess(ksc.getToken().getIssuedFor()).getRoles();
  }
}

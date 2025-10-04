package com.example.hello;

import javax.annotation.processing.Generated;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

@Path("hello")
public class HelloResource {

    @Context
    SecurityContext security;

    @GET
    public String sayHello() {
        return "Test 7.4.keycloak.a";
    }

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
                + ", audience=" + at.getAudience();
    }
}
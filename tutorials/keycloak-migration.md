# Basics and introduction to - JBoss EAP - Keycloak migration

## Overview of tutorial

This tutorial will give a high level overview of the EAP7.4 to EAP 8.0 migration of keycloak with examples, we will also explore the general config options what the values are and what they imply in behaviors when setting them.  This also includes details around keycloak adapter methods and Elytron equivalents.

## Table of Contents

Tutorial listing

1. [Prereqs](#prerequisites)
2. [Tutorial Breakouts](#tutorials)
3. [Reference Docs](#reference-docs)

---

## Prerequisites

Please review `pre-req.md` if you wish to follow the steps with a setup cluster.

Review configure/keycloak/keycloak-generic.cli

---

## Tutorial

This does require a functioning keycloak with appropriate jwt oidc implementation.



## Common functions

Confirm imports are in the war

```bash
jar tf target/hello-eap7-api-1.0.war
```

## Tests to run

Get a token

```bash
  TOKEN=$(curl -s -X POST \
    http://192.168.31.200:8080/realms/eap-apps/protocol/openid-connect/token \
    -d "client_id=eap8-api" \
    -d "username=testuser" \
    -d "password=password123" \
    -d "grant_type=password" | jq -r '.access_token')
```

Test some endpoints

```bash
curl -i http://localhost:8080/myapi/api/hello

curl -i http://localhost:8080/myapi/api/secured/me -H "Authorization: Bearer $TOKEN"
```

## Function Mapping (In progress not fully vetted)

Two projects in this attempt to describe the function mapping in practice.

- examples/resteap8/keycloak
- examples/resteap7/keycloak

within reasteap7 this uses the keycloak adapter for 7
reasteap8 uses jakarta eleytron components.


| Func | EAP 7.4 | EAP 8 (Jakarta) | EAP 8 + MicroProfile | Notes |
|--------------------|--------------------------------|------------------|-|-|
| OAuth2 Constants | `OAuth2Constants` | Literal strings | Literal strings | Things like `BEARER_TOKEN_TYPE` are just the literal "Bearer". Use the HTTP Authorization header ("Bearer <token>") |
| Keystore Utils | `KeystoreUtil` | `java.security.KeyStore` | `java.security.KeyStore` | `java.security.KeyStore`, `KeyPairGenerator`, `KeyFactory`, `KeyStore.Builder` |
| Build JWT | `JWSBuilder` | JJWT library | `io.smallrye.jwt.build.Jwt` | smallrye or MicroProfile have JWT |
| Parse JWT | `JWSInput` | Manual parsing | `@Inject JsonWebToken` | ... |
| Access Token | `AccessToken` | `OidcSecurityContext` | `JsonWebToken` | Read claims from JsonWebToken (preferred_username, email, aud, and realm_access.roles). Elytron OIDC client + MP JWT injects it when CDI enabled. |
| Token Response | `AccessTokenResponse` | Manual JSON parsing | Manual JSON parsing | .. |
| ID Token | `IDToken` | Claims in token | `JsonWebToken.getClaim()` | In OIDC, ID token is also a JWT. If you need ID-token-only claims, just read them from the JWT you received; Elytron authenticates requests using the access token |
| JSON Serialize | `JsonSerialization` | JSON-B or JSON-P | JSON-B or JSON-P | Jackson can allso be used |



---

Notes:

Authn & enforcement: In EAP 8, the Elytron OIDC client subsystem handles token validation and web-app protection (your web.xml with <auth-method>OIDC</auth-method>). You typically don’t parse/verify the JWT yourself for request authn—just inject `JsonWebToken` (MP JWT) to read claims you care about.

The above is more BEARER token flows rather then needing a complete ID so `AccessID` and `TokenID` are treated the same here but note if you select a full flow.

In the code examples I am not actually using MicroProfile or smallrye if you want to use MicroProfile and OIDC flow integrated in a single client there is some manual bridge work to be done as Elytron setup is expecting one or the other to be managing the flow.

---

Cert stores in EAP 8

```bash
/subsystem=elytron/key-store=aKS:add(path="/path/keystore.p12", type=PKCS12, credential-reference={clear-text="somepassword"})
```

```bash
/subsystem=elytron/key-manager=aKM:add(key-store=aKS, credential-reference={clear-text="somepassword"})
```

```bash
/subsystem=elytron/server-ssl-context=letsencryptSSL:add(key-manager=aKM, protocols=["TLSv1.3","TLSv1.2"])
```

```bash
/subsystem=undertow/server=default-server/https-listener=https:write-attribute(name=ssl-context, value=letsencryptSSL)
```
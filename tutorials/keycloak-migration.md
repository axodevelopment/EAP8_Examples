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
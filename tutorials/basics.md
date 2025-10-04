# Basics and introduction to - {{Tutorial}}

## Overview of tutorial

This tutorial will give a high level overview of the {{Tutotorial}}, we will also explore the general config options what the values are and what they imply in behaviors when setting them.

## Table of Contents

Tutorial listing

1. [Prereqs](#prerequisites)
2. [Tutorial Breakouts](#tutorials)
3. [Reference Docs](#reference-docs)

---

## Prerequisites

Please review `pre-req.md` if you wish to follow the steps with a setup cluster.

---

## Tutorial

...


EAP 8 with OIDC

TOKEN=$(curl -s -X POST \
  http://192.168.31.200:8080/realms/eap-apps/protocol/openid-connect/token \
  -d "client_id=eap8-api" \
  -d "username=testuser" \
  -d "password=password123" \
  -d "grant_type=password" | jq -r '.access_token')


  ...

  The goal here is straight forward the dockerfile will use JBoss EAP8 Installation manager to install an EAP8 standard profile install.

  Then we will use the Jboss cli to install Elytron (Realm) 

  Then an app can use jakarta standard packages for eap 8 config to use elytron 



no auth test

```bash
curl http://localhost:8080/myapp/api/public/hello
```

Fail test

```bash
curl -v http://localhost:8080/myapp/api/secured/hello
```

should get a 401


get token

```bash
TOKEN=$(curl -s -X POST \
  http://192.168.31.200:8080/realms/eap-apps/protocol/openid-connect/token \
  -d "client_id=eap8-api" \
  -d "username=testuser" \
  -d "password=password123" \
  -d "grant_type=password" | jq -r '.access_token')
```

check payload

```bash
echo $TOKEN | cut -d'.' -f2 | base64 -d 2>/dev/null | jq '.'
```



```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/myapp/api/secured/hello
```

If granted a Realm role of user you should get access

/admin should still denied

/info will work if either user or admin is gratned to testuser.

I needed to nuke my admin user

so in podman exec -it 

```bash
sed -i '/^admin=/d' /opt/eap/standalone/configuration/mgmt-users.properties
/opt/eap/bin/add-user.sh
```

```bash
TOKEN=$(curl -s -X POST \\n  http://192.168.31.200:8080/realms/eap-apps/protocol/openid-connect/token \\n  -d "client_id=eap8-api" \\n  -d "username=testuser" \\n  -d "password=password123" \\n  -d "grant_type=password" | jq -r '.access_token')
```

## What does each section mean?



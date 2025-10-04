## Pre requisites

The goal is to have following resources deployed:

- Keycloak
- Installation Managers (galleon based or not)
- Keycloak Adapter
- (TODO)

### Keycloak setup

I am using a bearer only strategy for these demos here is an example of the JWT I am producing in Keycloak.  Feel free to setup whatever approach you want but I'll go over my settings.

JWT header

```bash
{"alg":"RS256","typ":"JWT","kid":"iBsCMtSTUwu99m2_Y7Xzh3C7RGwsHK9BnY3Jy_Wb2Qo"}
```


```bash
{
  "iss": "http://192.168.31.200:8080/realms/eap-apps",
  "aud": ["eap8-api","account"],
  "azp": "eap8-api",
  "preferred_username": "testuser",
  "resource_access": {"eap8-api":{"roles":["user"]}}
}
```

A couple main points here.

I needed an audience mapper since in some tests I am ...

```bash
/subsystem=keycloak/secure-deployment=hello-eap7-api-1.0.war:write-attribute(name=verify-token-audience, value=true)
```

So to do this in Keycloak you need to do the following steps (RHBK):

- Client Scopes -> Create client scope
- name it something (aud-eap8-api)
- Save it
- Add a Mapper to it
- Client Scope -> aud-eap8-api -> call it someting
- Set Add to Access Token (on)
- Go to Clients -> your client you have made for this
- Add your aud mappaer to your Client Scopes as default

For `Realms` I created a seperate realm which is where the eap-apps-realm comes into play mine is called `eap-apps`

In there is where I created the 'Realm Role' -> 'user'.

This role is added to a user I crated called 'testuser'.


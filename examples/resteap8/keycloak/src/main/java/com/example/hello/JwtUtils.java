package com.example.hello;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64.Decoder;
import java.util.stream.Collectors;

/**
 * Minimal helpers for decoding a Bearer JWT and extracting common claims.
 * OIDC-generic helpers are grouped together; Keycloak-specific helpers are marked clearly.
 */
public final class JwtUtils {
  private JwtUtils() {}

  // ------------------ OIDC-generic helpers ------------------

  /** Decode the Authorization header ("Bearer XXX.YYY.ZZZ") into a claims map. */
  public static Map<String, Object> decodeBearerClaims(String authorizationHeader) {

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return Collections.emptyMap();
    }

    String raw = authorizationHeader.substring("Bearer ".length()).trim();
    String[] parts = raw.split("\\.");

    if (parts.length < 2) return Collections.emptyMap();

    JsonObject payload = decodeBase64UrlToJson(parts[1]);

    return toMap(payload);
  }

  public static JsonObject decodeBase64UrlToJson(String b64url) {
    Decoder dec = Base64.getUrlDecoder();
    byte[] bytes = dec.decode(pad(b64url));

    try (JsonReader r = Json.createReader(new StringReader(new String(bytes, StandardCharsets.UTF_8)))) {
      return r.readObject();
    }
  }


  public static Map<String, Object> toMap(JsonObject jo) {

    Map<String, Object> out = new LinkedHashMap<>();

    for (String k : jo.keySet()) {
      JsonValue v = jo.get(k);
      out.put(k, convert(v));
    }

    return out;
  }

  public static String getOptionalStringClaim(Map<String, Object> claims, String name) {
    Object v = claims.get(name);

    return (v instanceof String) ? (String) v : null;
  }

  public static String joinAudience(Object audClaim) {

    if (audClaim == null) return "";

    if (audClaim instanceof String s) return s;

    if (audClaim instanceof Collection<?> c) {
      return c.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    return audClaim.toString();
  }

  // ------------------ Keycloak-specific helpers ------------------
  /**
   * Keycloak puts realm roles under claim:
   *   realm_access: { roles: [ "user", "admin"... ] }
   * This extracts those into a List<String>.
   */
  public static List<String> realmRoles(Map<String, Object> claims) {

    Object ra = claims.get("realm_access");

    if (ra instanceof Map<?, ?> m) {

      Object roles = m.get("roles");

      if (roles instanceof Collection<?> c) {
        return c.stream().map(Object::toString).collect(Collectors.toList());
      }
    }

    return Collections.emptyList();
  }

  // ------------------ internal helpers ------------------

  private static Object convert(JsonValue v) {

    switch (v.getValueType()) {
      case STRING:
        return ((JsonString) v).getString();
      case NUMBER:
        JsonNumber n = (JsonNumber) v;

        return n.isIntegral() ? n.longValueExact() : n.doubleValue();
      case TRUE:  return Boolean.TRUE;
      case FALSE: return Boolean.FALSE;
      case NULL:  return null;
      case OBJECT:

        return toMap((JsonObject) v);
      case ARRAY:

        return ((jakarta.json.JsonArray) v).stream().map(JwtUtils::convert).collect(Collectors.toList());
      default:

        return null;
    }
  }

  private static String pad(String b64url) {
    int m = b64url.length() % 4;
    
    return m == 2 ? b64url + "==" : m == 3 ? b64url + "=" : b64url;
  }
}

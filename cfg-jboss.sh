#Work in progress
# I want this script to automatically configure the OIDC subsystem in EAP8
# using environment variables for the key parameters.
# This is a work in progress and not yet functional.

#!/usr/bin/env bash
set -euo pipefail

CONTROLLER="${CONTROLLER:-localhost:9990}"
CLI="${JBOSS_HOME:-/opt/eap}/bin/jboss-cli.sh"

: "${WAR_NAME:?WAR_NAME is required (e.g. secured-api.war)}"
: "${PROVIDER_URL:?PROVIDER_URL is required}"
: "${CLIENT_ID:?CLIENT_ID is required}"

CLI_TMP="$(mktemp)"
trap 'rm -f "$CLI_TMP"' EXIT

cat > "$CLI_TMP" <<'EOF'
if (outcome != success) of /extension=org.wildfly.extension.elytron-oidc-client:read-resource
  /extension=org.wildfly.extension.elytron-oidc-client:add()
end-if

if (outcome != success) of /subsystem=elytron-oidc-client:read-resource
  /subsystem=elytron-oidc-client:add()
end-if
EOF

"$CLI" --connect --controller="$CONTROLLER" \
  --timeout=60000 \
  --command="batch" \
  --file="$CLI_TMP" \
  --command="run-batch" || true

# need to testt this more
NEEDS_RELOAD=$("$CLI" --connect --controller="$CONTROLLER" --commands=":read-attribute(name=server-state)" 2>/dev/null | grep -q running && \
              "$CLI" --connect --controller="$CONTROLLER" --commands=":read-resource(recursive=false, include-runtime=true)" 2>/dev/null | grep -q 'restart-required' && echo yes || echo no)

if [ "$NEEDS_RELOAD" = "yes" ]; then
  echo "Reload required – performing graceful reload"
  "$CLI" --connect --controller="$CONTROLLER" --commands=":reload"
else
  echo "No reload required"
fi

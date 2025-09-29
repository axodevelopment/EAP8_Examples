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

Review configure/keycloak/keycloak-generic.cli

---

## Tutorial

App repo (GitHub): the code. No OIDC files.

Security repo: web-fragment.jar, CLI script(s), truststores (or build instructions).

Pipeline:

- Build app WAR
- Overlay web-fragment.jar into WEB-INF/lib/ (or mount via ConfigMap in OpenShift)
- Deploy WAR to EAP (copy to standalone/deployments/)
- Run configure-oidc.sh (online) with env-specific values
- If server reports “reload required”, perform graceful :reload (zero code change)
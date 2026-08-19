#!/usr/bin/env bash
set -euo pipefail

SECRETS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/secrets"
mkdir -p "$SECRETS_DIR"
chmod 700 "$SECRETS_DIR"

generate_if_absent() {
  local file="$SECRETS_DIR/$1"
  local value="$2"
  if [ -f "$file" ]; then
    echo "[SKIP] $1 existe deja"
  else
    printf '%s' "$value" > "$file"
    chmod 600 "$file"
    echo "[OK]   $1 genere"
  fi
}

random_alnum() {
  LC_ALL=C tr -dc 'A-Za-z0-9' < /dev/urandom | head -c "$1"
}

generate_if_absent "postgres_user.txt" "wayfarer_app"
generate_if_absent "postgres_password.txt" "$(random_alnum 32)"
generate_if_absent "prometheus_user.txt" "prometheus"
generate_if_absent "prometheus_password.txt" "$(random_alnum 32)"
generate_if_absent "grafana_admin_password.txt" "$(random_alnum 32)"
generate_if_absent "jwt_secret.txt" "$(openssl rand -base64 48 | tr -d '\n')"

echo "Secrets disponibles dans $SECRETS_DIR"
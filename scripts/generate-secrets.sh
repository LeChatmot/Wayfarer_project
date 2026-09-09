#!/bin/sh
set -eu pipefail

SECRETS_DIR="./secrets"

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

gen_pg_username() {
  local prefix="${1:-app}"
  local len="${2:-8}"

  local first_char=$(tr -dc 'a-z' </dev/urandom | head -c1)
  local rest=$(tr -dc 'a-z0-9_' </dev/urandom | head -c "$len")

  local name="${prefix}_${first_char}${rest}"
  echo "${name:0:63}"
}

random_alnum() {
  LC_ALL=C tr -dc 'A-Za-z0-9' < /dev/urandom | head -c "$1"
}

generate_if_absent "postgres_user.txt" "$(gen_pg_username "wayfarer_postgres" 15)"
generate_if_absent "postgres_password.txt" "$(random_alnum 32)"
generate_if_absent "prometheus_user.txt" "prometheus"
generate_if_absent "prometheus_password.txt" "$(random_alnum 32)"
generate_if_absent "grafana_admin_password.txt" "$(random_alnum 32)"
generate_if_absent "jwt_secret.txt" "$(openssl rand -base64 48 | tr -d '\n')"
generate_if_absent "encryption_key.txt" "$(openssl rand -base64 32 | tr -d '\n')"
generate_if_absent "hash_key.txt" "$(openssl rand -base64 32 | tr -d '\n')"

echo "Secrets disponibles dans $SECRETS_DIR"
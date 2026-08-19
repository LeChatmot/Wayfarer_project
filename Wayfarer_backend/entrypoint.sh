#!/bin/bash
set -euo pipefail

read_secret() {
    local secret_path="$1"
    if [ -f "$secret_path" ]; then
        cat "$secret_path"
    else
        echo ""
    fi
}

export POSTGRES_USER=$(read_secret "/run/secrets/postgres_user")
export POSTGRES_PASSWORD=$(read_secret "/run/secrets/postgres_password")
export PROMETHEUS_USERNAME=$(read_secret "/run/secrets/prometheus_user")
export PROMETHEUS_PASSWORD=$(read_secret "/run/secrets/prometheus_password")

exec java -jar app.jar
#!/bin/sh
set -eu
POSTGRES_USER="$(cat /run/secrets/postgres_user)"
POSTGRES_PASSWORD="$(cat /run/secrets/postgres_password)"
export DATA_SOURCE_NAME="postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@postgres:5432/wayfarer?sslmode=disable"
exec postgres_exporter "$@"
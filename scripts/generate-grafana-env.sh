#!/bin/sh
set -eu
POSTGRES_USER="$(tr -d '\r\n' < ./secrets/postgres_user.txt)"
POSTGRES_PASSWORD="$(tr -d '\r\n' < ./secrets/postgres_password.txt)"
PROMETHEUS_USER="$(tr -d '\r\n' < ./secrets/prometheus_user.txt)"
PROMETHEUS_PASSWORD="$(tr -d '\r\n' < ./secrets/prometheus_password.txt)"
GF_SECURITY_ADMIN_PASSWORD="$(tr -d '\r\n' < ./secrets/grafana_admin_password.txt)"
umask 077
{
  printf 'GRAFANA_POSTGRES_USER=%s\n' "${POSTGRES_USER}"
  printf 'GRAFANA_POSTGRES_PASSWORD=%s\n' "${POSTGRES_PASSWORD}"
  printf 'GRAFANA_PROMETHEUS_USER=%s\n' "${PROMETHEUS_USER}"
  printf 'GRAFANA_PROMETHEUS_PASSWORD=%s\n' "${PROMETHEUS_PASSWORD}"
  printf 'GF_SECURITY_ADMIN_PASSWORD=%s\n' "${GF_SECURITY_ADMIN_PASSWORD}"
} > ./.env.grafana
echo "Fichier .env.grafana généré."
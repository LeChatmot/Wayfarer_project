#!/bin/bash

set -euo pipefail

MIGRATION_DIR="src/main/resources/db/migration"

if [ ! -d "$MIGRATION_DIR" ]; then
    echo "Erreur : le dossier $MIGRATION_DIR n'existe pas."
    exit 1
fi

find_last_version() {
    local last_version="0.0"
    local highest_major=0
    local highest_minor=0

    for file in "$MIGRATION_DIR"/V*.sql; do
        [ -e "$file" ] || continue

        filename=$(basename "$file")

        if [[ "$filename" =~ ^V([0-9]+)\.([0-9]+)__ ]]; then
            major="${BASH_REMATCH[1]}"
            minor="${BASH_REMATCH[2]}"

            if [ "$major" -gt "$highest_major" ] || { [ "$major" -eq "$highest_major" ] && [ "$minor" -gt "$highest_minor" ]; }; then
                highest_major=$major
                highest_minor=$minor
            fi
        fi
    done

    echo "${highest_major}.${highest_minor}"
}

LAST_VERSION=$(find_last_version)
MAJOR=$(echo "$LAST_VERSION" | cut -d'.' -f1)
MINOR=$(echo "$LAST_VERSION" | cut -d'.' -f2)

NEW_MINOR=$((MINOR + 1))
NEW_VERSION="${MAJOR}.${NEW_MINOR}"

TIMESTAMP=$(date +"%Y%m%d%H%M%S")

PLACEHOLDER="xxxxxxxxxx"

FILENAME="V${NEW_VERSION}__${TIMESTAMP}_${PLACEHOLDER}.sql"
FILEPATH="${MIGRATION_DIR}/${FILENAME}"

touch "$FILEPATH"

echo "Fichier de migration créé : $FILEPATH"
echo "N'oublie pas de renommer le fichier pour remplacer '${PLACEHOLDER}' par une description en snake_case."
#!/usr/bin/env bash

set -e

# shellcheck source=./func.sh
source "$(dirname "$0")/func.sh"

SCRPT="[scripts/set-release-version.sh]"

echo "$SCRPT Setting the release version."
./mvnw build-helper:parse-version \
       versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} \
       versions:commit

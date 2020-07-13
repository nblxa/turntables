#!/usr/bin/env bash

set -e

# shellcheck source=./func.sh
source "$(dirname "$0")/func.sh"

RELEASE_PROFILE=""
SCRPT="[scripts/build.sh]"

if [ "$PULL_REQUEST" == "false" ] && [[ "$BRANCH" == master ]]; then
  echo "$SCRPT Building a new release."
  RELEASE_PROFILE="-Prelease --settings scripts/release-settings.xml"
fi

if [ "$PULL_REQUEST" == "true" ]; then
  echo "$SCRPT Building a pull request."
fi

./mvnw verify \
       sonar:sonar \
       coveralls:report \
       -Derrorprone \
       -DrepoToken="$COVERALLS_REPO_TOKEN" \
       -T 1C -U -e -B -V $RELEASE_PROFILE

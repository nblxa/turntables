#!/usr/bin/env bash

set -e

# shellcheck source=./func.sh
source "$(dirname "$0")/func.sh"

SCRPT="[scripts/deploy.sh]"

if [ "$PULL_REQUEST" == "false" ] && [[ "$BRANCH" == master ]]; then
  echo "$SCRPT Deploying a new release."
  ./mvnw -pl turntables-core \
         clean deploy \
         --settings scripts/release-settings.xml \
         -Prelease -Dmaven.test.skip=true -e -B
  exit 0
fi

if [ "$PULL_REQUEST" == "true" ]; then
  echo "$SCRPT Not deploying a pull request."
  exit 0
fi

echo "$SCRPT Branch not recognized, not deploying."

#!/usr/bin/env bash

set -e

# shellcheck source=./func.sh
source "$(dirname "$0")/func.sh"

SCRPT="[scripts/build.sh]"

BUILD_BRANCH=$(getBranch)
BUILD_PULL_REQUEST=$(getPullRequest)
echo "$SCRPT Current branch : $BUILD_BRANCH"
echo "$SCRPT Pull request?  : $BUILD_PULL_REQUEST"

RELEASE_PROFILE=""

if [ "$BUILD_PULL_REQUEST" == "false" ] && [[ "$BUILD_BRANCH" == master ]]; then
  echo "$SCRPT Building a new release."
  RELEASE_PROFILE="-Prelease --settings scripts/release-settings.xml"
fi

if [ "$BUILD_PULL_REQUEST" == "true" ]; then
  echo "$SCRPT Building a pull request."
fi

./mvnw verify \
       sonar:sonar \
       coveralls:report \
       -Derrorprone \
       -DrepoToken="$COVERALLS_REPO_TOKEN" \
       -T 1C -U -e -V $RELEASE_PROFILE

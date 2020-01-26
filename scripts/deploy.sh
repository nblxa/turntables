#!/bin/bash

set -e

SCRPT="[scripts/deploy.sh]"

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [[ "$TRAVIS_BRANCH" == master ]]; then
  echo "$SCRPT Deploying a new release."
  ./mvnw -pl turntables-core \
         clean deploy \
         --settings scripts/release-settings.xml \
         -Prelease -Dmaven.test.skip=true -e -B
  exit 0
fi

if [ "$TRAVIS_PULL_REQUEST" == "true" ]; then
  echo "$SCRPT Not deploying a pull request."
  exit 0
fi

echo "$SCRPT Branch not recognized, not deploying."

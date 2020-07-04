#!/bin/bash

set -e

RELEASE_PROFILE=""
SCRPT="[scripts/build.sh]"

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [[ "$TRAVIS_BRANCH" == master ]]; then
  echo "$SCRPT Building a new release."
  RELEASE_PROFILE="-Prelease --settings scripts/release-settings.xml"
fi

if [ "$TRAVIS_PULL_REQUEST" == "true" ]; then
  echo "$SCRPT Building a pull request."
fi

./mvnw install \
       sonar:sonar -Dsonar.projectKey=turntables \
       coveralls:report \
       -Derrorprone \
       -e -B -V $RELEASE_PROFILE

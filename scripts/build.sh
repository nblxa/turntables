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
       -DttOraHost="$ORA_HOST" \
       -DttOraUrl="jdbc:oracle:thin:@${ORA_TNSNAME}?TNS_ADMIN=$(pwd)/turntables-test-oracle/wallet.local" \
       -DttClientId="$OAUTH_CLIENT_ID" \
       -DttClientSecret="$OAUTH_CLIENT_SECRET" \
       -Doracle.jdbc.fanEnabled=false \
       -e -B -V $RELEASE_PROFILE

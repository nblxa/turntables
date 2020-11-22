#!/usr/bin/env bash

set -e

# shellcheck source=./func.sh
source "$(dirname "$0")/func.sh"

SCRPT="[scripts/deploy.sh]"

DEPLOY_BRANCH=$(getBranch)
DEPLOY_PULL_REQUEST=$(getPullRequest)
echo "$SCRPT Current branch : $DEPLOY_BRANCH"
echo "$SCRPT Pull request?  : $DEPLOY_PULL_REQUEST"

if [ "$DEPLOY_PULL_REQUEST" == "false" ] && [[ "$DEPLOY_BRANCH" == master ]]; then
  echo "$SCRPT Deploying a new release."
  ./mvnw clean package deploy \
         --settings scripts/release-settings.xml \
         -Prelease -Dmaven.test.skip=true -e \
         -Dspotbugs.skip=true
  exit 0
fi

if [ "$DEPLOY_PULL_REQUEST" == "true" ]; then
  echo "$SCRPT Not deploying a pull request."
  exit 0
fi

echo "$SCRPT Branch not recognized, not deploying."

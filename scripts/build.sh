#!/usr/bin/env bash

set -e

# shellcheck source=./func.sh
source "$(dirname "$0")/func.sh"

SCRPT="[scripts/build.sh]"

BUILD_BRANCH=$(getBranch)
BUILD_PULL_REQUEST=$(getPullRequest)
echo "$SCRPT Current branch : $BUILD_BRANCH"
echo "$SCRPT Pull request?  : $BUILD_PULL_REQUEST"

./mvnw package -Derrorprone -T 1C -U -e

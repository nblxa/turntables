#!/usr/bin/env bash

getPullRequest() {
  PULL_REQUEST=""
  if [ "$TRAVIS_PULL_REQUEST" == "true" ] | [ -n "$CIRCLE_PULL_REQUEST" ]; then
    PULL_REQUEST=true
  else
    PULL_REQUEST=false
  fi
  echo "$PULL_REQUEST"
}

getBranch() {
  BRANCH="$TRAVIS_BRANCH"
  if [ -z "$BRANCH" ]; then
    BRANCH="$CIRCLE_BRANCH"
  fi
  echo "$BRANCH"
}

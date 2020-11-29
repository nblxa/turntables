#!/usr/bin/env bash

DIR="$(dirname "$0")/.."
DIR="$(realpath "$DIR")"

PARAM="$1"

# shellcheck source=./func.sh
source "$DIR/scripts/func.sh"

SCRPT="[scripts/doc-versions.sh]"
echo "$SCRPT Updating versions in the documentation"

replaceInXml() {
  GRP="$1"
  ART="$2"
  RPL="$3"
  DST="$4"

  CURR_GRP=""
  CURR_ART=""
  #echo "GRP=$GRP ART=$ART RPL=$RPL DST=$DST" #debug
  awk '/<dependency>/,/<\/dependency>/ { printf NR "="; print}' "$DST" | while read -rs TL
  do
    N="$(echo -n "$TL" | cut -f1 -d=)"
    TXT="$(echo -n "$TL" | cut -f2 -d=)"
    #echo "N=$N, TXT=$TXT" #debug
    echo -n "$TXT" | grep -q "<dependency>"
    IS_DEP="$?"
    if [ "$IS_DEP" -eq "0" ]; then
      CURR_GRP=""
      CURR_ART=""
      #echo "dep" #debug
    else
      echo -n "$TXT" | grep -qE "<groupId>$GRP</groupId>"
      IS_GRP="$?"
      if [ "$IS_GRP" -eq "0" ]; then
        CURR_GRP=$(echo -n "$TXT" | sed -E "s|^.*<groupId>($GRP)</groupId>.*$|\1|")
        CURR_ART=""
        #echo "grp" #debug
      else
        echo -n "$TXT" | grep -qE "<artifactId>$ART</artifactId>"
        IS_ART="$?"
        if [ "$IS_ART" -eq "0" ]; then
          CURR_ART=$(echo -n "$TXT" | sed -E "s|^.*<artifactId>($ART)</artifactId>.*$|\1|")
          #echo "art" #debug
        else
          echo -n "$TXT" | grep -qE "<version>"
          IS_VER="$?"
          if [ "$IS_VER" -eq "0" ] && [ -n "$CURR_GRP" ] && [ -n "$CURR_ART" ]; then
            sed -i -E "${N}s|^(.*)<version>.*</version>(.*)$|\1<version>${RPL}</version>\2|mg" "$DST"
          fi
        fi
      fi
    fi
    #echo "$CURR_GRP:$CURR_ART" #debug
  done
}

ASSERTJ_VERSION="$("$DIR/mvnw" -q -Dexec.executable="echo" -Dexec.args='${assertj.version}' --non-recursive exec:exec)"
MYSQL_VERSION="$("$DIR/mvnw" -q -Dexec.executable="echo" -Dexec.args='${mysql.connector.java.version}' --non-recursive exec:exec)"
OJDBC_VERSION="$("$DIR/mvnw" -q -Dexec.executable="echo" -Dexec.args='${ojdbc.version}' --non-recursive exec:exec)"
echo "$SCRPT assertj $ASSERTJ_VERSION"
echo "$SCRPT mysql $MYSQL_VERSION"
echo "$SCRPT ojdbc $OJDBC_VERSION"

if [ "$PARAM" == "release" ]; then
  TURNTABLES_VERSION="$("$DIR/mvnw" -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)"
  echo "$SCRPT turntables $TURNTABLES_VERSION"
  replaceInXml "io.github.nblxa" "turntables-.*?" "$TURNTABLES_VERSION" "$DIR/JDBC.md"
  replaceInXml "io.github.nblxa" "turntables-.*?" "$TURNTABLES_VERSION" "$DIR/README.md"
fi

replaceInXml "org.assertj" "assertj-core" "$ASSERTJ_VERSION" "$DIR/JDBC.md"
replaceInXml "org.assertj" "assertj-core" "$ASSERTJ_VERSION" "$DIR/README.md"

replaceInXml "mysql" "mysql-connector-java" "$MYSQL_VERSION" "$DIR/JDBC.md"
replaceInXml "mysql" "mysql-connector-java" "$MYSQL_VERSION" "$DIR/README.md"

replaceInXml "com.oracle.database.jdbc" "ojdbc.*" "$OJDBC_VERSION" "$DIR/JDBC.md"
replaceInXml "com.oracle.database.jdbc" "ojdbc.*" "$OJDBC_VERSION" "$DIR/README.md"

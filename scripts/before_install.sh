#!/bin/bash

set -e

# fetch the whole repository

git fetch --unshallow

# export GPG details

echo "$GPG_SECRET_KEYS" | base64 --decode | $GPG_EXECUTABLE --import --no-tty --batch
echo "$GPG_OWNERTRUST" | base64 --decode | $GPG_EXECUTABLE --import-ownertrust

# allow entering passphrase without tty

if [ -d ~/.gnupg ]; then
  mkdir -p ~/.gnupg
  chmod 700 ~/.gnupg
fi
if [ -f ~/.gnupg/gpg-agent.conf ]; then
  touch ~/.gnupg/gpg-agent.conf
  chmod 600 ~/.gnupg/gpg-agent.conf
fi
echo "allow-loopback-pinentry" | cat >> ~/.gnupg/gpg-agent.conf
gpg-connect-agent reloadagent /bye

# add Oracle Wallet

echo "Decrypting Oracle Wallet"
openssl aes-256-cbc -K $encrypted_6d9a2cafb883_key -iv $encrypted_6d9a2cafb883_iv \
    -in turntables-test-oracle/wallet.local.zip.enc \
    -out turntables-test-oracle/wallet.local.zip -d

echo "Extracting Oracle Wallet"
unzip turntables-test-oracle/wallet.local.zip -d turntables-test-oracle

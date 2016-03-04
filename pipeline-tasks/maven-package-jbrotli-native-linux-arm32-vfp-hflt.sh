#!/bin/bash

# fail fast
set -e

PROJECT_BASE_PATH=$(dirname "$0")/..
PROJECT_BASE_PATH=$(realpath "$PROJECT_BASE_PATH")

cd "$PROJECT_BASE_PATH/jbrotli-native/linux-x86-amd64"
chmod +x build.sh
mvn install

cd "$PROJECT_BASE_PATH/jbrotli"
mvn package

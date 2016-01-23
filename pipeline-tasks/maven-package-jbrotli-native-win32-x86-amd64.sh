#!/bin/bash

PROJECT_BASE_PATH=$(dirname "$0")/..
PROJECT_BASE_PATH=$(realpath "$PROJECT_BASE_PATH")

cd "$PROJECT_BASE_PATH/jbrotli-native/linux-x86-amd64" || exit $?
chmod +x build.sh || exit $?
mvn install || exit $?

cd "$PROJECT_BASE_PATH/jbrotli" || exit $?
mvn package || exit $?

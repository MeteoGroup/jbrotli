#!/bin/bash

PROJECT_BASE_PATH="$(dirname $0)/.."

cd "$PROJECT_BASE_PATH/jbrotli-native/linux-x86-amd64" || exit $?
mvn install || exit $?

cd "$PROJECT_BASE_PATH/jbrotli" || exit $?
mvn package || exit $?
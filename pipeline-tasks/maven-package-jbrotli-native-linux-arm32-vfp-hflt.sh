#!/bin/bash

# fail fast
set -e

PROJECT_BASE_PATH=$(dirname "$0")/..
PROJECT_BASE_PATH=$(realpath "$PROJECT_BASE_PATH")

cd "$PROJECT_BASE_PATH/jbrotli-native/linux-arm32-vfp-hflt"
chmod +x build*.sh
mvn -DskipTests=true package

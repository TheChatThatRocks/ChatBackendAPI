#!/bin/bash

PROJECT_DIR="../../"
mkdir -p dependency
# Compile project and copy it to dependecy folder for deploy
(cd ${PROJECT_DIR} && ./gradlew -x test build)
cp ${PROJECT_DIR}/build/libs/backendapi*.jar dependency
(cd dependency && jar -xf backendapi*.jar)
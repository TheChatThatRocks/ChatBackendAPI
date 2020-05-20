#!/bin/bash

PROJECT_DIR="../../../ChatEncryptionAPI/"
mkdir -p dependency
# Compile project and copy it to dependecy folder for deploy
(cd ${PROJECT_DIR} && ./gradlew -x test build)
cp ${PROJECT_DIR}/build/libs/encryption*.jar dependency
(cd dependency && jar -xf encryption*.jar)
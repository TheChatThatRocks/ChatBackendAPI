#!/bin/bash

(cd backendAPI && ./build.sh)
(cd encryption && ./build.sh)

#docker-compose up
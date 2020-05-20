#!/bin/bash
#heroku container:login
#heroku apps chat-cms-abct

#(cd backendAPI && ./build.sh)
#(cd encryption && ./build.sh)
#./build.sh

#heroku container:push web --recursive -a chat-cms-abct
heroku container:push web -a chat-cms-abct
heroku container:release web -a chat-cms-abct
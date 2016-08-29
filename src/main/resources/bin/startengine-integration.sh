#!/bin/bash

echo "starting crawler/indexer for integration test"
java -cp .:../lib/* com/jwm/ir/Main --integration_test --pagerank_interval=0 > /dev/null &
JAVA_PID="$!"
echo "$JAVA_PID" > pid.txt
echo "Started process with pid $JAVA_PID"

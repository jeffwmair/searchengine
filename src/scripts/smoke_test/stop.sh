#!/bin/bash
PID=`cat pid.txt`
echo "Killing process with PID $PID"
kill "$PID"


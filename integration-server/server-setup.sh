#!/bin/bash

sudo apt-get update -y
sudo apt-get upgrade -y
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y
echo "JAVA_HOME=/usr/bin/java" >> /etc/environment
sudo apt-get install git -y
sudo apt-get install maven -y

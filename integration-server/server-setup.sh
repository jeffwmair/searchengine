#!/bin/bash

sudo apt-get update -y
sudo apt-get upgrade -y
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y
echo "JAVA_HOME=/usr/bin/java" >> /etc/environment
sudo apt-get install git -y
sudo apt-get install maven -y
sudo apt-get install mysql-server -y
wget -q -O - http://pkg.jenkins-ci.org/debian/jenkins-ci.org.key | apt-key add -
echo deb http://pkg.jenkins-ci.org/debian binary/ > /etc/apt/sources.list.d/jenkins.list
sudo apt-get update
sudo apt-get install jenkins

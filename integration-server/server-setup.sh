#!/bin/bash

sudo apt-get update -y
sudo apt-get upgrade -y
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y
sudo apt-get install git -y
sudo apt-get install maven -y
sudo apt-get install mysql-server -y
wget -q -O - http://pkg.jenkins-ci.org/debian/jenkins-ci.org.key | apt-key add -
echo deb http://pkg.jenkins-ci.org/debian binary/ > /etc/apt/sources.list.d/jenkins.list
sudo apt-get update -y
sudo apt-get install jenkins -y
# wait a few seconds so the jenkins password is generated
echo "waiting a few seconds jenkins to start..."
sleep 20
cp -r jenkins-jobs/* /var/lib/jenkins/jobs/
# set the jobs dirs as writable for the jenkins application
chmod a+w -R /var/lib/jenkins/jobs/*
echo "Jenkins init password:"
cat /var/lib/jenkins/secrets/initialAdminPassword
echo "Jenkins is restarting so that the imported jobs will show up"
service jenkins restart
echo "Restart is finished"


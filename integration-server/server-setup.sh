#!/bin/bash

sudo apt-get update -y
sudo apt-get upgrade -y
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y
sudo apt-get install git -y
sudo apt-get install maven -y
sudo apt-get install mysql-server -y
sudo apt-get install apache2 -y
cp apache-content/* /var/www/html/
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
#echo "Jenkins is restarting so that the imported jobs will show up"
#service jenkins restart
#echo "Restart is finished"

# add mysql objects so that integration tests will succeed
echo "Adding mysql objects..."
mysql --execute="create database searchengine_test;"
mysql --execute="create user 'se_test_user'@'localhost' identified by 'se_test_user';"
mysql --execute="grant all on searchengine_test.* to 'se_test_user'@'localhost';"

echo "All done. Please run the following to restart jenkins after entering the inital password and installing plugins: service jenkins restart"

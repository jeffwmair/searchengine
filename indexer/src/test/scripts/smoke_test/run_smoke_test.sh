#!/bin/bash

set -e

WEB_TEST_DIR="$JWM_PROD/website/searchengine_test/"
CRAWL_HOST="http://localhost/searchengine_test/"
HERE=`pwd`

build_program() {
	# build the java program
	cd "$SEARCH_ENGINE"
	echo "cleanup old target dir"
	rm -rf target/
	echo "build new search-engine"
	mvn package 
	cd -
}

deploy_web_services() {
	# need to deploy the php services to the apache web dir
	echo "Updating deployment of php services"
	rsync -r $SEARCH_ENGINE/src/web/* $JWM_PROD/website/searchengine/
	echo "Updating credentials"
	cp utils.php $JWM_PROD/website/searchengine/services/
}

setup_test_pages() {
	rm -rf "$WEB_TEST_DIR"
	mkdir -p "$WEB_TEST_DIR"
	cp page*.html "$WEB_TEST_DIR"
}

setup_db() {
	echo "Creating db"
	../init_db.sh
}

JAVA_PID=''
run_program() {
	echo "Starting program"
	cd $JWM_DEV/searchengine/target/
	rm -rf SearchEngine-1.0/
	mkdir SearchEngine-1.0
	cp SearchEngine-1.0.war SearchEngine-1.0/
	cd SearchEngine-1.0
	tar -xf SearchEngine-1.0.war
	cd WEB-INF/classes
	mkdir flags
	# create a stop command file already; the test will finish before this is observed, actually.
	#touch flags/stop.txt
	# requires a stopwords.txt for now...
	touch stopwords.txt
	./bin/startengine-integration.sh
	cp pid.txt $HERE
	cd $HERE
}

stop_if_running() {
	./stop.sh
}

#stop_if_running
build_program
#deploy_web_services
setup_test_pages
setup_db
run_program

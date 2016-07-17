#!/bin/bash

set -e

WEB_TEST_DIR="$JWM_PROD/website/searchengine_test/"
CRAWL_HOST="http://localhost/searchengine_test/"

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

	echo "Inserting seed data"
	mysql "searchengine_test" < integration_test1_data.sql
}

JAVA_PID=''
run_program() {
	echo "Starting program"
	cd $JWM_DEV/searchengine/target/
	mkdir flags
	# create a stop command file already; the test will finish before this is observed, actually.
	#touch flags/stop.txt
	# requires a stopwords.txt for now...
	touch stopwords.txt
	java -jar SearchEngine-1.0.jar --host=localhost/searchengine --crawl=true --index=true --checkrobots=true --pagerank_interval=0 > /dev/null &
	JAVA_PID="$!"
	echo "Started process with pid $JAVA_PID"
}

tear_down_test_pages() {
	echo "Removing integration test pages"
	rm -rf "$WEB_TEST_DIR"
}

tear_down_db() {
	echo "Removing integration test database"
	mysql --execute="drop database searchengine_test"
}


build_program
deploy_web_services
setup_test_pages
setup_db
run_program
#tear_down_test_pages
#tear_down_db

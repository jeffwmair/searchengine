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
	cd ../../../../target/
	mkdir flags
	# create a stop command file already; the test will finish before this is observed, actually.
	#touch flags/stop.txt
	# requires a stopwords.txt for now...
	touch stopwords.txt
	java -jar searchengine-indexer-1.0.jar --integration_test --pagerank_interval=0 > /dev/null &
	JAVA_PID="$!"
	echo "Started process with pid $JAVA_PID"
	cd $HERE
	echo "$JAVA_PID" > pid.txt
}

stop_if_running() {
	./stop.sh
}

#stop_if_running
build_program
setup_test_pages
setup_db
run_program

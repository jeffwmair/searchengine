#!/bin/bash

set -e

WEB_TEST_DIR="$JWM_PROD/website/searchengine_test/"
CRAWL_HOST="http://localhost/searchengine_test/"

# setup the test pages
rm -rf "$WEB_TEST_DIR"
mkdir -p "$WEB_TEST_DIR"
cp page*.html "$WEB_TEST_DIR"

# reset the database for integration testing

# start the program
# start blah blah --domain=http://localhost/searchengine_test/

# query the database to check for the correct final state; ie, pages indexed, page rank, etc.


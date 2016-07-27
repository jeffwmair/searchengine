#!/bin/bash

#exit on error
set -e

echo "Starting init db script..."

USER='se_test_user'
PASS='se_test_user'
DB='searchengine_test'
HOST='localhost'


drop_db() {
	echo "Dropping db $DB"
	mysql --execute="drop database if exists $DB;"
}

drop_user() {
	echo "dropping user $USER"
	mysql --execute="drop user '$USER'@'$HOST';"
}

create_db() {
	echo "Creating db $DB"
	mysql --execute="create database $DB;"
}

create_user() {
	echo "Creating user $USER"
	mysql --execute="create user '$USER'@'$HOST' identified by '$PASS';"
}

grant_user_to_db() {
	echo "Granting user $USER to $DB"
	mysql --execute="grant all on $DB.* to '$USER'@'$HOST';"
}

create_tables() {
	echo "Creating tables in $DB"
	mysql "$DB" < ./src/sql/create_tables.sql
}

cd "$SEARCH_ENGINE"
drop_db
drop_user
create_db
create_user
grant_user_to_db
#create_tables	
cd -

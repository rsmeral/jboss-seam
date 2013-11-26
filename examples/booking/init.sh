#!/bin/bash

# clean deployment dir
rm -rf ~/jbosseap/standalone/deployments/*

# take a nap
sleep 60

# init db
cat app-root/data/init.sql | mysql -v -v -f -B -S $OPENSHIFT_MYSQL_DB_SOCKET -u $OPENSHIFT_MYSQL_DB_USERNAME -p $OPENSHIFT_MYSQL_DB_PASSWORD -h $OPENSHIFT_MYSQL_DB_HOST -P $OPENSHIFT_MYSQL_DB_PORT -D seamsoak2

# restart
gear restart --all --cart jbosseap-6
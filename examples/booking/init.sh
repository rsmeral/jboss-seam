# clean deployment dir
rm -rf ~/jbosseap/standalone/deployments/*

# take a nap
sleep 60

# init db
mysql -S $OPENSHIFT_MYSQL_DB_SOCKET -u $OPENSHIFT_MYSQL_DB_USER -p $OPENSHIFT_MYSQL_DB_PASSWORD -f seamsoak2 < app-root/data/init.sql

# restart
gear restart --all --cart jbosseap-6
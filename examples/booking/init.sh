# clean deployment dir
rm -rf ~/jbosseap/standalone/deployments/*

# take a nap
sleep 60

# init db
mysql seamsoak2 < app-root/data/init.sql

# restart
gear restart --all --cart jbosseap-6
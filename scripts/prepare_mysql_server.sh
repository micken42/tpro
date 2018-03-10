#!/bin/bash
set -e

echo " - - - - P R E P A R E - M Y S Q L - D A T A B A S E - - - - "
echo "Logging in to mysql server as root @ localhost"
mysql -uroot -p mysql < sql/init_mysql_user_tpro.sql

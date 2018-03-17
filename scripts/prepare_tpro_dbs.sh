#!/bin/bash
set -e

echo " - - - - P R E P A R E - U S E R - M A N A G E M E N T - D B - - - - "
mysql -utpro -ptpro -e "create database if not exists tpro_user_management"
mysql -utpro -ptpro tpro_user_management < sql/tpro-user-management/drop_user_management.sql
mysql -utpro -ptpro tpro_user_management < sql/tpro-user-management/create_user_management.sql
mysql -utpro -ptpro tpro_user_management < sql/tpro-user-management/dummy_user_management.sql

echo " - - - - P R E P A R E -  B O O K S T O R E - P L U G I N - D B - - - - "
mysql -utpro -ptpro -e "create database if not exists tpro_plugin_bookstore"
mysql -utpro -ptpro tpro_plugin_bookstore < sql/tpro-plugin-bookstore/drop_bookstore.sql
mysql -utpro -ptpro tpro_plugin_bookstore < sql/tpro-plugin-bookstore/create_bookstore.sql
mysql -utpro -ptpro tpro_plugin_bookstore < sql/tpro-plugin-bookstore/dummy_bookstore.sql

#echo " - - - - P R E P A R E -  H E L L O - U S E R - P L U G I N - D B - - - - "
#mysql -utpro -ptpro -e "create database if not exists tpro_plugin_hello_user"
#mysql -utpro -ptpro tpro_plugin_hello_user < sql/tpro-plugin-hello-user/drop_hello_user.sql
#mysql -utpro -ptpro tpro_plugin_hello_user < sql/tpro-plugin-hello-user/create_hello_user.sql
#mysql -utpro -ptpro tpro_plugin_hello_user < sql/tpro-plugin-hello-user/dummy_hello_user.sql


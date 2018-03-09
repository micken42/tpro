#!/bin/bash
set -e

echo " - - - - P R E P A R E - U S E R - M A N A G E M E N T - D B - - - - "
mysql -utpro -ptpro -e "create database if not exists tpro_user_management"
mysql -utpro -ptpro tpro_user_management < sql/drop_user_management.sql
mysql -utpro -ptpro tpro_user_management < sql/create_user_management.sql
mysql -utpro -ptpro tpro_user_management < sql/dummy_user_management.sql

echo " - - - - P R E P A R E -  B O O K S T O R E - P L U G I N - D B - - - - "
mysql -utpro -ptpro -e "create database if not exists tpro_plugin_bookstore"
mysql -utpro -ptpro tpro_plugin_bookstore < sql/drop_bookstore.sql
mysql -utpro -ptpro tpro_plugin_bookstore < sql/create_bookstore.sql
mysql -utpro -ptpro tpro_plugin_bookstore < sql/dummy_bookstore.sql


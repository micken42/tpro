
 -- initial MySQL setup 

 CREATE USER 'tpro'@'localhost' IDENTIFIED BY 'tpro';

 GRANT ALL PRIVILEGES ON *.* TO 'tpro'@'localhost' WITH GRANT OPTION;
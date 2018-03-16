#!/bin/bash
# Wichtig zur Vermeidung, dass weitere Kommandos trotz Fehlschlags
# Vorheriger ausgeführt werden un Unerwartetes passiert. Das Uner-
# wartete kann schlimme Folgen mit sich bringen. 
set -e

# Laufenden tomcat7 Service stoppen
sudo service tomcat7 stop

# Zeitpunkt des Deployments und Namen des zu deployenden 
# TPro WAR merken
deployment_time=`date '+%Y-%m-%d_%H:%M:%S'`
tpro_war=`ls tpro-app*.war`

# Installation des TPro WAR im tomcat7, indem das webapp-
# Verzeichnis bereinigt wird und das TPro WAR als tpro-app.war
# dort abgelegt wird
mv $tpro_war tpro-app.war
sudo rm -r /opt/tomcat7/webapps/* 
sudo cp tpro-app.war /opt/tomcat7/webapps

# Speichern des soeben installierten TPro WAR im Verzeichnis 
# deployments unter einem Verzeichnis, was als Namen den aktuellen
# Zeitstempel des Deployments traegt, um eine einfache Deployment-
# Historie zu pflegen
mkdir -p deployments/$deployment_time
cp tpro-app.war deployments/$deployment_time/$tpro_war
rm tpro-app.war

# tomcat7 Service erneut starten. Beim erneuten Start von tomcat7 
# wird das tpro-app.war in tomcat7's webapp-Verzeichnis automatisch
# entpackt und das dabei entstehende Verzeichnis tpro-app wird als
# ROOT behandelt. Das bedeutet das TPro unter der ROOT URL "/" der 
# laufenden tomcat7-Instanz ansteuerbar ist.   
sudo service tomcat7 start

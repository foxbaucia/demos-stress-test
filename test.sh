#!/bin/sh
git pull

mvn test || exit 1

mvn clean install || exit 1

echo --------------------------------------------
echo STARTING APP
echo --------------------------------------------

java -jar target/urls-stress-test-1.0.0.jar


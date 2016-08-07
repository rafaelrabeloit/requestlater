::Then, if one fails, all of them fail! :)
::The order matters!
::TODO read environment from args
mvn -f ..\microservice-value\pom.xml install && mvn -f ..\microservice-job\pom.xml install && mvn -f ..\microservice-watcher\pom.xml install && mvn -f ..\voyeur-server\pom.xml install -Denvironment=staging
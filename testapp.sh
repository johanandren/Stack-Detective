#!/bin/bash
java -Djava.util.logging.config.file=src/main/resources/jdk-log-debug.properties -cp "$HOME/.m2/repository/org/slf4j/slf4j-api/1.5.8/slf4j-api-1.5.8.jar:$HOME/.m2/repository/org/slf4j/slf4j-jdk14/1.5.8/slf4j-jdk14-1.5.8.jar:target/stackdetective-1.0-SNAPSHOT.jar" com.markatta.stackdetective.TestApp $*

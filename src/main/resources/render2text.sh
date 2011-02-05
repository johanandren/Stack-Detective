#!/bin/bash
java -Djava.util.logging.config.file=src/main/resources/jdk-log-debug.properties -cp "$HOME/.m2/repository/log4j/log4j/1.2.14/log4j-1.2.14.jar:classes" com.markatta.stackdetective.util.RenderAsText $*

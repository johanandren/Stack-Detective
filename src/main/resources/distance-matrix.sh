#!/bin/bash
#
# Copyright (C) 2011 Johan Andren <johan@markatta.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

java -Djava.util.logging.config.file=src/main/resources/jdk-log-debug.properties -cp "$HOME/.m2/repository/log4j/log4j/1.2.14/log4j-1.2.14.jar:classes" com.markatta.stackdetective.util.PrintDistanceMatrix $*

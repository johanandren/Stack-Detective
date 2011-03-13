/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.parse;

import java.util.regex.Pattern;

/**
 * The token types. 
 *
 * @author johan
 */
enum TokenType {
    // order is important here, each pattern is tried in order
    // so if any possible overlaps exist they need to be ordered 
    // accordingly
    PARENTHESIS_OPEN("^\\("),
    PARENTHESIS_CLOSE("^\\)"),
    WS("^[\\t\\ ]+"),
    DOT("^\\."),
    COLON("^:"),
    NUMBER("^[0-9]+"),
    LINEFEED("^\n\r?"),
        // this sucks, but I cannot figure out what unicode-character-blocks there are
    // that could be used for internationalized matching
    STRING("^\\w+"),
    SPECIAL_CHARACTER("^.");

    private final Pattern regexp;

    private TokenType(String regexpString) {
        regexp = Pattern.compile(regexpString);
    }

    public Pattern getRegexp() {
        return regexp;
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Parses an input string into tokens
 * 
 * @author johan
 */
class Lexer {

    List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<Token>();
        for (int position = 0; position < source.length(); position++) {
            String substring = source.substring(position);
            boolean found = false;
            for (TokenType tokenType : TokenType.values()) {
                Matcher matcher = tokenType.getRegexp().matcher(substring);
                if (matcher.find()) {
                    String text = matcher.group();

                    // we need to move further than one step for next iteration
                    if (text.length() > 1) {
                        position += text.length() - 1;
                    }
                    tokens.add(new Token(tokenType, text));
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ParseException("Failed at " + position + ": " + substring);
            }
        }
        return tokens;
    }
}

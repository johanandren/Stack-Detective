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

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author johan
 */
final class TokenStack extends Stack<Token> {

    public TokenStack(List<Token> tokens) {
        // push in reversed order to get first item first on stack
        for (int i = tokens.size() - 1; i >= 0; i--) {
            push(tokens.get(i));
        }
    }

    /**
     * 
     * @param expectedType the type expected to be next
     * @param ignoreWs keep popping until a non-whitespace token is found
     * @return The next token
     * @throws ParseException if the next token is not any of the given types
     */
    Token popNext(boolean ignoreWs, TokenType... expectedTypes) {

        if (ignoreWs) {
            consumeWs();
        }
        Token next = pop();

        if (expectedTypes != null) {

            for (TokenType tokenType : expectedTypes) {
                if (next.getType() == tokenType) {
                    return next;
                }
            }

            throw new ParseException("Expected token to be one of " + Arrays.toString(expectedTypes) + " but was " + next);
        } else {
            return next;
        }
    }

    /**
     * Pop WS* from the stack.
     */
    public void consumeWs() {
        while (!isEmpty() && peek().getType() == TokenType.WS) {
            pop();
        }
    }

    
    /**
     * Pop (WS | LF)* from the stack
     */
    public void consumeLfAndWs() {
        while (!isEmpty() && (peek().getType() == TokenType.WS || peek().getType() == TokenType.LINEFEED)) {
            pop();
        }
    }
    
    /**
     * Pop the next token, check that it is a string and match the given regexp
     * with it. 
     * @param ignoreWs Just pop ws tokens and ignore them
     * @param regexp A regexp that the next string should match
     * @return The STRING token matching the regexp
     * @throws ParseException if the next is not a string or if it does not match the regexp
     */
    Token popNextString(boolean ignoreWs, String regexp) {
        if (ignoreWs) {
            consumeWs();
        }
        Token next = pop();
        if (next.getType() != TokenType.STRING) {
            throw new ParseException("Expected token to be STRING but was " + next);
        }
        if (!next.getText().matches(regexp)) {
            throw new ParseException("Expected next string to match " + regexp + " but did not, token: " + next);
        }
        return next;
    }

    private boolean nextIsOneOf(TokenType type, TokenType... types) {

        TokenType nextType = peek().getType();
        if (nextType == type) {
            return true;
        }
        for (TokenType tokenType : types) {
            if (tokenType == nextType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Keep poping until a non listed type is found (will not be poped) and concatenate
     * the tokens text content.
     */
    String popAndConcatenate(TokenType type, TokenType... types) {
        StringBuilder builder = new StringBuilder();
        while (!isEmpty() && nextIsOneOf(type, types)) {
            builder.append(pop().getText());
        }
        return builder.toString();
    }
    
    /**
     * Keep poping and concatenating the texts until the given type is found.
     */
    String popAndConcatenateUntil(TokenType type, TokenType ... expectedTypes) {
        StringBuilder builder = new StringBuilder();
        while (!isEmpty() && !nextIsOneOf(type, expectedTypes)) {
            builder.append(pop().getText());
        }
        return builder.toString();
    }
    
    

    /**
     * Check the type of the next token without poping it from the stack
     */
    boolean nextTypeIs(TokenType tokenType, TokenType... expectedTypes) {
        if (empty()) {
            return false;
        }
        Token nextToken = peek();
        if (nextToken.getType() == tokenType) {
            return true;
        }
        if (expectedTypes != null) {
            for (TokenType current : expectedTypes) {
                if (nextToken.getType() == current) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Pop multiple items off the stack and return the last token
     */
    Token pop(int numberToPop) {
        Token last = null;
        for (int i = 0; i < numberToPop; i++) {
            last = pop();
        }
        return last;
    }
}

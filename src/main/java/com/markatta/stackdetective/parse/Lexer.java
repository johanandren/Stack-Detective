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

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

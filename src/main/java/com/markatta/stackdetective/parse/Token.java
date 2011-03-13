package com.markatta.stackdetective.parse;

/**
 *
 * @author johan
 */
class Token {

    private final TokenType type;

    private final String text;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[type: " + type + ", text: " + text + "]";
    }
}

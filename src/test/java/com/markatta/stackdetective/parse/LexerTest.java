package com.markatta.stackdetective.parse;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class LexerTest {

    @Test
    public void tokenizeSimpleString() {
        Lexer instance = new Lexer();
        List<Token> result = instance.tokenize("aaaa.bbbb.cccc");
        assertEquals(5, result.size());
    }

    @Test
    public void tokenizeSwedishCharacters() {
        Lexer instance = new Lexer();
        List<Token> result = instance.tokenize("åäö");
        assertEquals(3, result.size());
    }

    
    @Test
    public void tokenizeComplexString() {
        Lexer instance = new Lexer();
        List<Token> result = instance.tokenize("aaaa.bbbb.cccc.ExceptionClass: Description of error\n\tat a.b.c.OtherClass(File.type:23)");

        TokenType[] expectedTypes = new TokenType[]{
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.COLON,
            TokenType.WS,
            TokenType.STRING,
            TokenType.WS,
            TokenType.STRING,
            TokenType.WS,
            TokenType.STRING,
            TokenType.LINEFEED,
            TokenType.WS,
            TokenType.STRING,
            TokenType.WS,
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.PARENTHESIS_OPEN,
            TokenType.STRING,
            TokenType.DOT,
            TokenType.STRING,
            TokenType.COLON,
            TokenType.NUMBER,
            TokenType.PARENTHESIS_CLOSE
        };

        for (int i = 0; i < result.size(); i++) {
            Token token = result.get(i);
            TokenType expextedType = expectedTypes[i];
            assertEquals("Token " + i + " is of wrong type.", expextedType, token.getType());
        }
    }
}
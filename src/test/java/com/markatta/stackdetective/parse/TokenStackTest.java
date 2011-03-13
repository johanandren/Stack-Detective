package com.markatta.stackdetective.parse;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class TokenStackTest {

    @Test
    public void constructorWithListAddsTokensInCorrectOrder() {
        Token stringToken = new Token(TokenType.STRING, "a");
        Token whitespace = new Token(TokenType.WS, " ");
        TokenStack instance = new TokenStack(Arrays.asList(whitespace, stringToken));
        assertSame(whitespace, instance.pop());
        assertSame(stringToken, instance.pop());
    }

    @Test
    public void popNextWithConsumeWhitespaceReturnsNextNonWhitespace() {
        Token stringToken = new Token(TokenType.STRING, "a");
        List<Token> tokens = Arrays.asList(new Token(TokenType.WS, " "), stringToken);
        TokenStack tokenStack = new TokenStack(tokens);

        Token result = tokenStack.popNext(true, TokenType.STRING);
        assertSame(stringToken, result);
    }

    @Test(expected = ParseException.class)
    public void popNextWithWrongTypeThrowsException() {
        Token stringToken = new Token(TokenType.STRING, "a");
        List<Token> tokens = Arrays.asList(stringToken);
        TokenStack tokenStack = new TokenStack(tokens);

        tokenStack.popNext(false, TokenType.DOT);
    }

    @Test(expected = ParseException.class)
    public void popNextStringWithNonStringNextThrowsException() {
        List<Token> tokens = Arrays.asList(new Token(TokenType.COLON, ":"));
        TokenStack tokenStack = new TokenStack(tokens);

        tokenStack.popNextString(false, "regexp");
    }

    @Test(expected = ParseException.class)
    public void popNextStringWithNotMatchingRegexpThrowsException() {
        TokenStack tokenStack = new TokenStack(Arrays.asList(new Token(TokenType.STRING, "ab")));

        tokenStack.popNextString(false, ".");
    }

    @Test
    public void popNextStringWithMatchingRegexpReturnsToken() {
        Token stringToken = new Token(TokenType.STRING, "ab");
        TokenStack tokenStack = new TokenStack(Arrays.asList(stringToken));

        Token result = tokenStack.popNextString(false, ".+");
        assertSame(stringToken, result);
    }

    @Test
    public void nextTypeIsWorksWithSingleArgument() {
        TokenStack instance = new TokenStack(Arrays.asList(new Token(TokenType.WS, " ")));
        assertTrue(instance.nextTypeIs(TokenType.WS));
        assertFalse(instance.nextTypeIs(TokenType.STRING));
    }

    @Test
    public void nextTypeIsWOrksWithMultipleArguments() {
        TokenStack instance = new TokenStack(Arrays.asList(new Token(TokenType.WS, " ")));
        assertTrue(instance.nextTypeIs(TokenType.COLON, TokenType.STRING, TokenType.WS));
        assertFalse(instance.nextTypeIs(TokenType.STRING, TokenType.COLON));
    }

    @Test
    public void multiPop() {
        Token stringToken = new Token(TokenType.STRING, "a");
        TokenStack instance = new TokenStack(Arrays.asList(new Token(TokenType.WS, " "), stringToken));
        Token last = instance.pop(2);
        assertSame(stringToken, last);
        assertTrue(instance.isEmpty());
    }

    @Test
    public void popAndConcatenate() {

        TokenStack tokenStack = new TokenStack(Arrays.asList(new Token(TokenType.STRING, "a"), new Token(TokenType.DOT, "."), new Token(TokenType.STRING, "b")));

        String result = tokenStack.popAndConcatenate(TokenType.STRING, TokenType.DOT);
        assertEquals("a.b", result);
    }
}
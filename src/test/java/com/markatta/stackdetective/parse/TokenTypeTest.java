
package com.markatta.stackdetective.parse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class TokenTypeTest {

   

    @Test
    public void testGetRegexp() {
        
        assertTrue(TokenType.SPECIAL_CHARACTER.getRegexp().matcher("[").matches());
    }

}
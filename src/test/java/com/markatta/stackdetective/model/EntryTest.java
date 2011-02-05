package com.markatta.stackdetective.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class EntryTest {

    @Test
    public void testGetPackageName() {
        Entry instance = new Entry("testMethod", "com.example.ClassName", "ClassName.java", 23);
        assertEquals("com.example", instance.getPackageName());
        assertEquals("ClassName", instance.getClassName());
    }
    
    @Test
    public void worksWithDefaultPackage() {
        Entry instance = new Entry("testMethod", "ClassName", "ClassName.java", 23);
        assertEquals("", instance.getPackageName());
        assertEquals("ClassName", instance.getClassName());
    }
    
}
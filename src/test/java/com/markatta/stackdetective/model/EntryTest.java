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
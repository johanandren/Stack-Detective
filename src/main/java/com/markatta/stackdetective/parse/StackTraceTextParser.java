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

import com.markatta.stackdetective.model.StackTrace;

/**
 *
 * @author johan
 */
public interface StackTraceTextParser {

    /**
     * @param stacktrace A text to parse into a stacktrace
     * @return A stack trace object or <code>null</code> if parsing was not possible.
     * @throws ParseException if the trace was not parseable
     */
    public StackTrace parse(CharSequence stacktrace);
}

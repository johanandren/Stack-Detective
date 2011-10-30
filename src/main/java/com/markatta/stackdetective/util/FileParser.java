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
package com.markatta.stackdetective.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.markatta.stackdetective.model.StackTrace;
import com.markatta.stackdetective.parse.StackTraceParserFactory;
import com.markatta.stackdetective.parse.StackTraceTextParser;

/**
 * Parses text files into stack trace objects.
 * 
 * @author johan
 */
final class FileParser {

	static List<StackTrace> parseOnePerFile(File[] filePaths) throws IOException {
		List<StackTrace> stackTraces = new ArrayList<StackTrace>();
		for (File file : filePaths) {
			stackTraces.add(parse(file));
		}
		return stackTraces;
	}

	static List<StackTrace> parseOnePerFile(String[] filePaths) throws IOException {
		List<StackTrace> stackTraces = new ArrayList<StackTrace>();
		for (String path : filePaths) {
			stackTraces.add(parse(new File(path)));

		}
		return stackTraces;
	}

	private static StackTrace parse(File file) throws IOException {
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuilder builder = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}

			StackTraceTextParser parser = StackTraceParserFactory.getDefaultTextParser();
			return parser.parse(builder);

		} finally {
			reader.close();
		}
	}
}

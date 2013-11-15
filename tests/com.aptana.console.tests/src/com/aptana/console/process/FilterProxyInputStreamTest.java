/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.process;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.aptana.debug.core.FilterProxyInputStream;
import com.aptana.debug.core.IProcessOutputFilter;

import junit.framework.TestCase;

/**
 * @author Max Stepanov
 */
public class FilterProxyInputStreamTest extends TestCase {
	
	private static final String PREFIX = "[ABC]";
	private static final String UTF8 = "UTF-8";

	private static InputStream createInputStream(String content, String encoding, final String filterPrefix) throws Exception {
		return new FilterProxyInputStream(new ByteArrayInputStream(content.getBytes(encoding)), encoding, new IProcessOutputFilter() {
			public String filter(String line) {
				return filterPrefix == null || line.startsWith(filterPrefix) ? line : null;
			}
		});
	}
	
	private static String[] readInputStream(InputStream in, String encoding) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines.toArray(new String[lines.size()]);
	}
	
	private static String[] createAndReadInputStream(String content, String encoding, final String filterPrefix) throws IOException, Exception {
		return readInputStream(createInputStream(content, encoding, filterPrefix), encoding);
	}

	public void testEmptyInput() throws Exception {
		String content = "";
		String[] output = createAndReadInputStream(content, UTF8, PREFIX);
		assertEquals("Number of lines mismatch", 0, output.length);
		output = createAndReadInputStream(content, UTF8, null);
		assertEquals("Number of lines mismatch", 0, output.length);
	}

	public void testEmptyLine() throws Exception {
		String content = "\n";
		String[] output = createAndReadInputStream(content, UTF8, PREFIX);
		assertEquals("Number of lines mismatch", 0, output.length);
		output = createAndReadInputStream(content, UTF8, null);
		assertEquals("Number of lines mismatch", 1, output.length);
	}

	public void testSingleLine() throws Exception {
		String content = "abc";
		String[] output = createAndReadInputStream(content, UTF8, PREFIX);
		assertEquals("Number of lines mismatch", 0, output.length);
		output = createAndReadInputStream(content, UTF8, null);
		assertEquals("Number of lines mismatch", 1, output.length);
	}

	public void testSingleLineWithNewLine() throws Exception {
		String content = "abc\n";
		String[] output = createAndReadInputStream(content, UTF8, PREFIX);
		assertEquals("Number of lines mismatch", 0, output.length);
		output = createAndReadInputStream(content, UTF8, null);
		assertEquals("Number of lines mismatch", 1, output.length);
	}

	public void testMatch() throws Exception {
		String content = PREFIX+" abc";
		String[] output = createAndReadInputStream(content, UTF8, PREFIX);
		assertEquals("Number of lines mismatch", 1, output.length);
		output = createAndReadInputStream(content, UTF8, null);
		assertEquals("Number of lines mismatch", 1, output.length);
	}

	public void testMultipleLines() throws Exception {
		String content = "line1\n"+PREFIX+"line2\nline3\n"+PREFIX+"line4";
		String[] output = createAndReadInputStream(content, UTF8, PREFIX);
		assertEquals("Number of lines mismatch", 2, output.length);
		assertEquals("Line content mismatch", PREFIX+"line2", output[0]);
		assertEquals("Line content mismatch", PREFIX+"line4", output[1]);
		output = createAndReadInputStream(content, UTF8, null);
		assertEquals("Number of lines mismatch", 4, output.length);
	}

}

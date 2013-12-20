/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("nls")
public class URLEncoderTest {

	@Test
	public void testHttpEncodeURL() throws MalformedURLException {
		URL url = URLEncoder.encode(new URL("http://www.aptana.com/index with space.html?refresh=1&all=true#a"));
		assertEquals("Encoded URL doesn't match", "http://www.aptana.com/index%20with%20space.html?refresh=1&all=true#a", url.toExternalForm());
	}

	@Test
	public void testFtpEncodeURL() throws MalformedURLException {
		URL url = URLEncoder.encode(new URL("ftp://ftp.aptana.com/path/file.txt"));
		assertEquals("Encoded URL doesn't match", "ftp://ftp.aptana.com/path/file.txt", url.toExternalForm());
	}

	@Test
	public void testFileEncodeURL() throws MalformedURLException {
		URL url = URLEncoder.encode(new URL("file:///path/file.txt"));
		assertEquals("Encoded URL doesn't match", "file:/path/file.txt", url.toExternalForm());
	}

	@Test
	public void testEncode() throws MalformedURLException {
		String encoded = URLEncoder.encode("/path/to/file.txt", null, null);
		assertEquals("Encoded URL doesn't match", "/path/to/file.txt", encoded);
	}
	
	@Test
	public void testEncodeWithSpace() throws MalformedURLException {
		String encoded = URLEncoder.encode("/path/to/file with space.txt", null, null);
		assertEquals("Encoded URL doesn't match", "/path/to/file%20with%20space.txt", encoded);
	}

	@Test
	public void testEncodeWithQuery() throws MalformedURLException {
		String encoded = URLEncoder.encode("/path/to/file.txt", "refresh=1&all=true", null);
		assertEquals("Encoded URL doesn't match", "/path/to/file.txt?refresh=1&all=true", encoded);
	}

	@Test
	public void testEncodeWithFragment() throws MalformedURLException {
		String encoded = URLEncoder.encode("/path/to/file.txt", null, "abc");
		assertEquals("Encoded URL doesn't match", "/path/to/file.txt#abc", encoded);
	}

	@Test
	public void testEncodeWithEncodedFragment() throws MalformedURLException {
		String encoded = URLEncoder.encode("/path/to/file.txt", null, "ab%20c");
		assertEquals("Encoded URL doesn't match", "/path/to/file.txt#ab%20c", encoded);
	}

}

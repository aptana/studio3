package com.aptana.util.tests;

import junit.framework.TestCase;

import com.aptana.util.StringUtil;

public class StringUtilTest extends TestCase
{

	public void testMd5()
	{
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", StringUtil.md5(""));
		assertEquals("a4c4da98a897d052baf31d4e5c0cce55", StringUtil.md5("cwilliams@aptana.com"));

		assertNull(StringUtil.md5(null));
	}

	public void testSanitizeHTML()
	{
		assertEquals("Heckle &amp; Jeckle", StringUtil.sanitizeHTML("Heckle & Jeckle"));
		assertEquals("&lt;html>Heckle &amp; Jeckle&lt;/html>", StringUtil.sanitizeHTML("<html>Heckle & Jeckle</html>"));
	}
}

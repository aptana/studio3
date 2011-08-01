/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class URLUtilTest extends TestCase
{
	public void testJoinParameters() throws Exception
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("a", "b");
		params.put("c", "d?e");
		params.put("f", "#");
		params.put("g", "h i");

		String param = URLUtil.joinParameters(params, false);
		assertTrue("Parameter list incorrectly joined", param.indexOf("a=b") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("c=d?e") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("f=#") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("g=h i") >= 0);

		param = URLUtil.joinParameters(params, true);
		assertTrue("Parameter list incorrectly joined", param.indexOf("a=b") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("&c=d%3Fe") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("f=%23") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("g=h+i") >= 0);
	}

	public void testAppendParameters() throws MalformedURLException, UnsupportedEncodingException
	{

		Map<String, String> params = new HashMap<String, String>();
		params.put("c", "d");

		// not encoding
		// url, no params
		URL url = new URL("http://www.aptana.com");
		URL newUrl = URLUtil.appendParameters(url, params, false);
		assertEquals("http://www.aptana.com?c=d", newUrl.toString());

		// url, no params, with anchor
		url = new URL("http://www.aptana.com#anchor");
		newUrl = URLUtil.appendParameters(url, params, false);
		assertEquals("http://www.aptana.com?c=d#anchor", newUrl.toString());

		// url, params, no anchor
		url = new URL("http://www.aptana.com?a=b");
		newUrl = URLUtil.appendParameters(url, params, false);
		assertEquals("http://www.aptana.com?a=b&c=d", newUrl.toString());

		// url, params, anchor
		url = new URL("http://www.aptana.com?a=b#anchor");
		newUrl = URLUtil.appendParameters(url, params, false);
		assertEquals("http://www.aptana.com?a=b&c=d#anchor", newUrl.toString());

		// url, existing same param, anchor
		url = new URL("http://www.aptana.com?a=b&c=d#anchor");
		newUrl = URLUtil.appendParameters(url, params, false);
		assertEquals("http://www.aptana.com?a=b&c=d&c=d#anchor", newUrl.toString());

		// encoding
		params = new HashMap<String, String>();
		params.put("c", "d#e");

		// url, no params
		url = new URL("http://www.aptana.com");
		newUrl = URLUtil.appendParameters(url, params, true);
		assertEquals("http://www.aptana.com?c=d%23e", newUrl.toString());

		// url, no params, with anchor
		url = new URL("http://www.aptana.com#anchor");
		newUrl = URLUtil.appendParameters(url, params, true);
		assertEquals("http://www.aptana.com?c=d%23e#anchor", newUrl.toString());

		// url, params, no anchor
		url = new URL("http://www.aptana.com?a=b");
		newUrl = URLUtil.appendParameters(url, params, true);
		assertEquals("http://www.aptana.com?a=b&c=d%23e", newUrl.toString());

		// url, params, anchor
		url = new URL("http://www.aptana.com?a=b#anchor");
		newUrl = URLUtil.appendParameters(url, params, true);
		assertEquals("http://www.aptana.com?a=b&c=d%23e#anchor", newUrl.toString());

		// url, existing same param, anchor
		url = new URL("http://www.aptana.com?a=b&c=d#anchor");
		newUrl = URLUtil.appendParameters(url, params, true);
		assertEquals("http://www.aptana.com?a=b&c=d&c=d%23e#anchor", newUrl.toString());

	}
}

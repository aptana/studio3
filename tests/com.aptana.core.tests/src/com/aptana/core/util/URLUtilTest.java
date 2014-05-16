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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

public class URLUtilTest
{
	@Test
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

		// true is default case
		param = URLUtil.joinParameters(params);
		assertTrue("Parameter list incorrectly joined", param.indexOf("a=b") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("&c=d%3Fe") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("f=%23") >= 0);
		assertTrue("Parameter list incorrectly joined", param.indexOf("g=h+i") >= 0);

		// test null case
		assertEquals("Parameter list incorrectly joined", "", URLUtil.joinParameters(null, false));
	}

	@Test
	public void testAppendParametersString() throws MalformedURLException, UnsupportedEncodingException
	{

		// not encoding
		// url, no params
		URL url = new URL("http://www.aptana.com");
		URL newUrl = URLUtil.appendParameters(url, new String[] { "c", "d" });
		assertEquals("http://www.aptana.com?c=d", newUrl.toString());

		// url, no params, with anchor
		url = new URL("http://www.aptana.com#anchor");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d" });
		assertEquals("http://www.aptana.com?c=d#anchor", newUrl.toString());

		// url, params, no anchor
		url = new URL("http://www.aptana.com?a=b");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d" });
		assertEquals("http://www.aptana.com?a=b&c=d", newUrl.toString());

		// url, params, anchor
		url = new URL("http://www.aptana.com?a=b#anchor");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d" });
		assertEquals("http://www.aptana.com?a=b&c=d#anchor", newUrl.toString());

		// url, existing same param, anchor
		url = new URL("http://www.aptana.com?a=b&c=d#anchor");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d" });
		assertEquals("http://www.aptana.com?a=b&c=d&c=d#anchor", newUrl.toString());

		// url, no params
		url = new URL("http://www.aptana.com");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d#e" });
		assertEquals("http://www.aptana.com?c=d%23e", newUrl.toString());

		// url, no params, with anchor
		url = new URL("http://www.aptana.com#anchor");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d#e" });
		assertEquals("http://www.aptana.com?c=d%23e#anchor", newUrl.toString());

		// url, params, no anchor
		url = new URL("http://www.aptana.com?a=b");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d#e" });
		assertEquals("http://www.aptana.com?a=b&c=d%23e", newUrl.toString());

		// url, params, anchor
		url = new URL("http://www.aptana.com?a=b#anchor");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d#e" });
		assertEquals("http://www.aptana.com?a=b&c=d%23e#anchor", newUrl.toString());

		// url, existing same param, anchor
		url = new URL("http://www.aptana.com?a=b&c=d#anchor");
		newUrl = URLUtil.appendParameters(url, new String[] { "c", "d#e" });
		assertEquals("http://www.aptana.com?a=b&c=d&c=d%23e#anchor", newUrl.toString());

		// test null case
		url = new URL("http://www.aptana.com");
		assertEquals("Parameter list incorrectly joined", url, URLUtil.appendParameters(url, (String[]) null));

		assertEquals("Parameter list incorrectly joined", null,
				URLUtil.appendParameters(null, new String[] { "c", "d#e" }));

		assertEquals("Parameter list incorrectly joined", null, URLUtil.appendParameters(null, (String[]) null));

		try
		{
			assertEquals("Parameter list incorrectly joined", null,
					URLUtil.appendParameters(null, new String[] { "c" }));
			fail();
		}
		catch (IllegalArgumentException ex)
		{

		}
	}

	@Test
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
		newUrl = URLUtil.appendParameters(url, params);
		assertEquals("http://www.aptana.com?c=d%23e", newUrl.toString());

		// url, no params, with anchor
		url = new URL("http://www.aptana.com#anchor");
		newUrl = URLUtil.appendParameters(url, params);
		assertEquals("http://www.aptana.com?c=d%23e#anchor", newUrl.toString());

		// url, params, no anchor
		url = new URL("http://www.aptana.com?a=b");
		newUrl = URLUtil.appendParameters(url, params);
		assertEquals("http://www.aptana.com?a=b&c=d%23e", newUrl.toString());

		// url, params, anchor
		url = new URL("http://www.aptana.com?a=b#anchor");
		newUrl = URLUtil.appendParameters(url, params);
		assertEquals("http://www.aptana.com?a=b&c=d%23e#anchor", newUrl.toString());

		// url, existing same param, anchor
		url = new URL("http://www.aptana.com?a=b&c=d#anchor");
		newUrl = URLUtil.appendParameters(url, params);
		assertEquals("http://www.aptana.com?a=b&c=d&c=d%23e#anchor", newUrl.toString());

		// test null case
		url = new URL("http://www.aptana.com");
		assertEquals("Parameter list incorrectly joined", url,
				URLUtil.appendParameters(url, (Map<String, String>) null));

		assertEquals("Parameter list incorrectly joined", null, URLUtil.appendParameters(null, params));

		assertEquals("Parameter list incorrectly joined", null,
				URLUtil.appendParameters(null, (Map<String, String>) null));
	}

	@Test
	public void testAppendDefaultParameters() throws MalformedURLException, UnsupportedEncodingException
	{
		// not encoding
		// url, no params
		URL url = new URL("http://www.aptana.com");
		URL newUrl = URLUtil.appendDefaultParameters(url);
		assertEquals(URLUtil.joinParameters(URLUtil.getDefaultParameters()), newUrl.getQuery());
	}

	@Test
	public void testGetDefaultParameters()
	{
		Map<String, String> parameters = URLUtil.getDefaultParameters();
		assertEquals(EclipseUtil.getProductVersion(), parameters.get("v"));
		assertEquals(Locale.getDefault().toString(), parameters.get("nl"));
		assertEquals(2, parameters.size());
	}
}

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Utilities for manipulating URLs
 * 
 * @author Ingo Muschenetz
 */
public class URLUtil
{

	private static String UTF8 = "UTF-8"; //$NON-NLS-1$

	/**
	 * Joins a map of key/values into URL parameters. Assumes the items are already URL encoded
	 * 
	 * @param parameters
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String joinParameters(Map<String, String> parameters, boolean encode)
			throws UnsupportedEncodingException
	{
		ArrayList<String> builder = new ArrayList<String>();
		for (Map.Entry<String, String> entry : parameters.entrySet())
		{
			if (encode)
			{
				builder.add(URLEncoder.encode(entry.getKey(), UTF8) + "=" + URLEncoder.encode(entry.getValue(), UTF8)); //$NON-NLS-1$				
			}
			else
			{
				builder.add(entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$
			}
		}
		return StringUtil.join("&", builder); //$NON-NLS-1$
	}

	/**
	 * Appends a set of parameters onto a existing URL
	 * 
	 * @param url
	 * @param parameters
	 * @param encodeParameters
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static URL appendParameters(URL url, Map<String, String> parameters, boolean encode)
			throws MalformedURLException, UnsupportedEncodingException
	{
		String urlString = url.toString();
		int questionIndex = urlString.indexOf('?');
		int anchorIndex = urlString.indexOf('#');

		// if pre-existing ?, we will be adding parameters to existing
		String separator = questionIndex < 0 ? "?" : "&"; //$NON-NLS-1$//$NON-NLS-2$
		String params = joinParameters(parameters, encode);

		String newUrl = null;
		if (anchorIndex < 0)
		{
			newUrl = urlString + separator + params;
		}
		else
		{
			// If anchor exists, add anchor text to end
			newUrl = urlString.substring(0, anchorIndex) + separator + params + urlString.substring(anchorIndex);
		}

		return new URL(newUrl);
	}

}

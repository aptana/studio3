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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utilities for manipulating URLs
 * 
 * @author Ingo Muschenetz
 */
public class URLUtil
{

	/**
	 * Joins a map of key/values into URL parameters. It will URL encode the new keys and values
	 * 
	 * @param parameters
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String joinParameters(Map<String, String> parameters) throws UnsupportedEncodingException
	{
		return joinParameters(parameters, true);
	}

	/**
	 * Joins a map of key/values into URL parameters.
	 * 
	 * @param parameters
	 * @param encode
	 *            URL-encode the keys and values
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	static String joinParameters(Map<String, String> parameters, boolean encode)
			throws UnsupportedEncodingException
	{
		if (parameters == null)
		{
			return StringUtil.EMPTY;
		}

		ArrayList<String> builder = new ArrayList<String>();
		for (Map.Entry<String, String> entry : parameters.entrySet())
		{
			if (encode)
			{
				String value = (entry.getValue() != null) ? entry.getValue() : StringUtil.EMPTY;
				builder.add(URLEncoder.encode(entry.getKey(), IOUtil.UTF_8)
						+ "=" + URLEncoder.encode(value, IOUtil.UTF_8)); //$NON-NLS-1$
			}
			else
			{
				builder.add(entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$
			}
		}
		return StringUtil.join("&", builder); //$NON-NLS-1$
	}

	/**
	 * Appends a set of parameters onto a existing URL.. It will URL encode the new keys and values
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static URL appendParameters(URL url, String[] parameters) throws MalformedURLException,
			UnsupportedEncodingException
	{
		if (parameters == null)
		{
			return url;
		}

		if (parameters.length % 2 != 0)
		{
			throw new IllegalArgumentException(Messages.URLUtil_EvenNumberUrlParameters);
		}

		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < parameters.length; i = i + 2)
		{
			params.put(parameters[i], parameters[i + 1]);
		}
		return appendParameters(url, params, true);
	}

	/**
	 * Appends a set of parameters onto a existing URL.. It will URL encode the new keys and values
	 * 
	 * @param url
	 * @param parameters
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static URL appendParameters(URL url, Map<String, String> parameters) throws MalformedURLException,
			UnsupportedEncodingException
	{
		return appendParameters(url, parameters, true);
	}

	/**
	 * Appends a default suite of parameters to Studio URLs, including version and user's language
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	static URL appendDefaultParameters(URL url) throws MalformedURLException, UnsupportedEncodingException
	{
		Map<String, String> parameters = getDefaultParameters();
		return appendParameters(url, parameters, true);
	}

	/**
	 * Gets a default set of parameters to append to common Studio URLs
	 * 
	 * @return
	 */
	public static Map<String, String> getDefaultParameters()
	{
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("nl", System.getProperty("osgi.nl", Locale.getDefault().toString())); //$NON-NLS-1$ //$NON-NLS-2$
		parameters.put("v", EclipseUtil.getProductVersion()); //$NON-NLS-1$
		return parameters;
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
		if (url == null)
		{
			return null;
		}
		if (parameters == null)
		{
			return url;
		}

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

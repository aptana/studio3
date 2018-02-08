/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.internal;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import com.aptana.core.util.IOUtil;

/**
 * @author Max Stepanov
 */
public final class Util
{

	/**
	 * 
	 */
	private Util()
	{
	}

	/**
	 * decodeURL
	 * 
	 * @param url
	 * @return String
	 */
	public static String decodeURL(String url)
	{
		try
		{
			return URLDecoder.decode(url, IOUtil.UTF_8);
		}
		catch (Exception ignore)
		{
			ignore.getCause();
		}
		try
		{
			return URLDecoder.decode(url, "ASCII"); //$NON-NLS-1$
		}
		catch (UnsupportedEncodingException ignore)
		{
			ignore.getCause();
		}
		return url;
	}

	public static URL toURL(URI uri) throws MalformedURLException
	{
		if (uri != null)
		{
			return uri.toURL();
		}
		return null;
	}

}

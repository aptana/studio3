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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class URLEncoder
{

	private URLEncoder()
	{
	}

	/**
	 * Encode URL
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL encode(URL url) throws MalformedURLException
	{
		try
		{
			String auth = url.getAuthority();
			String host = url.getHost();
			int port = url.getPort();
			if (StringUtil.isEmpty(auth) || (auth.equals(host) && port == -1) || (auth.equals(host + ":" + port))) //$NON-NLS-1$
			{
				URI uri = new URI(url.getProtocol(), null, host, port, url.getPath(), url.getQuery(), url.getRef());
				return uri.toURL();
			}
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), Messages.URLEncoder_Cannot_Encode_URL + url, e);
		}
		return url;
	}

	public static String encode(String path, String query, String fragment)
	{
		StringBuilder sb = new StringBuilder();
		StringTokenizer tok = new StringTokenizer(path, "/", true); //$NON-NLS-1$
		String segment;
		while (tok.hasMoreTokens())
		{
			segment = tok.nextToken();
			if (segment.length() == 1 && segment.charAt(0) == '/')
			{
				sb.append(segment);
			}
			else
			{
				sb.append(encodeSegment(segment));
			}
		}
		if (!StringUtil.isEmpty(query))
		{
			sb.append('?').append(query);
		}
		if (!StringUtil.isEmpty(fragment))
		{
			sb.append('#').append(encodeSegment(fragment));
		}
		return sb.toString();
	}

	private static String encodeSegment(String segment)
	{
		int index = segment.indexOf('%');
		if (index != -1 && index + 1 < segment.length() && Character.isDigit(segment.charAt(index + 1)))
		{
			return segment;
		}

		StringBuilder sb = new StringBuilder();
		char[] chars = segment.toCharArray();
		for (char ch : chars)
		{
			if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= ',' && ch <= ':') || ch == '~'
					|| ch == '_' || ch == '!' || ch == '$')
			{
				sb.append(ch);
			}
			else
			{
				try
				{
					byte[] bytes = Character.toString(ch).getBytes("UTF8"); //$NON-NLS-1$
					for (byte b : bytes)
					{
						sb.append('%').append(Integer.toHexString((b >> 4) & 0x0F))
								.append(Integer.toHexString(b & 0x0F));
					}
				}
				catch (UnsupportedEncodingException e)
				{
				}
			}
		}
		return sb.toString();
	}
}

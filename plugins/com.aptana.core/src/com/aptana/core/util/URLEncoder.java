/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

/**
 * @author Max Stepanov
 */
public final class URLEncoder
{

	/**
	 * 
	 */
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
			if ( auth == null || auth.length() == 0
				|| (auth.equals(host) && port == -1)
				|| (auth.equals(host+":"+port))) { //$NON-NLS-1$
				URI uri = new URI(
						url.getProtocol(),
						null,
						host,
						port,
						url.getPath(),
						url.getQuery(),
						url.getRef());
				url = uri.toURL();
			}
		}
		catch (URISyntaxException e)
		{
			logError(Messages.URLEncoder_Cannot_Encode_URL + url, e);
		}
		return url;
	}

	public static String encode(String path, String query, String fragment)
	{
		StringBuffer sb = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(path, "/", true); //$NON-NLS-1$
		while (tok.hasMoreTokens())
		{
			String segment = tok.nextToken();
			if (segment.length() == 1 && segment.charAt(0) == '/')
			{
				sb.append(segment);
			}
			else
			{
				sb.append(encodeSegment(segment));
			}
		}
		if (query != null && query.length() > 0) {
			sb.append('?').append(query);
		}
		if (fragment != null && fragment.length() > 0) {
			sb.append('?').append(encodeSegment(fragment));
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
		StringBuffer sb = new StringBuffer();
		char[] chars = segment.toCharArray();
		for (int i = 0; i < chars.length; ++i)
		{
			char ch = chars[i];
			if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= ',' && ch <= ':') || ch == '~'
					|| ch == '_' || ch == '!' || ch == '$')
			{
				sb.append(ch);
			}
			else
			{
				try
				{
					byte[] bytes = new Character(ch).toString().getBytes("UTF8"); //$NON-NLS-1$
					for(int j = 0; j < bytes.length; ++j)
					{
						sb.append('%')
						.append(Integer.toHexString( (bytes[j] >> 4) & 0x0F ))
						.append(Integer.toHexString( bytes[j] & 0x0F ));
					}
				}
				catch (UnsupportedEncodingException e)
				{
				}
			}
		}
		return sb.toString();
	}

	private static void logError(String errorMessage, Throwable e) {
		CorePlugin.log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK, errorMessage, e));
	}
}

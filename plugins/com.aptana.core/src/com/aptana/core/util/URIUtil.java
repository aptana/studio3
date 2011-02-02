/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URIUtil
{

	/**
	 * URIUtil
	 */
	private URIUtil()
	{
	}

	/**
	 * decodeURI
	 * 
	 * @param uri
	 * @return
	 */
	public static String decodeURI(String uri)
	{
		String result = null;
	
		if (uri != null)
		{
			try
			{
				result = URLDecoder.decode(uri.toString(), "utf-8"); //$NON-NLS-1$
			}
			catch (UnsupportedEncodingException e)
			{
				// ignore, returns null
			}
		}
	
		return result;
	}
}

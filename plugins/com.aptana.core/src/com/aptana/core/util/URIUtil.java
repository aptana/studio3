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
				result = URLDecoder.decode(uri.toString(), "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				// ignore, returns null
			}
		}
	
		return result;
	}
	
	
}

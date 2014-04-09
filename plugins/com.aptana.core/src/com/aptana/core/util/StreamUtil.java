/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author Spike Washburn
 */
public class StreamUtil
{

	/**
	 * Default buffer size.
	 */
	private static final int DEFAULT_SIZE = 15 * 1024;

	private StreamUtil()
	{
	}

	/**
	 * Retrieves the content from an input stream.
	 * 
	 * @param stream
	 *            the input stream
	 * @return the content
	 * @throws IOException
	 */
	public static String readContent(InputStream stream) throws IOException
	{
		return readContent(stream, null);
	}

	/**
	 * Retrieves the content from an input stream using a specific charset.
	 * 
	 * @param stream
	 *            the input stream
	 * @param charset
	 *            the charset
	 * @return the content
	 * @throws IOException
	 */
	private static String readContent(InputStream stream, String charset) throws IOException
	{
		if (stream == null)
		{
			return null;
		}

		BufferedReader reader = getBufferedReader(stream, charset);
		try
		{
			StringBuilder buffer = new StringBuilder();
			char[] readBuffer = new char[2048];
			int n = reader.read(readBuffer);
			while (n > 0)
			{
				buffer.append(readBuffer, 0, n);
				n = reader.read(readBuffer);
			}
			return buffer.toString();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				// ignores
			}
		}
	}

	private static BufferedReader getBufferedReader(InputStream stream, String charset)
			throws UnsupportedEncodingException
	{
		InputStreamReader inputReader;
		if (charset == null)
		{
			inputReader = new InputStreamReader(stream);
		}
		else
		{
			inputReader = new InputStreamReader(stream, charset);
		}
		return new BufferedReader(inputReader, DEFAULT_SIZE);
	}
}

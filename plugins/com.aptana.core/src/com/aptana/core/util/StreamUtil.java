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
	public static String readContent(InputStream stream, String charset) throws IOException
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

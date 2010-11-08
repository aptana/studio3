package com.aptana.core.util;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.aptana.core.CorePlugin;

/**
 * A stream gobbler thread that reads from an InputStream, collects the read text, and allows retrieving it by calling
 * getResult, during, or at the end of the execution.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class InputStreamGobbler extends Thread
{
	private InputStream is;
	private String charset;
	private String newLineSeparator;
	private StringBuilder result;

	/**
	 * Constructs a new InputStreamGobbler
	 * 
	 * @param is
	 * @param charset
	 * @param newLineSeparator
	 */
	public InputStreamGobbler(InputStream is, String newLineSeparator, String charset)
	{
		if (is == null || newLineSeparator == null)
		{
			throw new IllegalArgumentException("The InputStream and the newLineSeparator cannot be null!"); //$NON-NLS-1$
		}
		this.is = is;
		this.charset = charset;
		this.newLineSeparator = newLineSeparator;
	}

	/**
	 * Returns the read text. If the thread was never started yet, the result will be null.
	 * 
	 * @return The string that was read from the input stream.
	 */
	public String getResult()
	{
		if (result == null)
		{
			return null;
		}
		return result.toString();
	}

	/**
	 * Run the gobbler as a thread that will read from the input stream and will store it in memory. The resulted text
	 * can be retrieved by calling {@link #getResult()}
	 */
	public void run()
	{
		InputStreamReader isr = null;
		try
		{
			if (charset != null)
			{
				isr = new InputStreamReader(is, charset);
			}
			else
			{
				isr = new InputStreamReader(is);
			}

			BufferedReader br = new BufferedReader(isr);
			result = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null)
			{
				result.append(line);
				result.append(newLineSeparator);
			}
			// delete last extraneous newline
			if (result.length() > 0)
			{
				result.deleteCharAt(result.length() - newLineSeparator.length());
			}
		}
		catch (IOException ioe)
		{
			CorePlugin.logError(ioe.getMessage(), ioe);
		}
		finally
		{
			if (isr != null)
			{
				try
				{
					isr.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}
}
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
package com.aptana.core.internal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.aptana.core.CorePlugin;

/**
 * A runnable class that is designed to write into the given OutputStream in a thread.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class OutputStreamThread extends Thread
{
	private OutputStream is;
	private String charset;
	private String content;

	/**
	 * Construct a new OutputStreamGobbler.
	 * 
	 * @param os
	 * @param content
	 * @param charset
	 */
	public OutputStreamThread(OutputStream os, String content, String charset)
	{
		if (os == null || content == null)
		{
			throw new IllegalArgumentException("The OutputStream and the content cannot be null!"); //$NON-NLS-1$
		}
		this.is = os;
		this.content = content;
		this.charset = charset;
	}

	/**
	 * Do the actual writing as a thread.
	 */
	public void run()
	{
		OutputStreamWriter osr = null;
		try
		{
			if (charset != null)
			{
				osr = new OutputStreamWriter(is, charset);
			}
			else
			{
				osr = new OutputStreamWriter(is);
			}

			BufferedWriter br = new BufferedWriter(osr);

			br.write(content);
			br.flush();

		}
		catch (IOException ioe)
		{
			CorePlugin.logError(ioe.getMessage(), ioe);
		}
		finally
		{
			if (osr != null)
			{
				try
				{
					osr.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}
}
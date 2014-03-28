/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * A runnable class that is designed to write into the given OutputStream in a thread.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
class OutputStreamThread extends Thread
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
	OutputStreamThread(OutputStream os, String content, String charset)
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
		catch (IOException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
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

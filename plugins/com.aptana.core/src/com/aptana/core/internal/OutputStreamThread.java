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
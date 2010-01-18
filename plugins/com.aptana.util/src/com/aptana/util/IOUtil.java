package com.aptana.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public abstract class IOUtil
{

	/**
	 * Reads an InputStream into a String. Safely closes the stream after reading, or if any exceptions occur. Returns
	 * null if the stream is null or an exception occurs reading in the stream.
	 * 
	 * @param stream
	 * @return
	 */
	public static String read(InputStream stream)
	{
		return read(stream, null);
	}

	public static String read(InputStream stream, String charset)
	{
		if (stream == null)
			return null;
		try
		{
			InputStreamReader inReader;
			if (charset != null)
				inReader = new InputStreamReader(stream, charset);
			else
				inReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(inReader);
			StringBuilder template = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				template.append(line);
			}
			return template.toString();
		}
		catch (IOException e)
		{
			UtilPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, UtilPlugin.PLUGIN_ID, e.getMessage(), e));
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		return null;
	}

}

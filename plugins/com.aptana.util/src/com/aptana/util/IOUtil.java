package com.aptana.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class IOUtil
{

	/**
	 * Reads an InputStream into a String. Safely closes the stream after reading, or if any exceptions occur.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream stream) throws IOException
	{
		// TODO Point here from GitHistoryPage lines 283 - 315
		// TODO Point here from DiffFormatter lines 99-131
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder template = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				template.append(line);
			}
			return template.toString();
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

}

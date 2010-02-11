package com.aptana.terminal.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.terminal.Activator;

public class ProcessReader extends Thread
{
	private InputStream _input;
	private String _name;
	private StringBuffer _output;
	private Pattern _filter;

	/**
	 * ProcessReader
	 * 
	 * @param name
	 * @param is
	 * @param output
	 */
	public ProcessReader(String name, InputStream is, StringBuffer output)
	{
		this._input = is;
		this._name = name;
		this._output = output;
	}

	/**
	 * getReaderName
	 */
	public String getReaderName()
	{
		return this._name;
	}

	/**
	 * setFilter
	 * 
	 * @param pattern
	 */
	public void setFilter(Pattern pattern)
	{
		this._filter = pattern;
	}

	/**
	 * run
	 */
	public void run()
	{
		try
		{
			byte[] line = new byte[1024];
			int count;

			while ((count = this._input.read(line)) != -1)
			{
				String text = new String(line, 0, count);
				boolean sendText = true;

				if (this._filter != null)
				{
					// assume we're still inside filtered text
					sendText = false;

					Matcher matcher = this._filter.matcher(text);

					if (matcher.find())
					{
						// grab any text after the EOFilter marker
						text = text.substring(matcher.end());

						// only send text if we have some
						sendText = (text.length() > 0);

						// turn off filtering
						this._filter = null;
					}
				}

				if (sendText)
				{
					synchronized (this._output)
					{
						this._output.append(text);
					}
				}
			}
		}
		catch (IOException e)
		{
			Activator.logError(Messages.ProcessReader_Error_Reading_From_Process, e);
		}
	}
}

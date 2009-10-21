package com.aptana.terminal.server;
import java.io.IOException;
import java.io.InputStream;

public class ProcessReader extends Thread
{
	InputStream is;
	String type;
	StringBuffer output;

	public ProcessReader(String type, InputStream is, StringBuffer output)
	{
		this.is = is;
		this.type = type;
		this.output = output;
	}

	public void run()
	{
		try
		{
			byte[] line = new byte[1024];
			int count;
			
			while ((count = is.read(line)) != -1)
			{
				String text = new String(line, 0, count);
				
				synchronized (output)
				{
					output.append(text);
				}
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}

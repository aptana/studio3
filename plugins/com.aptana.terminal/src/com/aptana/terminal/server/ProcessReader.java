package com.aptana.terminal.server;
import java.io.IOException;
import java.io.InputStream;

import com.aptana.terminal.Activator;

public class ProcessReader extends Thread
{
	private InputStream _input;
	private String _name;
	private StringBuffer _output;

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
				
				synchronized (this._output)
				{
					this._output.append(text);
				}
				
				//System.out.println(this._name + ":~" + encodeString(text) + "~");
			}
		}
		catch (IOException e)
		{
			Activator.logError(Messages.ProcessReader_Error_Reading_From_Process, e);
		}
	}
	
//	/**
//	 * encodeString
//	 * 
//	 * @param text
//	 * @return
//	 */
//	protected String encodeString(String text)
//	{
//		StringBuilder builder = new StringBuilder();
//		
//		for (char c : text.toCharArray())
//		{
//			if (c < 32 || 127 < c)
//			{
//				builder.append("\\x").append(Integer.toString((int) c, 16));
//			}
//			else
//			{
//				builder.append(c);
//			}
//		}
//		
//		return builder.toString();
//	}
}

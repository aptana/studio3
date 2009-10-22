package com.aptana.terminal.server;
import java.io.IOException;
import java.io.InputStream;

public class ProcessReader extends Thread
{
	private InputStream _input;
	private String _name;
	private StringBuffer _output;

	public ProcessReader(String name, InputStream is, StringBuffer output)
	{
		this._input = is;
		this._name = name;
		this._output = output;
	}

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
				
				//System.out.println(this._name + ":~" + Activator.encodeString(text) + "~");
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}

package com.aptana.terminal.server;
import java.io.IOException;
import java.io.OutputStream;


public class ProcessWriter
{
	private OutputStream _os;
	
	/**
	 * ProcessWriter
	 * 
	 * @param os
	 */
	public ProcessWriter(OutputStream os)
	{
		this._os = os;
	}
	
	public void sendText(String text)
	{
		try
		{
			this._os.write(text.getBytes());
			this._os.flush();
			
			//System.out.println("STDIN:~" + Activator.encodeString(text) + "~");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

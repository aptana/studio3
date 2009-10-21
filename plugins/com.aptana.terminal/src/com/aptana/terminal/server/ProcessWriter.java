package com.aptana.terminal.server;
import java.io.IOException;
import java.io.OutputStream;


public class ProcessWriter
{
	OutputStream os;
	
	public ProcessWriter(OutputStream os)
	{
		this.os = os;
	}
	
	public void sendText(String text)
	{
		try
		{
			os.write(text.getBytes());
			os.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

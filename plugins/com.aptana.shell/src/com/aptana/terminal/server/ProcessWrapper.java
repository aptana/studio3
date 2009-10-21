package com.aptana.terminal.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.terminal.Activator;

public class ProcessWrapper
{
	private Process _process;
	private ProcessReader _stdout;
	private ProcessReader _stderr;
	private ProcessWriter _stdin;
	private StringBuffer _output;

	/**
	 * ProcessWrapper
	 * 
	 * @param process
	 */
	public ProcessWrapper()
	{
	}

	/**
	 * start
	 */
	public void start()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("redtty"), null);
		
		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			File file = new File(fileURL.toURI());
			ProcessBuilder builder = new ProcessBuilder(file.getAbsolutePath());
			Map<String, String> env = builder.environment();
			env.put("TERM", "xterm-color");
	
			try
			{
				this._process = builder.start();
				this._output = new StringBuffer();
				this._stdout = new ProcessReader("STDOUT", this._process.getInputStream(), this._output);
				this._stderr = new ProcessReader("STDERR", this._process.getInputStream(), this._output);
				this._stdin = new ProcessWriter(this._process.getOutputStream());
				
				this._stdout.start();
				this._stderr.start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * stop
	 */
	public void stop()
	{
		if (this._process != null)
		{
			this._process.destroy();

			try
			{
				this._stdout.join();
				this._stderr.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * sendText
	 * 
	 * @param text
	 */
	public void sendText(char[] chars)
	{
		this.sendText(new String(chars));
	}

	/**
	 * sendText
	 * 
	 * @param text
	 */
	public void sendText(String text)
	{
		if (this._stdin != null)
		{
			this._stdin.sendText(text);
		}
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		String result = null;

		if (this._output != null)
		{
			synchronized (this._output)
			{
				result = this._output.toString();
				this._output.setLength(0);
			}
		}

		return result;
	}
}

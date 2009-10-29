package com.aptana.terminal.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

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
	 * getCommandLineArguments
	 * 
	 * @return
	 */
	protected String[] getCommandLineArguments()
	{
		return new String[0];
	}
	
	/**
	 * getProcessFile
	 * 
	 * @return
	 */
	protected File getProcessFile()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("redtty"), null); //$NON-NLS-1$
		File result = null;

		try
		{
			URL fileURL = FileLocator.toFileURL(url);
			
			result = new File(fileURL.toURI());
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(
				Messages.ProcessWrapper_Error_Locating_Terminal_Executable,
				new Object[] { url.toString() }
			);
			
			Activator.logError(message, e);
		}
		catch (URISyntaxException e)
		{
			String message = MessageFormat.format(
					Messages.ProcessWrapper_Malformed_Terminal_Executable_URI,
					new Object[] { url.toString() }
				);
				
				Activator.logError(message, e);
		}
		
		return result;
	}
	
	/**
	 * getStartingDirectory
	 * 
	 * @return
	 */
	protected String getStartingDirectory()
	{
		return System.getProperty("user.home"); //$NON-NLS-1$
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
	 * Set up environment variables for the new process
	 * 
	 * @param env
	 */
	protected void setupEnvironment(Map<String, String> env)
	{
		String OS = Platform.getOS();
		
		if (OS.equals(Platform.OS_WIN32) == false)
		{
			env.put("TERM", "xterm-color"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * start
	 */
	public void start()
	{
		File file = this.getProcessFile();

		if (file != null && file.exists())
		{
			List<String> argList = new ArrayList<String>();
			String[] args;
			
			argList.add(file.getAbsolutePath());
			argList.addAll(Arrays.asList(this.getCommandLineArguments()));
			args = argList.toArray(new String[argList.size()]);
			
			ProcessBuilder builder = new ProcessBuilder(args);
			
			// setup the TERM environment variable to be compatible with WebTerm
			this.setupEnvironment(builder.environment());
			
			// grab a working directory
			String startingDirectory = this.getStartingDirectory();
			
			// apply working directory to the process
			if (startingDirectory != null)
			{
				File dir = new File(startingDirectory);
				
				if (dir.exists())
				{
					builder.directory(dir);
				}
			}

			// start up and begin processing I/O
			try
			{
				this._process = builder.start();
				this._output = new StringBuffer();
				this._stdout = new ProcessReader("STDOUT", this._process.getInputStream(), this._output); //$NON-NLS-1$
				this._stderr = new ProcessReader("STDERR", this._process.getErrorStream(), this._output); //$NON-NLS-1$
				this._stdin = new ProcessWriter(this._process.getOutputStream());
	
				this._stdout.start();
				this._stderr.start();
			}
			catch (IOException e)
			{
				String message = MessageFormat.format(
					Messages.ProcessWrapper_Error_Starting_Process,
					new Object[] { file.getAbsoluteFile() }
				);
				
				Activator.logError(message, e);
			}
		}
		else
		{
			String message = MessageFormat.format(
				Messages.ProcessWrapper_Process_File_Does_Not_Exist,
				new Object [] { file.getAbsoluteFile() }
			);
			
			Activator.logWarning(message);
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
}

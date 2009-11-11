package com.aptana.terminal.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import org.eclipse.core.runtime.URIUtil;

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
		String OS = Platform.getOS();
		String[] result = new String[0];
		
		if (OS.equals(Platform.OS_WIN32))
		{
			result = new String[] { "/K", "cd" };
		}
		
		return result;
	}
	
	/**
	 * getProcessFile
	 * 
	 * @return
	 */
	protected String getProcessName()
	{
		String OS = Platform.getOS();
		String result = null;
		
		if (OS.equals(Platform.OS_WIN32))
		{
			result = "cmd.exe";
		}
		else if (OS.equals(Platform.OS_MACOSX) || OS.equals(Platform.OS_LINUX))
		{
			URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("redtty"), null); //$NON-NLS-1$
			try
			{
				URL fileURL = FileLocator.toFileURL(url);
				
				File file = new File(new Path(fileURL.getPath()).toOSString());
				
				result = file.getAbsolutePath();
			}
			catch (IOException e)
			{
				String message = MessageFormat.format(
					Messages.ProcessWrapper_Error_Locating_Terminal_Executable,
					new Object[] { url.toString() }
				);
				
				Activator.logError(message, e);
			}
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
			
//			if (Platform.getOS().equals(Platform.OS_WIN32))
//			{
//				this._output.append(text);
//			}
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
		String file = this.getProcessName();

		if (file != null && file.length() > 0)
		{
			List<String> argList = new ArrayList<String>();
			String[] args;
			
			argList.add(file);
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
					new Object[] { file }
				);
				
				Activator.logError(message, e);
			}
		}
		else
		{
			Activator.logWarning(Messages.ProcessWrapper_Process_File_Does_Not_Exist);
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

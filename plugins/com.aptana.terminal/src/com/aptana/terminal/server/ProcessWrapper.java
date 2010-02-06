package com.aptana.terminal.server;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;

public class ProcessWrapper
{
	// TODO: These shouldn't be in here. We're pulling the values from the explorer plugin
	// so as not to create a dependency on the two projects.
	private static final String ACTIVE_PROJECT_PROPERTY = "activeProject"; //$NON-NLS-1$
	private static final String EXPLORER_PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$
	
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	private static Map<String,List<ProcessConfiguration>> configurationMap;
	
	private Process _process;
	private ProcessReader _stdout;
	private ProcessReader _stderr;
	private ProcessWriter _stdin;
	private StringBuffer _output;
	private String _startingDirectory;
	
	/**
	 * getCurrentConfiguration
	 * 
	 * @return
	 */
	protected static ProcessConfiguration getCurrentConfiguration()
	{
		ProcessConfiguration result = null;
		
		// make sure we've loaded our configurations
		loadConfigurations();
		
		// return default for now
		List<ProcessConfiguration> configurations = configurationMap.get(Platform.getOS());
		
		if (configurations != null && configurations.size() > 0)
		{
			result = configurations.get(0);
		}
		
		return result;
	}
	
	/**
	 * loadConfigurations
	 */
	protected static void loadConfigurations()
	{
		// TODO: read from extension point
		if (configurationMap == null)
		{
			configurationMap = new HashMap<String, List<ProcessConfiguration>>();
			
			List<ProcessConfiguration> configurations;
			
			// MAC OS X
			configurations = new ArrayList<ProcessConfiguration>();
			configurations.add(new DefaultMacOsXConfiguration());
			configurationMap.put(Platform.OS_MACOSX, configurations);
			
			// LINUX
			configurations = new ArrayList<ProcessConfiguration>();
			configurations.add(new DefaultLinuxConfiguration());
			configurationMap.put(Platform.OS_LINUX, configurations);
			
			// WIN32
			configurations = new ArrayList<ProcessConfiguration>();
			configurations.add(new DefaultWindowsConfiguration());
			configurationMap.put(Platform.OS_WIN32, configurations);
		}
	}
	
	/**
	 * ProcessWrapper
	 * 
	 * @param process
	 */
	public ProcessWrapper()
	{
		this(null);
	}
	
	/**
	 * ProcessWrapper
	 * 
	 * @param startingDirectory
	 */
	public ProcessWrapper(String startingDirectory)
	{
		this._startingDirectory = startingDirectory;
	}
	
	/**
	 * getStartingDirectory
	 * 
	 * @return
	 */
	protected String getStartingDirectory()
	{
		if (this._startingDirectory == null)
		{
			String value = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID, ACTIVE_PROJECT_PROPERTY, null, null);
			
			if (value != null)
			{
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(value);
				
				if (project != null)
				{
					IPath projectLocation = project.getLocation();
					
					if (projectLocation != null)
					{
						this._startingDirectory = projectLocation.toPortableString();
					}
				}
			}
			
			if (this._startingDirectory == null)
			{
				this._startingDirectory = System.getProperty(USER_HOME_PROPERTY);
			}
		}
		
		return this._startingDirectory;
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
	 * @param ch
	 */
	public void sendText(char ch)
	{
		this.sendText(Character.toString(ch));
	}
	
	/**
	 * sendText
	 * 
	 * @param chars
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
	 * start
	 */
	public void start()
	{
		ProcessConfiguration configuration = getCurrentConfiguration();
		
		if (configuration != null && configuration.isValid())
		{
			List<String> argList = configuration.getCommandLineArguments();
			String file = configuration.getProcessName();

			// prepend the executable to run
			argList.add(0, file);
			
			ProcessBuilder builder = new ProcessBuilder(argList);
			
			// setup the TERM environment variable to be compatible with WebTerm
			configuration.setupEnvironment(builder.environment());

			// set the working directory
			builder.directory(new File(this.getStartingDirectory()));

			// start up and begin processing I/O
			try
			{
				// perform any last-minute configuration before we start the process
				configuration.beforeStart(this, builder);
				
				this._process = builder.start();
				this._output = new StringBuffer();
				this._stdout = new ProcessReader("STDOUT", this._process.getInputStream(), this._output); //$NON-NLS-1$
				this._stderr = new ProcessReader("STDERR", this._process.getErrorStream(), this._output); //$NON-NLS-1$
				this._stdin = new ProcessWriter(this._process.getOutputStream());
	
				this._stdout.start();
				this._stderr.start();
				
				// perform any post-start configuration
				configuration.afterStart(this);
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
			// no config or it's invalid
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

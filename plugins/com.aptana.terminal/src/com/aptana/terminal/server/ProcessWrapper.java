package com.aptana.terminal.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;
import com.aptana.terminal.Utils;

public class ProcessWrapper
{
	public class Message
	{
		public final Date timestamp;
		public final String name;
		public final String content;
		
		/**
		 * Message
		 * 
		 * @param name
		 * @param content
		 */
		public Message(String name, String content)
		{
			this.timestamp = new Date();
			this.name = (name != null) ? name : ""; //$NON-NLS-1$
			this.content = (content != null) ? content : ""; //$NON-NLS-1$
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return this.timestamp + ": " + this.name + ": " + Utils.encodeString(this.content); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$
	
	// TODO: These shouldn't be in here. We're pulling the values from the explorer plugin
	// so as not to create a dependency on the two projects.
	private static final String ACTIVE_PROJECT_PROPERTY = "activeProject"; //$NON-NLS-1$
	public static final String EXPLORER_PLUGIN_ID = "com.aptana.radrails.explorer"; //$NON-NLS-1$
	
	private Process _process;
	private ProcessReader _stdout;
	private ProcessReader _stderr;
	private ProcessWriter _stdin;
	private StringBuffer _output;
	private String _startingDirectory;
	private boolean _trackMessages = false;
	private List<Message> _messages;
	
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
	 * clearMessages
	 */
	public void clearMessages()
	{
		if (this._messages != null)
		{
			this._messages.clear();
		}
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
			result = new String[] { "/K", "cd" }; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return result;
	}
	
	/**
	 * getMessages
	 * 
	 * @return
	 */
	public Message[] getMessages()
	{
		Message[] result;
		
		if (this._messages != null)
		{
			result = this._messages.toArray(new Message[this._messages.size()]);
			
			Arrays.sort(result, new Comparator<Message>()
			{
				public int compare(Message o1, Message o2)
				{
					return o1.timestamp.compareTo(o2.timestamp);
				}
			});
		}
		else
		{
			result = new Message[0];
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
		String OSARCH = Platform.getOSArch();

		String result = null;
		
		if (OS.equals(Platform.OS_WIN32))
		{
			result = "cmd.exe"; //$NON-NLS-1$
		}
		else if (OS.equals(Platform.OS_MACOSX) || OS.equals(Platform.OS_LINUX))
		{
			URL url;
			if (OS.equals(Platform.OS_MACOSX)) {
				url = FileLocator.find(Activator.getDefault().getBundle(), new Path("redtty"), null); //$NON-NLS-1$
			} else {
				url = FileLocator.find(Activator.getDefault().getBundle(), new Path("redtty."+ OS + "." + OSARCH), null); //$NON-NLS-1$ //$NON-NLS-2$
			}
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
		if (this._startingDirectory == null)
		{
			String value = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID, ACTIVE_PROJECT_PROPERTY, null, null);
			IProject project = null;
			
			if (value != null)
			{
				project = ResourcesPlugin.getWorkspace().getRoot().getProject(value);
				
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
			
			if (this._trackMessages)
			{
				this.logMessage("<", result); //$NON-NLS-1$
			}
		}

		return result;
	}

	/**
	 * logMessage
	 * 
	 * @param name
	 * @param content
	 */
	protected void logMessage(String name, String content)
	{
		if (this._messages == null)
		{
			this._messages = new ArrayList<Message>();
		}
		
		this._messages.add(new Message(name, content));
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
			
			if (this._trackMessages)
			{
				this.logMessage(">", text); //$NON-NLS-1$
			}
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
		if (this._messages != null)
		{
			for (Message message : this.getMessages())
			{
				if (message.content != null && message.content.length() > 0)
				{
					System.out.println(message.toString());
				}
			}
		}
		
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
	 * trackMessages
	 * 
	 * @param value
	 */
	public void trackMessages(boolean value)
	{
		this._trackMessages = value;
	}
}

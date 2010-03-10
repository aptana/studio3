package com.aptana.terminal.server;

import java.io.File;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * BuiltinCygwinConfiguration
 */
public class WindowsCmdConfiguration extends WindowsCygwinConfiguration
{
	/**
	 * BuiltinCygwinConfiguration
	 */
	public WindowsCmdConfiguration()
	{
	}
	
	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#afterStart(com.aptana.terminal.server.ProcessWrapper)
	 */
	@Override
	public void afterStart(ProcessWrapper wrapper)
	{
		// Turn on filtering
		String marker = UUID.randomUUID().toString();
		Pattern filter = Pattern.compile("^" + marker + "[\\r\\n]+", Pattern.MULTILINE);
		
		wrapper.setStandardOutputFilter(filter);
		
		// Set current directory, if needed
		String startingDirectory = wrapper.getStartingDirectory();
		
		if (startingDirectory != null && startingDirectory.length() > 0)
		{
			File dir = new File(startingDirectory);
			
			if (dir.exists())
			{
				wrapper.sendText("cd \"`cygpath \"" + dir.getAbsolutePath() + "\"`\"\n");
			}
		}
		
		// startup cmd.exe and turn filtering off
		String command = "cmd.exe /K '@echo " + marker + "'";
		
		wrapper.sendText(command + ";exit\n");
	}
}

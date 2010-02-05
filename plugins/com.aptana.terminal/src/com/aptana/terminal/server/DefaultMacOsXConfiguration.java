package com.aptana.terminal.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;
import com.aptana.util.ResourceUtils;

/**
 * DefaultMacConfiguration
 */
public class DefaultMacOsXConfiguration implements ProcessConfiguration
{
	private static final String REDTTY = "redtty";

	/**
	 * BuiltinCygwinConfiguration
	 */
	public DefaultMacOsXConfiguration()
	{
	}
	
	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#afterStart(com.aptana.terminal.server.ProcessWrapper)
	 */
	@Override
	public void afterStart(ProcessWrapper wrapper)
	{
		// do nothing
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#beforeStart(com.aptana.terminal.server.ProcessWrapper, java.lang.ProcessBuilder)
	 */
	@Override
	public void beforeStart(ProcessWrapper wrapper, ProcessBuilder builder)
	{
		// do nothing
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#getCommandLineArguments()
	 */
	@Override
	public List<String> getCommandLineArguments()
	{
		return new ArrayList<String>();
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#getPlatform()
	 */
	@Override
	public String getPlatform()
	{
		return Platform.OS_MACOSX;
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#getProcessName()
	 */
	@Override
	public String getProcessName()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(REDTTY), null); //$NON-NLS-1$
		
		return ResourceUtils.resourcePathToString(url);
	}

	/**
	 * @see com.aptana.terminal.server.ProcessConfiguration#isValid()
	 */
	@Override
	public boolean isValid()
	{
		Path executable = new Path(REDTTY);
		File file = executable.toFile();
		
		return file.canExecute();
	}

	/*
	 * @see com.aptana.terminal.server.ProcessConfiguration#setupEnvironment(java.util.Map)
	 */
	@Override
	public void setupEnvironment(Map<String, String> env)
	{
		env.put("TERM", "xterm-color"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}

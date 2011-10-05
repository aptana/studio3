/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.aptana.configurations.ConfigurationsPlugin;
import com.aptana.configurations.ConfigurationsUtil;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ProcessUtil;

/**
 * A abstract configuration processor delegate that provides some common operations of running a command and retrieving
 * its output.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class AbstractProcessorDelegate implements IConfigurationProcessorDelegate
{
	protected Map<String, String> supportedCommands;
	protected Expression enablementExpression;

	/**
	 * Constructs a new processor delegate.
	 */
	public AbstractProcessorDelegate()
	{
		supportedCommands = new HashMap<String, String>(5);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorDelegate#getSupportedCommands()
	 */
	public Set<String> getSupportedCommands()
	{
		return Collections.unmodifiableSet(supportedCommands.keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.configurations.processor.IConfigurationProcessorDelegate#setEnablement(org.eclipse.core.expressions
	 * .Expression)
	 */
	public void setEnablement(Expression enablementExpression)
	{
		this.enablementExpression = enablementExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorDelegate#isEnabled()
	 */
	public boolean isEnabled()
	{
		return ConfigurationsUtil.evaluateEnablement(enablementExpression);
	}

	/**
	 * Provides a common implementation of validating the command type and executing the command on a shell.
	 * 
	 * @param commandType
	 *            One of the supported command types
	 * @param workingDir
	 *            The work directory to run the command from (can be null)
	 * @return The run command output
	 * @throws IllegalArgumentException
	 *             In case the command type is not supported or a shell was not found
	 */
	public Object runCommand(String commandType, IPath workingDir)
	{
		if (!isEnabled())
		{
			throw new IllegalStateException("Command not enabled - " + commandType); //$NON-NLS-1$
		}
		String command = supportedCommands.get(commandType);
		if (command == null)
		{
			throw new IllegalArgumentException("Command not supported - " + commandType); //$NON-NLS-1$
		}
		String shellCommandPath = getShellPath();
		if (shellCommandPath == null)
		{
			throw new IllegalArgumentException("Shell command path is null"); //$NON-NLS-1$
		}
		String versionOutput = null;
		try
		{
			versionOutput = ProcessUtil.outputForProcess(ShellExecutable.run(getSupportedApplication(), workingDir,
					null, command));
		}
		catch (Exception e)
		{
			IdeLog.logError(ConfigurationsPlugin.getDefault(), e, null);
		}
		return versionOutput;
	}

	/**
	 * Return the shell command path.
	 * 
	 * @return The shell path.
	 * @throws IllegalArgumentException
	 *             In case we could not find any such path.
	 */
	protected String getShellPath()
	{
		// Get the shell path
		String shellCommandPath = AbstractConfigurationProcessor.getShellPath();
		if (shellCommandPath == null)
		{
			if (Platform.OS_WIN32.equals(Platform.getOS()))
			{
				// In case we are on Windows, try to get the result by executing 'cmd'
				shellCommandPath = "cmd"; //$NON-NLS-1$
			}
			else
			{
				throw new IllegalArgumentException("Could not locate a shell to run the command"); //$NON-NLS-1$
			}
		}
		return shellCommandPath;
	}

	public abstract String getSupportedApplication();
}

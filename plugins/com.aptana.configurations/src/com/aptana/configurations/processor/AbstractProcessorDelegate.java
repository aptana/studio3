/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.ShellExecutable;
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
		String commandSwitch = "-c"; //$NON-NLS-1$
		if (shellCommandPath.equals("cmd")) { //$NON-NLS-1$
			// Treat it differently
			commandSwitch = "/C"; //$NON-NLS-1$
		}
		command = getSupportedApplication() + ' ' + command;
		// System.out.println(ShellExecutable.getEnvironment());
		String versionOutput = ProcessUtil.outputForCommand(shellCommandPath, workingDir, ShellExecutable.getEnvironment(), new String[] { commandSwitch,
				command });
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

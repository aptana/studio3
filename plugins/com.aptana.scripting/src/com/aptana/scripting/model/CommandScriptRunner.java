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
package com.aptana.scripting.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.scripting.Activator;
import com.aptana.scripting.ScriptLogger;

public class CommandScriptRunner extends AbstractCommandRunner
{
	private int _exitValue;
	private File _tempFile;

	/**
	 * CommandScriptJob
	 */
	public CommandScriptRunner(CommandElement command, CommandContext context)
	{
		super("Execute Command Script", command, context, null); //$NON-NLS-1$
	}

	/**
	 * afterExecute
	 */
	protected void afterExecute()
	{
		if (this._tempFile != null && this._tempFile.exists())
		{
			this._tempFile.delete();
		}
	}

	/**
	 * beforeExecute
	 * 
	 * @throws IOException
	 */
	protected void beforeExecute() throws IOException
	{
		this.createScriptFile();
	}

	/**
	 * createScriptfile
	 * 
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	protected String createScriptFile() throws IOException, FileNotFoundException
	{
		String OS = org.eclipse.core.runtime.Platform.getOS();

		// create temporary file for execution
		this._tempFile = File.createTempFile("command_temp_", ".sh"); //$NON-NLS-1$ //$NON-NLS-2$

		// dump "invoke" content into temp file
		PrintWriter pw = new PrintWriter(this._tempFile);
		pw.print(this.getCommand().getInvoke());
		pw.close();
		return OS;
	}

	/**
	 * getCommandLineArguments
	 * 
	 * @return
	 */
	protected String[] getCommandLineArguments()
	{
		return new String[] { "-l", this._tempFile.getAbsolutePath() }; //$NON-NLS-1$
	}

	/**
	 * executeScript
	 * 
	 * @return
	 */
	public String executeScript()
	{
		IPath shell;
		try
		{
			shell = ShellExecutable.getPath();
		}
		catch (CoreException e)
		{
			Activator.logError(Messages.CommandScriptRunner_CANNOT_LOCATE_SHELL, e);
			this._exitValue = 1;
			this.setExecutedSuccessfully(false);
			return MessageFormat.format(Messages.CommandScriptRunner_UNABLE_TO_LOCATE_SHELL_FOR_COMMAND, new Object[] { this.getCommand().getPath() });
		}
		String[] commandLine = this.getCommandLineArguments();
		String resultText = null;
		String input = IOUtil.read(this.getContext().getInputStream(), "UTF-8"); //$NON-NLS-1$			
		Map<Integer, String> result = ProcessUtil.runInBackground(shell.toOSString(), this.getCommand().getWorkingDirectory(), input, this
			.getContributedEnvironment(), commandLine);

		if (result == null)
		{
			this._exitValue = 1;
			this.setExecutedSuccessfully(false);
		}
		else
		{
			this._exitValue = result.keySet().iterator().next();
			resultText = result.values().iterator().next();
			this.setExecutedSuccessfully(this._exitValue == 0);
		}
		return resultText;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		String resultText = ""; //$NON-NLS-1$

		try
		{
			this.beforeExecute();

			resultText = this.executeScript();
		}
		catch (IOException e)
		{
			ScriptLogger.logError(e.getMessage());
			this._exitValue = 1;
			this.setExecutedSuccessfully(false);
		}
		finally
		{
			afterExecute();
		}

		CommandResult result = new CommandResult(this.getCommand(), this.getContext());
		result.setReturnValue(this._exitValue);
		result.setExecutedSuccessfully(this.getExecutedSuccessfully());
		if (result.executedSuccessfully())
		{
			result.setOutputString(resultText);
		}
		else
		{
			result.setErrorString(resultText);
		}
		// save result
		this.setCommandResult(result);

		return Status.OK_STATUS;
	}
}

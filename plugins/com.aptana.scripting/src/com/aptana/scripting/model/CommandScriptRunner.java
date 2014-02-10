/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingActivator;

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
			IdeLog.logError(ScriptingActivator.getDefault(), Messages.CommandScriptRunner_CANNOT_LOCATE_SHELL, e);
			this._exitValue = 1;
			this.setExecutedSuccessfully(false);
			return MessageFormat.format(Messages.CommandScriptRunner_UNABLE_TO_LOCATE_SHELL_FOR_COMMAND,
					new Object[] { this.getCommand().getPath() });
		}
		String[] commandLine = this.getCommandLineArguments();
		String resultText = null;
		String input = IOUtil.read(this.getContext().getInputStream(), IOUtil.UTF_8);
		List<String> args = new ArrayList<String>(Arrays.asList(commandLine));
		args.add(0, shell.toOSString());
		IStatus result = new ProcessRunner().runInBackground(this.getCommand().getWorkingDirectory(),
				this.getContributedEnvironment(), input, args);

		if (result == null)
		{
			this._exitValue = 1;
			this.setExecutedSuccessfully(false);
		}
		else
		{
			this._exitValue = result.getCode();
			resultText = result.getMessage();
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

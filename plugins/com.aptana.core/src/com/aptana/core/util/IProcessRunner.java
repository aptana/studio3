/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface IProcessRunner
{
	public static final String TEXT_TO_OBFUSCATE = "textToObfuscate"; //$NON-NLS-1$
	/*
	 * When this flag is set in the environment for the process, it hints to redirect the error stream to redirect to
	 * output stream itself.
	 */
	public static final String REDIRECT_ERROR_STREAM = "redirectErrorStream"; //$NON-NLS-1$

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param arguments
	 *            A List of String arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public Process run(String... args) throws IOException, CoreException;

	/**
	 * Runs a command in the workingDir with the passed in arguments. Returns an IStatus. Exit code of the process is
	 * stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus runInBackground(String... args);

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param arguments
	 *            A List of String arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public Process run(IPath workingDirectory, String... arguments) throws IOException, CoreException;

	/**
	 * Runs a command in the workingDir with the passed in arguments. Returns an IStatus. Exit code of the process is
	 * stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param workingDir
	 *            The working directory to use for the process.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus runInBackground(IPath workingDir, String... args);

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param arguments
	 *            A List of String arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public Process run(Map<String, String> environment, String... arguments) throws IOException, CoreException;

	/**
	 * Runs a command in the workingDir with the passed in arguments. Returns an IStatus. Exit code of the process is
	 * stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus runInBackground(Map<String, String> environment, String... args);

	/**
	 * Launches the process and returns a handle to the active Process.
	 * 
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param arguments
	 *            A List of String arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public Process run(IPath workingDirectory, Map<String, String> environment, String... arguments)
			throws IOException, CoreException;

	/**
	 * Runs a command in the workingDir with the passed in arguments. Returns an IStatus. Exit code of the process is
	 * stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param workingDir
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus runInBackground(IPath workingDir, Map<String, String> environment, String... args);

	/**
	 * Launches the process, pipes input to STDIN and returns an IStatus representing the result of execution. The
	 * output of the process is displayed onto progress monitor for each line (as a subTask). Exit code of the process
	 * is stored in the IStatus.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit code makes it an
	 * IStatus with ERROR severity. Otherwise it uses OK severity. This version of invoking is intended for commands
	 * that require piping input to STDIN, typically used for commands run under sudo where we pass the password to
	 * STDIN. If the input is null or empty, then nothing will be piped to STDIN. This will block waiting for the
	 * process to complete. If the process is blocked on reads a cancelled progress monitor may not be honored and will
	 * remain blocked!
	 * 
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param input
	 *            String input to pipe to STDIN after launching the process.
	 * @param monitor
	 *            Progress monitor to display the output of the progress
	 * @param arguments
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus run(IPath workingDirectory, Map<String, String> environment, char[] input, List<String> args,
			IProgressMonitor monitor);

	/**
	 * Launches the process, pipes input to STDIN and returns an IStatus representing the result of execution. Exit code
	 * of the process is stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit
	 * code makes it an IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param input
	 *            String input to pipe to STDIN after launching the process.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus runInBackground(IPath workingDirectory, Map<String, String> environment, String input,
			List<String> args);

	/**
	 * Launches the process, pipes input to STDIN and returns an IStatus representing the result of execution. Exit code
	 * of the process is stored in the IStatuse.getCode(). Output is stored in IStatus.getMessage(). A non-zero exit
	 * code makes it an IStatus with ERROR severity. Otherwise it uses OK severity.
	 * 
	 * @param input
	 *            String input to pipe to STDIN after launching the process.
	 * @param workingDirectory
	 *            The working directory to use for the process.
	 * @param environment
	 *            Environment variable map to use for the process.
	 * @param redirect
	 *            Whether the output is redirected to a temporary file and read from that.
	 * @param args
	 *            A List of String arguments to the command.
	 * @return
	 */
	public IStatus runInBackground(IPath workingDirectory, Map<String, String> environment, String input,
			boolean redirect, List<String> args);
}

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
package com.aptana.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.CorePlugin;
import com.aptana.core.internal.InputStreamGobbler;
import com.aptana.core.internal.OutputStreamThread;

/**
 * A Utility for launching process synch and async via ProcessBuilder. Does not go through the Eclipse launching
 * infrastructure or our terminal!
 * 
 * @author cwilliams
 */
public abstract class ProcessUtil
{

	public static String outputForCommand(String command, IPath workingDir, String... args)
	{
		return outputForCommand(command, workingDir, null, args);
	}

	public static String outputForCommand(String command, IPath workingDir, Map<String, String> env, String... args)
	{
		Map<Integer, String> result = runInBackground(command, workingDir, env, args);
		if (result == null || result.isEmpty())
			return null;
		return result.values().iterator().next();
	}

	public static String read(InputStream stream)
	{
		return IOUtil.read(stream, "UTF-8"); //$NON-NLS-1$
	}

	public static Map<Integer, String> runInBackground(String command, IPath workingDir, String... args)
	{
		return runInBackground(command, workingDir, null, args);
	}

	public static Map<Integer, String> runInBackground(String command, IPath workingDir, Map<String, String> env,
			String... args)
	{
		return runInBackground(command, workingDir, null, env, args);
	}

	public static String outputForProcess(Process process)
	{
		Map<Integer, String> result = processData(process, null);
		if (result == null || result.isEmpty())
			return null;
		return result.values().iterator().next();
	}

	private static Map<Integer, String> processData(Process process, String input)
	{
		String lineSeparator = ResourceUtil.getLineSeparatorValue(null);
		try
		{
			// Read and write in threads to avoid from choking the process streams
			OutputStreamThread writerThread = null;
			if (input != null)
			{
				// TODO - Use EditorUtils.getEncoding once we have an IFile reference.
				// Using the UTF-8 will not work for all cases.
				writerThread = new OutputStreamThread(process.getOutputStream(), input, "UTF-8"); //$NON-NLS-1$
			}
			InputStreamGobbler readerGobbler = new InputStreamGobbler(process.getInputStream(), lineSeparator, "UTF-8"); //$NON-NLS-1$
			InputStreamGobbler errorGobbler = new InputStreamGobbler(process.getErrorStream(), lineSeparator, null);

			// Start the threads
			if (writerThread != null)
			{
				writerThread.start();
			}
			readerGobbler.start();
			errorGobbler.start();
			// This will wait till the process is done.
			int exitValue = process.waitFor();
			if (writerThread != null)
			{
				writerThread.interrupt();
				writerThread.join();
			}
			readerGobbler.interrupt();
			errorGobbler.interrupt();
			readerGobbler.join();
			errorGobbler.join();

			String read = readerGobbler.getResult();
			if (exitValue != 0 && (read == null || read.trim().length() == 0))
			{
				read = errorGobbler.getResult();
			}
			else
			{
				if (read != null && read.endsWith("\n")) //$NON-NLS-1$
				{
					read = read.substring(0, read.length() - 1);
				}
			}
			Map<Integer, String> result = new HashMap<Integer, String>();
			result.put(exitValue, read);
			return result;
		}
		catch (InterruptedException e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		return null;		
	}

	/**
	 * Launches the process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param command
	 * @param workingDir
	 * @param input
	 * @param env
	 * @param args
	 * @return
	 */
	public static Map<Integer, String> runInBackground(String command, IPath workingDirectory, String input,
			Map<String, String> environment, String... arguments)
	{
		try
		{
			Process p = run(command, workingDirectory, environment, arguments);
			return processData(p, input);
		}
		catch (IOException e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		catch (CoreException e)
		{
			CorePlugin.logError(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Launches the process and returns a handle to the active Process.
	 * @param command
	 * @param workingDirectory
	 * @param environment
	 * @param arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static Process run(String command, IPath workingDirectory, Map<String,String> environment, String... arguments) throws IOException, CoreException {
		List<String> commands = new ArrayList<String>(Arrays.asList(arguments));
		commands.add(0, command);
		return run(commands, workingDirectory, environment);
	}

	/**
	 * 
	 * @param command
	 * @param workingDirectory
	 * @param arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static Process run(String command, IPath workingDirectory, String... arguments) throws IOException, CoreException {
		return run(command, workingDirectory, null, arguments);
	}

	/**
	 * Launches the process and returns a handle to the active Process.
	 * @param command
	 * @param workingDirectory
	 * @param environment
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static Process run(List<String> command, IPath workingDirectory, Map<String,String> environment) throws IOException, CoreException {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		if (workingDirectory != null) {
			processBuilder.directory(workingDirectory.toFile());
		}
		if (environment != null && !environment.isEmpty()) {
			processBuilder.environment().putAll(environment);
		}
		return processBuilder.start();
	}

}

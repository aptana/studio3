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

package com.aptana.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 *
 */
public class ShellExecutable {

	private static final String[] POSSIBLE_SHELL_LOCATIONS_WIN32 = new String[] {
		"%PROGRAMW6432%\\Git\\bin", //$NON-NLS-1$
		"%PROGRAMFILES%\\Git\\bin", //$NON-NLS-1$
		"%PROGRAMFILES(X86)%\\Git\\bin" //$NON-NLS-1$
	};
	
	public static final String PATH_SEPARATOR = ":"; //$NON-NLS-1$
	
	private static final String SH_EXE = "sh.exe"; //$NON-NLS-1$
	private static final String BASH = "bash"; //$NON-NLS-1$
	
	private static boolean initilizing = false;
	private static IPath shellPath = null;
	private static Map<String, String> shellEnvironment;
	
	
	/**
	 * 
	 */
	private ShellExecutable() {
	}
	
	public static synchronized IPath getPath() throws CoreException {
		if (shellPath == null) {
			boolean isWin32 = Platform.OS_WIN32.equals(Platform.getOS());
			try {
				initilizing = true;
				shellPath = getPreferenceShellPath();
				if (shellPath == null) {
					shellPath = ExecutableUtil.find(isWin32 ? SH_EXE : BASH, false, getPossibleShellLocations());
				}
			} finally {
				initilizing = false;
			}
			if (shellPath == null) {
				throw new CoreException(new Status(Status.ERROR, CorePlugin.PLUGIN_ID, "Shell executable could not be found.")); //$NON-NLS-1$
			}
		}
		return shellPath;
	}

	private static List<IPath> getPossibleShellLocations() {	
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			List<IPath> list = new ArrayList<IPath>();
			for (String location : POSSIBLE_SHELL_LOCATIONS_WIN32) {
				IPath path = Path.fromOSString(PlatformUtil.expandEnvironmentStrings(location));
				if (path.toFile().isDirectory()) {
					list.add(path);
				}
			}
			return list;
		}
		return null;
	}
	
	private static IPath getPreferenceShellPath() {
		String pref = new InstanceScope().getNode(CorePlugin.PLUGIN_ID).get(ICorePreferenceConstants.SHELL_EXECUTABLE_PATH, null);
		if (pref != null && !StringUtil.isEmpty(pref)) {
			IPath path = Path.fromOSString(pref);
			if (path.toFile().isDirectory()) {
				boolean isWin32 = Platform.OS_WIN32.equals(Platform.getOS());
				path = path.append(isWin32 ? SH_EXE : BASH);
			}
			if (ExecutableUtil.isExecutable(path)) {
				return path;
			}
			CorePlugin.logWarning("Shell executable path preference point to an invalid location"); //$NON-NLS-1$
		}
		return null;
	}
	
	public static void setPreferenceShellPath(IPath path) {
		IEclipsePreferences prefs = new InstanceScope().getNode(CorePlugin.PLUGIN_ID);
		if (path != null) {
			prefs.put(ICorePreferenceConstants.SHELL_EXECUTABLE_PATH, path.toOSString());			
		} else {
			prefs.remove(ICorePreferenceConstants.SHELL_EXECUTABLE_PATH);
		}
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			CorePlugin.logError("Saving preferences failed.", e); //$NON-NLS-1$
		}
		shellPath = null;
		shellEnvironment = null;
	}
	
	public synchronized static Map<String, String> getEnvironment() {
		if (shellEnvironment == null) {
			shellEnvironment = new HashMap<String, String>();			
			try {
				shellEnvironment.putAll(buildEnvironment(ProcessUtil.outputForProcess(run("env", null, null)))); //$NON-NLS-1$
			} catch (Exception e) {
				CorePlugin.logError("Get shell environment failed.", e); //$NON-NLS-1$
			}
		}
		return shellEnvironment;
	}
	
	private static Map<String, String> buildEnvironment(String envp) {
		Map<String, String> env = new HashMap<String, String>();
		StringTokenizer tok = new StringTokenizer(envp, "\r\n"); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			String envstring = tok.nextToken();
			int eqlsign = envstring.indexOf('=');
			if (eqlsign != -1) {
				env.put(envstring.substring(0,eqlsign), envstring.substring(eqlsign+1));
			}
		}
		env.remove("_"); //$NON-NLS-1$
		return env;
	}
	
	private synchronized static List<String> toShellCommand(List<String> command) throws CoreException {
		if (initilizing) {
			return command;
		}
		List<String> shellCommand = new ArrayList<String>();
		shellCommand.add(getPath().toOSString());
		shellCommand.add("--login"); //$NON-NLS-1$
		shellCommand.add("-c"); //$NON-NLS-1$
		StringBuffer sb = new StringBuffer();
		for (String arg : command) {
			sb.append(arg.replaceAll("\"|\'", "\\$0")).append(' '); //$NON-NLS-1$ //$NON-NLS-2$
		}
		shellCommand.add(sb.toString().trim());
		return shellCommand;
	}
	
	public static List<String> toShellCommand(String command, String... arguments) throws CoreException {
		List<String> commands = new ArrayList<String>(Arrays.asList(arguments));
		commands.add(0, command);
		return toShellCommand(commands);
	}
			
	public static Process run(List<String> command, IPath workingDirectory, Map<String,String> environment) throws IOException, CoreException {
		ProcessBuilder processBuilder = new ProcessBuilder(toShellCommand(command));
		if (workingDirectory != null) {
			processBuilder.directory(workingDirectory.toFile());
		}
		if (environment != null && !environment.isEmpty()) {
			processBuilder.environment().putAll(environment);
		}
		return processBuilder.start();
	}

	public static Process run(List<String> command, IPath workingDirectory, String[] envp) throws IOException, CoreException {
		command = toShellCommand(command);
		return Runtime.getRuntime().exec(command.toArray(new String[command.size()]), envp, workingDirectory.toFile());
	}

	public static Process run(String command, IPath workingDirectory, Map<String,String> environment, String... arguments) throws IOException, CoreException {
		List<String> commands = new ArrayList<String>(Arrays.asList(arguments));
		commands.add(0, command);
		return run(commands, workingDirectory, environment);
	}

	public static Process run(IPath executablePath, IPath workingDirectory, Map<String,String> environment, String... arguments) throws IOException, CoreException {
		return run(executablePath.toOSString(), workingDirectory, environment, arguments);
	}

}

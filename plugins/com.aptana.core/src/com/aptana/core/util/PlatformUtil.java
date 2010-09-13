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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;
import com.aptana.core.internal.platform.CoreMacOSX;
import com.aptana.core.internal.platform.CoreNatives;

/**
 * @author Max Stepanov
 */
public final class PlatformUtil
{
	
	public static class ProcessItem {
		private String executableName;
		private int pid;
		private int parentId;
		
		private ProcessItem(String executableName, int pid, int parentId) {
			this.executableName = executableName;
			this.pid = pid;
			this.parentId = parentId;
		}

		public String getExecutableName() {
			return executableName;
		}

		public int getPid() {
			return pid;
		}

		@Override
		public String toString() {
			return "["+pid+"] "+executableName; //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * DESKTOP_DIRECTORY
	 */
	public static final String DESKTOP_DIRECTORY = "%DesktopDirectory%"; //$NON-NLS-1$

	/**
	 * LOCAL_APPDATA
	 */
	public static final String LOCAL_APPDATA = "%LOCAL_APPDATA%"; //$NON-NLS-1$


	/**
	 * DOCUMENTS_DIRECTORY
	 */
	public static final String DOCUMENTS_DIRECTORY = "%DocumentsDirectory%"; //$NON-NLS-1$

	/**
	 * HOME_DIRECTORY
	 */
	public static final String HOME_DIRECTORY = Platform.OS_WIN32.equals(Platform.getOS()) ? "%USERPROFILE%" : "~"; //$NON-NLS-1$ //$NON-NLS-2$	
	
	private PlatformUtil()
	{
	}

	/**
	 * getRunningProcesses
	 * 
	 * @return processes list as ProcessItem[]
	 */
	public static ProcessItem[] getRunningProcesses()
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			try
			{
				Object[] namesWithIDs = CoreNatives.GetProcessList();
				if (namesWithIDs != null) {
					List<ProcessItem> list = new ArrayList<ProcessItem>();
					for (int i = 0; i < namesWithIDs.length-2; i+=3) {
						list.add(new ProcessItem((String)namesWithIDs[i],	/* executable */
								((Integer)namesWithIDs[i+1]).intValue(),	/* pid */
								((Integer)namesWithIDs[i+2]).intValue()));	/* ppid*/
					}
					return list.toArray(new ProcessItem[list.size()]);
				}
			}
			catch (UnsatisfiedLinkError e)
			{
				CorePlugin.log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
						Messages.PlatformUtils_CoreLibraryNotFound, e));
			}
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS()))
		{
			Process process = null;
			try
			{
				process = Runtime.getRuntime().exec("/bin/ps --no-headers xo pid:1,ppid:1,args"); //$NON-NLS-1$
				InputStream in = process.getInputStream();
				LineNumberReader reader = new LineNumberReader(new InputStreamReader(in, "ISO-8859-1")); //$NON-NLS-1$
				String line;
				List<ProcessItem> list = new ArrayList<ProcessItem>();
				while ((line = reader.readLine()) != null)
				{
					int index = line.indexOf(' ');
					if (index > 0) {
						int pid = Integer.parseInt(line.substring(0, index));
						line = line.substring(index+1).trim();
						index = line.indexOf(' ');
						if (index > 0) {
							int ppid = Integer.parseInt(line.substring(0, index));
							line = line.substring(index+1).trim();
							index = line.indexOf(' ');
							if (index > 0) {
								line = line.substring(0, index);
							}
							list.add(new ProcessItem(line.trim(), pid, ppid));
						}
					}
				}
				return list.toArray(new ProcessItem[list.size()]);
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (process != null)
				{
					process.destroy();
				}
			}
		}
		else if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			Process process = null;
			Process process2 = null;
			try
			{
				process = Runtime.getRuntime().exec("/usr/bin/perl", new String[] { "VERSIONER_PERL_PREFER_32_BIT=yes" }); //$NON-NLS-1$ //$NON-NLS-2$
				InputStream in = process.getInputStream();
				OutputStream out = process.getOutputStream();
				String command = "use Mac::Processes; while ( ($psn, $psi) = each(%Process) ) { print GetProcessPID($psi->processNumber).\" \".$psi->processAppSpec.\"\\n\"; }"; //$NON-NLS-1$
				out.write(command.getBytes("ISO-8859-1")); //$NON-NLS-1$
				out.close();
				LineNumberReader reader = new LineNumberReader(new InputStreamReader(in, "ISO-8859-1")); //$NON-NLS-1$

				process2 = Runtime.getRuntime().exec("/bin/ps xo pid=,ppid=,command="); //$NON-NLS-1$
				LineNumberReader reader2 = new LineNumberReader(new InputStreamReader(process2.getInputStream(), "ISO-8859-1")); //$NON-NLS-1$
				Map<Integer, Integer> pid2ppid = new HashMap<Integer, Integer>();
				Map<Integer, String> pid2command = new HashMap<Integer, String>();
				String line;
				while ((line = reader2.readLine()) != null) {
					line = line.trim();
					int index = line.indexOf(' ');
					if (index > 0) {
						int pid = Integer.parseInt(line.substring(0, index));
						line = line.substring(index+1).trim();
						index = line.indexOf(' ');
						int ppid = Integer.parseInt(line.substring(0, index));
						line = line.substring(index+1).trim();
						pid2ppid.put(pid, ppid);
						pid2command.put(pid, line.trim());
					}
				}

				List<ProcessItem> list = new ArrayList<ProcessItem>();
				while ((line = reader.readLine()) != null)
				{
					int index = line.indexOf(' ');
					if (index > 0) {
						int pid = Integer.parseInt(line.substring(0, index));
						pid2command.put(pid, line.substring(index+1).trim());
					}
				}
				for (int pid : pid2command.keySet()) {
					Integer ppid = pid2ppid.get(pid);
					list.add(new ProcessItem(pid2command.get(pid), pid, ppid != null ? ppid.intValue() : 0));
				}
				return list.toArray(new ProcessItem[list.size()]);
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (process != null)
				{
					process.destroy();
				}
				if (process2 != null)
				{
					process2.destroy();
				}
			}
		}
		return new ProcessItem[0];
	}

	/**
	 * getRunningChildProcesses
	 * 
	 * @return child processes list as ProcessItem[]
	 */
	public static ProcessItem[] getRunningChildProcesses() {
		int currentPid = 0;
		ProcessItem[] allProcesses = getRunningProcesses();
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			try
			{
				currentPid = CoreNatives.GetCurrentProcessId();
			}
			catch (UnsatisfiedLinkError e)
			{
				CorePlugin.log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
						Messages.PlatformUtils_CoreLibraryNotFound, e));
			}
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS())
				|| Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			Process process = null;
			try
			{
				process = Runtime.getRuntime().exec("/bin/ps xo ppid=,command="); //$NON-NLS-1$
				InputStream in = process.getInputStream();
				LineNumberReader reader = new LineNumberReader(new InputStreamReader(in, "ISO-8859-1")); //$NON-NLS-1$
				String line;
				while ((line = reader.readLine()) != null)
				{
					line = line.trim() ;
					int index = line.indexOf(' ');
					if (index > 0) {
						try {
							int pid = Integer.parseInt(line.substring(0, index));
							line = line.substring(index+1).trim();
							if (line.startsWith("/bin/ps xo ppid")) { //$NON-NLS-1$
								currentPid = pid;
								break;
							}
						} catch (NumberFormatException e) {
						}
					}
				}
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (process != null)
				{
					process.destroy();
				}
			}

		}

		if (currentPid  != 0) {
			List<ProcessItem> list = new ArrayList<ProcessItem>();
			for (int i = 0; i < allProcesses.length; ++i) {
				if (allProcesses[i].parentId == currentPid && allProcesses[i].pid != currentPid) {
					list.add(allProcesses[i]);
				}
			}
			return list.toArray(new ProcessItem[list.size()]);
		}

		return new ProcessItem[0];
	}

	public static void killProcess(int pid) {
		if (pid == 0) {
			return;
		}
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			try {
				CoreNatives.KillProcess(pid);
			}
			catch (UnsatisfiedLinkError e) {
				CorePlugin.log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
						Messages.PlatformUtils_CoreLibraryNotFound, e));
			}
		} else if (Platform.OS_LINUX.equals(Platform.getOS())
				|| Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			try {
				Runtime.getRuntime().exec("/bin/kill -9 "+(((long)pid)&0xFFFFFFFF)); //$NON-NLS-1$
			} catch (IOException e) {
			}
		}
	}

	/**
	 * expandEnvironmentStrings
	 * 
	 * @param path
	 * @return expanded environment variable
	 */
	public static synchronized String expandEnvironmentStrings(String path)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			String expanded = CoreNatives.ExpandEnvironmentStrings(path);
			if (expanded != null)
			{
				path = expanded;
			}
			if (path.startsWith(DESKTOP_DIRECTORY))
			{
				String desktopDirectory = CoreNatives.GetSpecialFolderPath(CoreNatives.CSIDL_DESKTOPDIRECTORY);
				if (desktopDirectory != null)
				{
					path = desktopDirectory + path.substring(DESKTOP_DIRECTORY.length());
				}
			} else if (path.startsWith(LOCAL_APPDATA))
			{
				String localAppData = CoreNatives.GetSpecialFolderPath(CoreNatives.CSIDL_LOCAL_APPDATA);
				if (localAppData != null)
				{
					path = localAppData + path.substring(LOCAL_APPDATA.length());
				}
			}
		} else if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			if (path.startsWith(DESKTOP_DIRECTORY)) {
				String desktopDirectory = CoreMacOSX.FileManager_findFolder(true, CoreMacOSX.kDesktopFolderType);
				if (desktopDirectory != null) {
					path = desktopDirectory + path.substring(DESKTOP_DIRECTORY.length());
				}
			} else if (path.startsWith(DOCUMENTS_DIRECTORY)) {
				String docsDirectory = CoreMacOSX.FileManager_findFolder(true, CoreMacOSX.kDocumentsFolderType);
				if (docsDirectory != null) {
					path = docsDirectory + path.substring(DOCUMENTS_DIRECTORY.length());
				}
			} else if (path.startsWith(HOME_DIRECTORY)) {
				String homeDirectory = CoreMacOSX.FileManager_findFolder(true, CoreMacOSX.kCurrentUserFolderType);
				if (homeDirectory != null) {
					path = homeDirectory + path.substring(HOME_DIRECTORY.length());
				}
			}
		}
		if (path.startsWith("~")) { //$NON-NLS-1$
			String home = System.getProperty("user.home"); //$NON-NLS-1$
			if (home != null)
			{
				return home + path.substring(1);
			}
		}
		return path;
	}

	/**
	 * queryRegestryStringValue
	 * 
	 * @param keyName
	 * @param valueName
	 * @return value of regestry key
	 */
	public static String queryRegestryStringValue(String keyName, String valueName)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			long hRootKey;
			if ( keyName.startsWith("HKCR\\") || keyName.startsWith("HKEY_CLASSES_ROOT\\") ) { //$NON-NLS-1$ //$NON-NLS-2$
				hRootKey = CoreNatives.HKEY_CLASSES_ROOT;
			} else if ( keyName.startsWith("HKLM\\") || keyName.startsWith("HKEY_LOCAL_MACHINE\\") ) { //$NON-NLS-1$ //$NON-NLS-2$
				hRootKey = CoreNatives.HKEY_LOCAL_MACHINE;
			} else if ( keyName.startsWith("HKCU\\") || keyName.startsWith("HKEY_CURRENT_USER\\") ) { //$NON-NLS-1$ //$NON-NLS-2$
				hRootKey = CoreNatives.HKEY_CURRENT_USER;
			} else {
				throw new IllegalArgumentException("Invalid regestry key name"); //$NON-NLS-1$
			}
			keyName = keyName.substring(keyName.indexOf('\\')+1);
			long[] hKey = new long[1];
			if ( CoreNatives.RegOpenKey(hRootKey, keyName, CoreNatives.KEY_READ, hKey) ) {
				String[] result = new String[1];
				CoreNatives.RegQueryValue(hKey[0], valueName, result);
				CoreNatives.RegCloseKey(hKey[0]);
				return result[0];
			}
		}
		return null;
	}

	/**
	 * setRegestryStringValue
	 * 
	 * @param keyName
	 * @param valueName
	 * @param value
	 * @return result of operation
	 */
	public static boolean setRegestryStringValue(String keyName, String valueName, String value)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			long hRootKey;
			if ( keyName.startsWith("HKCR\\") || keyName.startsWith("HKEY_CLASSES_ROOT\\") ) { //$NON-NLS-1$ //$NON-NLS-2$
				hRootKey = CoreNatives.HKEY_CLASSES_ROOT;
			} else if ( keyName.startsWith("HKLM\\") || keyName.startsWith("HKEY_LOCAL_MACHINE\\") ) { //$NON-NLS-1$ //$NON-NLS-2$
				hRootKey = CoreNatives.HKEY_LOCAL_MACHINE;
			} else if ( keyName.startsWith("HKCU\\") || keyName.startsWith("HKEY_CURRENT_USER\\") ) { //$NON-NLS-1$ //$NON-NLS-2$
				hRootKey = CoreNatives.HKEY_CURRENT_USER;
			} else {
				throw new IllegalArgumentException("Invalid regestry key name"); //$NON-NLS-1$
			}
			keyName = keyName.substring(keyName.indexOf('\\')+1);
			long[] hKey = new long[1];
			if ( CoreNatives.RegCreateKey(hRootKey, keyName, CoreNatives.KEY_WRITE, hKey) ) {				
				boolean result = CoreNatives.RegSetValue(hKey[0], valueName, value);
				CoreNatives.RegCloseKey(hKey[0]);
				return result;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean isUserAdmin() {
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			return CoreNatives.IsUserAnAdmin();
		}
		return false;
	}
	
	public static boolean runAsAdmin(String program, String[] arguments) {
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			StringBuffer sb = null;
			if (arguments != null && arguments.length > 0) {
				sb = new StringBuffer();
				for(int i = 0; i < arguments.length; ++i) {
					String arg = arguments[i];
					if (arg.indexOf(' ') != -1) {
						sb.append('"').append(arg).append('"');
					} else {
						sb.append(arg);
					}
					sb.append(' ');
				}
			}
			return CoreNatives.ShellExecuteEx(program, sb != null ? sb.toString() : null,
					"runas", null, CoreNatives.SW_SHOWNORMAL); //$NON-NLS-1$
		}
		return false;		
	}
	
	/**
	 * Returns application info by name
	 * @param applicationPath
	 * @param name
	 * @return
	 */
	public static String getApplicationInfo(String applicationPath, String name) {
		if(Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			File dir = new File(applicationPath);
			if (dir.isDirectory()) {
				File plist = new File(dir, "Contents/Info.plist"); //$NON-NLS-1$
				if (plist.exists()) {
					LineNumberReader r = null;
					String keyString = "<key>"+name+"</key>"; //$NON-NLS-1$ //$NON-NLS-2$
					try {
						r = new LineNumberReader(new FileReader(plist));
						String line;
						while ((line = r.readLine()) != null) {
							if (line.indexOf(keyString) != -1) {
								line = r.readLine();
								String stag = "<string>"; //$NON-NLS-1$
								String etag = "</string>"; //$NON-NLS-1$
								int start = line.indexOf(stag);
								if (start != -1) {
									start += stag.length();
									int end = line.indexOf(etag, start);
									if (end != -1) {
										return line.substring(start, end);
									}
								}
							}
						}
					} catch (IOException e) {
						CorePlugin.log(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID,
								MessageFormat.format("Reading {0} fails", plist.getAbsolutePath()), e)); //$NON-NLS-1$
					} finally {
						if (r != null) {
							try {
								r.close();
							} catch (IOException ignore) {
							}
						}
					}
				}
			}
			return ""; //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Returns full path to platform-specific application executable
	 * @param applicationPath
	 * @return
	 */
	public static File getApplicationExecutable(String applicationPath) {
		File file = new File(applicationPath);
		if(Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			if (!file.isDirectory()) {
				return file;
			}
			String executable = getApplicationInfo(applicationPath,"CFBundleExecutable"); //$NON-NLS-1$
			if (executable != null) {
				File file2 = new File(file, "Contents/MacOSX/" + executable); //$NON-NLS-1$
				if (file2.exists()) {
					return file2;
				}
				file2 = new File(file, "Contents/MacOS/" + executable); //$NON-NLS-1$
				if (file2.exists()) {
					return file2;
				}
			}
		}

		return file;
	}
}

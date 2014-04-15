/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.environment.Constants;

import com.aptana.core.CorePlugin;
import com.aptana.core.internal.platform.CoreMacOSX;
import com.aptana.core.internal.platform.CoreNatives;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class PlatformUtil
{

	private static final ProcessItem[] NO_PROCESS_ITEMS = new ProcessItem[0];

	public static class ProcessItem
	{
		private String executableName;
		private int pid;
		private int parentId;

		private ProcessItem(String executableName, int pid, int parentId)
		{
			this.executableName = executableName;
			this.pid = pid;
			this.parentId = parentId;
		}

		public String getExecutableName()
		{
			return executableName;
		}

		public int getPid()
		{
			return pid;
		}

		@Override
		public String toString()
		{
			return "[" + pid + "] " + executableName; //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	public enum PlatformOs
	{
		WINDOWS, MAC, LINUX
	};

	/**
	 * The platform we're in.
	 */
	public static final PlatformOs platform;

	static
	{
		PlatformOs tempPlatform;
		if (CorePlugin.getDefault() != null)
		{
			String os = Platform.getOS();
			if (os.equals(Constants.OS_WIN32))
			{
				tempPlatform = PlatformOs.WINDOWS;
			}
			else if (os.equals(Constants.OS_MACOSX))
			{
				tempPlatform = PlatformOs.MAC;
			}
			else
			{
				tempPlatform = PlatformOs.LINUX;
			}

		}
		else
		{
			// Platform.getOS() cannot be used without bundles loaded (throws NPE),
			// so, the code-below is used to get the platform when running without bundles loaded.
			String env = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
			if (env.indexOf("win") != -1) //$NON-NLS-1$
			{
				tempPlatform = PlatformOs.WINDOWS;
			}
			else if (env.startsWith("mac os")) //$NON-NLS-1$
			{
				tempPlatform = PlatformOs.MAC;
			}
			else
			{
				tempPlatform = PlatformOs.LINUX;
			}
		}
		platform = tempPlatform; // assign final variable.
	}

	/**
	 * @return whether we are in windows or not
	 */
	public static boolean isWindows()
	{
		return platform == PlatformOs.WINDOWS;
	}

	/**
	 * @return whether we are in MacOs or not
	 */
	public static boolean isMac()
	{
		return platform == PlatformOs.MAC;
	}

	/**
	 * @return whether we are in MacOs or not
	 */
	public static boolean isLinux()
	{
		return platform == PlatformOs.LINUX;
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
	 * COMMON_APPDATA
	 */
	public static final String COMMON_APPDATA = "%COMMON_APPDATA%"; //$NON-NLS-1$

	/**
	 * DOCUMENTS_DIRECTORY
	 */
	public static final String DOCUMENTS_DIRECTORY = "%DocumentsDirectory%"; //$NON-NLS-1$

	/**
	 * HOME_DIRECTORY
	 */
	public static final String HOME_DIRECTORY = isWindows() ? "%USERPROFILE%" : "~"; //$NON-NLS-1$ //$NON-NLS-2$	

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
				if (namesWithIDs != null)
				{
					List<ProcessItem> list = new ArrayList<ProcessItem>();
					for (int i = 0; i < namesWithIDs.length - 2; i += 3)
					{
						list.add(new ProcessItem((String) namesWithIDs[i], /* executable */
						((Integer) namesWithIDs[i + 1]).intValue(), /* pid */
						((Integer) namesWithIDs[i + 2]).intValue())); /* ppid */
					}
					return list.toArray(new ProcessItem[list.size()]);
				}
			}
			catch (UnsatisfiedLinkError e)
			{
				IdeLog.logError(CorePlugin.getDefault(), Messages.PlatformUtils_CoreLibraryNotFound, e);
			}
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS()))
		{
			Process process = null;
			LineNumberReader reader = null;
			try
			{
				process = Runtime.getRuntime().exec("/bin/ps --no-headers xo pid:1,ppid:1,args"); //$NON-NLS-1$
				InputStream in = process.getInputStream();
				reader = new LineNumberReader(new InputStreamReader(in, "ISO-8859-1")); //$NON-NLS-1$
				String line;
				List<ProcessItem> list = new ArrayList<ProcessItem>();
				while ((line = reader.readLine()) != null)
				{
					int index = line.indexOf(' ');
					if (index > 0)
					{
						int pid = Integer.parseInt(line.substring(0, index));
						line = line.substring(index + 1).trim();
						index = line.indexOf(' ');
						if (index > 0)
						{
							int ppid = Integer.parseInt(line.substring(0, index));
							line = line.substring(index + 1).trim();
							index = line.indexOf(' ');
							if (index > 0)
							{
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
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException ignore)
					{
					}
				}
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
			LineNumberReader reader = null;
			LineNumberReader reader2 = null;
			try
			{
				process = Runtime.getRuntime().exec(
						"/usr/bin/perl", new String[] { "VERSIONER_PERL_PREFER_32_BIT=yes" }); //$NON-NLS-1$ //$NON-NLS-2$
				InputStream in = process.getInputStream();
				OutputStream out = process.getOutputStream();
				String command = "use Mac::Processes; while ( ($psn, $psi) = each(%Process) ) { print GetProcessPID($psi->processNumber).\" \".$psi->processAppSpec.\"\\n\"; }"; //$NON-NLS-1$
				out.write(command.getBytes("ISO-8859-1")); //$NON-NLS-1$
				out.close();
				reader = new LineNumberReader(new InputStreamReader(in, "ISO-8859-1")); //$NON-NLS-1$

				process2 = Runtime.getRuntime().exec("/bin/ps xo pid=,ppid=,command="); //$NON-NLS-1$
				reader2 = new LineNumberReader(new InputStreamReader(process2.getInputStream(), "ISO-8859-1")); //$NON-NLS-1$
				Map<Integer, Integer> pid2ppid = new HashMap<Integer, Integer>();
				Map<Integer, String> pid2command = new HashMap<Integer, String>();
				String line;
				while ((line = reader2.readLine()) != null)
				{
					line = line.trim();
					int index = line.indexOf(' ');
					if (index > 0)
					{
						int pid = Integer.parseInt(line.substring(0, index));
						line = line.substring(index + 1).trim();
						index = line.indexOf(' ');
						int ppid = Integer.parseInt(line.substring(0, index));
						line = line.substring(index + 1).trim();
						pid2ppid.put(pid, ppid);
						pid2command.put(pid, line.trim());
					}
				}

				List<ProcessItem> list = new ArrayList<ProcessItem>();
				while ((line = reader.readLine()) != null)
				{
					int index = line.indexOf(' ');
					if (index > 0)
					{
						int pid = Integer.parseInt(line.substring(0, index));
						pid2command.put(pid, line.substring(index + 1).trim());
					}
				}
				for (int pid : pid2command.keySet())
				{
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
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException ignore)
					{
					}
				}
				if (reader2 != null)
				{
					try
					{
						reader2.close();
					}
					catch (IOException ignore)
					{
					}
				}
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
		return NO_PROCESS_ITEMS;
	}

	/**
	 * getRunningChildProcesses
	 * 
	 * @return child processes list as ProcessItem[]
	 */
	public static ProcessItem[] getRunningChildProcesses()
	{
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
				IdeLog.logError(CorePlugin.getDefault(), Messages.PlatformUtils_CoreLibraryNotFound, e);
			}
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS()) || Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			Process process = null;
			LineNumberReader reader = null;
			try
			{
				process = Runtime.getRuntime().exec("/bin/ps xo ppid=,command="); //$NON-NLS-1$
				InputStream in = process.getInputStream();
				reader = new LineNumberReader(new InputStreamReader(in, "ISO-8859-1")); //$NON-NLS-1$
				String line;
				while ((line = reader.readLine()) != null)
				{
					line = line.trim();
					int index = line.indexOf(' ');
					if (index > 0)
					{
						try
						{
							int pid = Integer.parseInt(line.substring(0, index));
							line = line.substring(index + 1).trim();
							if (line.startsWith("/bin/ps xo ppid")) { //$NON-NLS-1$
								currentPid = pid;
								break;
							}
						}
						catch (NumberFormatException e)
						{
						}
					}
				}
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
					}
				}
				if (process != null)
				{
					process.destroy();
				}
			}

		}

		if (currentPid != 0)
		{
			List<ProcessItem> list = new ArrayList<ProcessItem>();
			for (int i = 0; i < allProcesses.length; ++i)
			{
				if (allProcesses[i].parentId == currentPid && allProcesses[i].pid != currentPid)
				{
					list.add(allProcesses[i]);
				}
			}
			return list.toArray(new ProcessItem[list.size()]);
		}

		return NO_PROCESS_ITEMS;
	}

	public static void killProcess(int pid)
	{
		killProcess(pid, 9);
	}

	public static void killProcess(int pid, int signal)
	{
		if (pid == 0)
		{
			return;
		}
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			try
			{
				CoreNatives.KillProcess(pid);
			}
			catch (UnsatisfiedLinkError e)
			{
				IdeLog.logError(CorePlugin.getDefault(), Messages.PlatformUtils_CoreLibraryNotFound, e);
			}
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS()) || Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			try
			{
				Runtime.getRuntime()
						.exec(MessageFormat
								.format("/bin/kill -{0} {1}", Integer.toString(signal & 0x0F), Long.toString((((long) pid) & 0xFFFFFFFF)))); //$NON-NLS-1$
			}
			catch (IOException e)
			{
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
			}
			else if (path.startsWith(LOCAL_APPDATA))
			{
				String localAppData = CoreNatives.GetSpecialFolderPath(CoreNatives.CSIDL_LOCAL_APPDATA);
				if (localAppData != null)
				{
					path = localAppData + path.substring(LOCAL_APPDATA.length());
				}
			}
			else if (path.startsWith(COMMON_APPDATA))
			{
				String commonAppData = CoreNatives.GetSpecialFolderPath(CoreNatives.CSIDL_COMMON_APPDATA);
				if (commonAppData != null)
				{
					path = commonAppData + path.substring(COMMON_APPDATA.length());
				}
			}
		}
		else if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			if (path.startsWith(DESKTOP_DIRECTORY))
			{
				String desktopDirectory = CoreMacOSX.FileManager_findFolder(true, CoreMacOSX.kDesktopFolderType);
				if (desktopDirectory != null)
				{
					path = desktopDirectory + path.substring(DESKTOP_DIRECTORY.length());
				}
			}
			else if (path.startsWith(DOCUMENTS_DIRECTORY))
			{
				String docsDirectory = CoreMacOSX.FileManager_findFolder(true, CoreMacOSX.kDocumentsFolderType);
				if (docsDirectory != null)
				{
					path = docsDirectory + path.substring(DOCUMENTS_DIRECTORY.length());
				}
			}
		}
		if (path.length() > 0 && path.charAt(0) == '~')
		{
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
	public static String queryRegistryStringValue(String keyName, String valueName)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			long hRootKey = getRootKey(keyName);
			keyName = keyName.substring(keyName.indexOf('\\') + 1);
			long[] hKey = new long[1];
			if (CoreNatives.RegOpenKey(hRootKey, keyName, CoreNatives.KEY_READ, hKey))
			{
				String[] result = new String[1];
				CoreNatives.RegQueryValue(hKey[0], valueName, result);
				CoreNatives.RegCloseKey(hKey[0]);
				return result[0];
			}
		}
		return null;
	}

	/**
	 * setRegistryStringValue
	 * 
	 * @param keyName
	 * @param valueName
	 * @param value
	 * @return result of operation
	 */
	public static boolean setRegistryStringValue(String keyName, String valueName, String value)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			long hRootKey = getRootKey(keyName);
			keyName = keyName.substring(keyName.indexOf('\\') + 1);
			long[] hKey = new long[1];
			if (CoreNatives.RegCreateKey(hRootKey, keyName, CoreNatives.KEY_WRITE, hKey))
			{
				boolean result = CoreNatives.RegSetValue(hKey[0], valueName, value);
				CoreNatives.RegCloseKey(hKey[0]);
				return result;
			}
		}
		return false;
	}

	private static long getRootKey(String keyName)
	{
		long hRootKey;
		if (keyName.startsWith("HKCR\\") || keyName.startsWith("HKEY_CLASSES_ROOT\\")) { //$NON-NLS-1$ //$NON-NLS-2$
			hRootKey = CoreNatives.HKEY_CLASSES_ROOT;
		}
		else if (keyName.startsWith("HKLM\\") || keyName.startsWith("HKEY_LOCAL_MACHINE\\")) { //$NON-NLS-1$ //$NON-NLS-2$
			hRootKey = CoreNatives.HKEY_LOCAL_MACHINE;
		}
		else if (keyName.startsWith("HKCU\\") || keyName.startsWith("HKEY_CURRENT_USER\\")) { //$NON-NLS-1$ //$NON-NLS-2$
			hRootKey = CoreNatives.HKEY_CURRENT_USER;
		}
		else
		{
			throw new IllegalArgumentException("Invalid registry key name"); //$NON-NLS-1$
		}
		return hRootKey;
	}

	/**
	 * @return
	 */
	public static boolean isUserAdmin()
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			return CoreNatives.IsUserAnAdmin();
		}
		return false;
	}

	public static boolean runAsAdmin(String program, String[] arguments)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			StringBuffer sb = null;
			if (arguments != null && arguments.length > 0)
			{
				sb = new StringBuffer();
				for (int i = 0; i < arguments.length; ++i)
				{
					String arg = arguments[i];
					if (arg.indexOf(' ') != -1)
					{
						sb.append('"').append(arg).append('"');
					}
					else
					{
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
	 * 
	 * @param applicationPath
	 * @param name
	 * @return
	 */
	public static String getApplicationInfo(String applicationPath, String name)
	{
		if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			File dir = new File(applicationPath);
			if (dir.isDirectory())
			{
				File plist = new File(dir, "Contents/Info.plist"); //$NON-NLS-1$
				if (plist.exists())
				{
					LineNumberReader r = null;
					String keyString = "<key>" + name + "</key>"; //$NON-NLS-1$ //$NON-NLS-2$
					try
					{
						r = new LineNumberReader(new FileReader(plist));
						String line;
						while ((line = r.readLine()) != null)
						{
							if (line.indexOf(keyString) != -1)
							{
								line = r.readLine();
								String stag = "<string>"; //$NON-NLS-1$
								String etag = "</string>"; //$NON-NLS-1$
								int start = line.indexOf(stag);
								if (start != -1)
								{
									start += stag.length();
									int end = line.indexOf(etag, start);
									if (end != -1)
									{
										return line.substring(start, end);
									}
								}
							}
						}
					}
					catch (IOException e)
					{
						IdeLog.logError(CorePlugin.getDefault(),
								MessageFormat.format("Reading {0} fails", plist.getAbsolutePath()), e); //$NON-NLS-1$
					}
					finally
					{
						if (r != null)
						{
							try
							{
								r.close();
							}
							catch (IOException ignore)
							{
							}
						}
					}
				}
			}
			return StringUtil.EMPTY;
		}
		return null;
	}

	/**
	 * Returns the install location of the Studio installation
	 * 
	 * @return
	 */
	public static IPath getInstallLocation()
	{
		return new Path(Platform.getInstallLocation().getURL().getFile());
	}

	/**
	 * Returns full path to platform-specific application executable
	 * 
	 * @param applicationPath
	 * @return
	 */
	public static File getApplicationExecutable(String applicationPath)
	{
		File file = new File(applicationPath);
		if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			if (!file.isDirectory())
			{
				return file;
			}
			String executable = getApplicationInfo(applicationPath, "CFBundleExecutable"); //$NON-NLS-1$
			if (executable != null)
			{
				File file2 = new File(file, "Contents/MacOSX/" + executable); //$NON-NLS-1$
				if (file2.exists())
				{
					return file2;
				}
				file2 = new File(file, "Contents/MacOS/" + executable); //$NON-NLS-1$
				if (file2.exists())
				{
					return file2;
				}
			}
		}
		return file;
	}

	/**
	 * Expands functionality to handle different linux distributions
	 * 
	 * @param targetOsName
	 * @return
	 */
	public static boolean isOSName(String targetOsName)
	{
		String osName = Platform.getOS();

		if (osName.equals(Platform.OS_LINUX))
		{
			// tries to get more precise information for Linux OSes
			try
			{
				File f;
				if ((f = new File("/etc/lsb-release")).canRead()) //$NON-NLS-1$
				{
					// Debian-based distribution; the file has same syntax as Java properties
					Properties props = new Properties();
					props.load(new FileInputStream(f));
					osName = props.getProperty("DISTRIB_ID"); //$NON-NLS-1$
				}
				else if ((f = new File("/etc/redhat-release")).canRead()) //$NON-NLS-1$
				{
					// RedHat-based distribution
					BufferedReader in = null;
					try
					{
						in = new BufferedReader(new FileReader(f));
						String line = in.readLine();
						int index = line.indexOf(" release"); //$NON-NLS-1$
						if (index >= 0)
						{
							osName = line.substring(0, index);
						}
					}
					catch (Exception e)
					{
					}
					finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
								// ignores
							}
						}
					}
				}
				else if ((f = new File("/etc/SuSE-release")).canRead()) //$NON-NLS-1$
				{
					// SuSE-based distribution
					BufferedReader in = null;
					try
					{
						in = new BufferedReader(new FileReader(f));
						osName = in.readLine();
					}
					catch (Exception e)
					{
					}
					finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
								// ignores
							}
						}
					}
				}
			}
			catch (IOException e)
			{
			}
		}

		if (osName != null)
		{
			return osName.equals(targetOsName);
		}

		return false;
	}

}

/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * A class that loads and holds some of the Java environment information.
 */
public class JVMInfo
{
	private static final String PROGRAM_FILES_X86_ENV = "ProgramFiles(x86)"; //$NON-NLS-1$
	private static final String PROGRAM_W6432_ENV = "ProgramW6432"; //$NON-NLS-1$
	private static final String PROGRAM_FILES_ENV = "ProgramFiles"; //$NON-NLS-1$
	private static final String JAVAC = "javac"; //$NON-NLS-1$
	private static final String VERSION_CMD = "-version"; //$NON-NLS-1$

	public static final String JAVA_6_SPECIFICATION = "1.6"; //$NON-NLS-1$

	private boolean isJDKInstalled;
	private boolean is32bit;
	private String javaVersion;
	// The JAVA_HOME value that will be used as an alternative in case the system does not point to a valid one.
	private String javaHome;
	// A JAVA_HOME that was detected when looking for the javac command.
	private String detectedJavaHome;
	private int javaUpdateVersion;
	private IPath javacPath;
	private static JVMInfo instance;

	/**
	 * Returns a JVMInfo instance.
	 * 
	 * @return An instance of the JVMInfo
	 */
	public static synchronized JVMInfo getInstance()
	{
		if (instance == null)
		{
			instance = new JVMInfo();
		}
		return instance;
	}

	/**
	 * Reset the instance.
	 */
	public static synchronized void reset()
	{
		instance = null;
	}

	private JVMInfo()
	{
		load();
	}

	/**
	 * @return the isJavaHomeSet
	 */
	public boolean isJavaHomeSet()
	{
		return !StringUtil.isEmpty(javaHome);
	}

	/**
	 * Returns the detected Java home directory. This value will hold the JAVA_HOME system environment value, in case it
	 * exists. In case it's missing, the value will hold the result of System.getProperty("java.home").
	 * 
	 * @return The detected Java home (can be <code>null</code> if not detected)
	 */
	public String getJavaHome()
	{

		return javaHome;
	}

	/**
	 * @return the is32bit
	 */
	public boolean is32bit()
	{
		return is32bit;
	}

	/**
	 * Returns true if the JVM has a Java6 specification.
	 * 
	 * @return true if the JVM has a Java6 specification; False, otherwise.
	 */
	public boolean isJava6()
	{
		return JAVA_6_SPECIFICATION.equals(getJavaVersion());
	}

	/**
	 * Returns true if the detected Java installation is JDK (has JDK features, such as javac).
	 * 
	 * @return true if the a JDK was detected; False, otherwise.
	 */
	public boolean isJDKInstalled()
	{
		return isJDKInstalled;
	}

	/**
	 * @return the javaVersion
	 */
	public String getJavaVersion()
	{
		return javaVersion;
	}

	/**
	 * @return the javaUpdateVersion
	 */
	public int getJavaUpdateVersion()
	{
		return javaUpdateVersion;
	}

	private void load()
	{
		try
		{
			parseJavaVersion();
			resolveJavaHome();
		}
		catch (Exception e)
		{
			// Catch any exception that we might hit and just log it.
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
	}

	/**
	 * Resolve the Java Home path. The path will be taken from the JAVA_HOME environment variable. In case a value is
	 * missing, we'll try to resolve it via a System.getProperty("java.home") call.
	 */
	private void resolveJavaHome()
	{
		Map<String, String> env = System.getenv();
		String javaHome = env.get("JAVA_HOME"); //$NON-NLS-1$
		if (isValidJavaHome(javaHome))
		{

			this.javaHome = javaHome;
		}
		else if (!StringUtil.isEmpty(detectedJavaHome))
		{
			this.javaHome = detectedJavaHome;
		}
		else
		{
			javaHome = System.getProperty("java.home"); //$NON-NLS-1$
			if (!StringUtil.isEmpty(javaHome))
			{
				IPath path = Path.fromOSString(javaHome);
				if (path.lastSegment().startsWith("jre")) //$NON-NLS-1$
				{
					javaHome = path.removeLastSegments(1).toString();
				}
			}
			if (isValidJavaHome(javaHome))
			{
				this.javaHome = javaHome;
			}
		}
	}

	/**
	 * Check if the Java Home path contains /lib/tools.jar
	 * 
	 * @param directory
	 */
	private static boolean isValidJavaHome(String directory)
	{
		if (!StringUtil.isEmpty(directory))
		{
			// Check if the JAVA_HOME path contains /lib/tools.jar or /lib/dt.jar (for Mac)
			File file = new File(directory);
			if (file.isDirectory())
			{
				// Non-MacOSX
				File toolsJar = new File(file, "lib/tools.jar"); //$NON-NLS-1$
				// For MacOSX
				File dtJar = new File(file, "lib/dt.jar"); //$NON-NLS-1$
				return toolsJar.exists() || dtJar.exists();
			}
		}
		return false;
	}

	private void parseJavaVersion()
	{
		String specificationVersion = System.getProperty("java.specification.version"); //$NON-NLS-1$
		if (specificationVersion != null)
		{
			javaVersion = specificationVersion;
		}
		String fullVersion = System.getProperty("java.version"); //$NON-NLS-1$
		Pattern numberPattern = Pattern.compile("\\d+"); //$NON-NLS-1$
		if (fullVersion != null)
		{
			int updateSeparator = fullVersion.indexOf('_');
			if (updateSeparator > -1)
			{
				String updateString = fullVersion.substring(updateSeparator + 1);
				Matcher matcher = numberPattern.matcher(updateString);
				if (matcher.find())
				{
					try
					{
						javaUpdateVersion = Integer.parseInt(updateString.substring(matcher.start(), matcher.end()));
					}
					catch (Exception e)
					{
						IdeLog.logError(CorePlugin.getDefault(), MessageFormat.format(
								"Could not parse the JVM update version for ''{0}''", updateString), e); //$NON-NLS-1$
					}
				}
			}
		}
		String arch = System.getProperty("sun.arch.data.model"); //$NON-NLS-1$
		if (arch == null)
		{
			arch = Platform.getOSArch();
		}
		if (arch != null)
		{
			Matcher matcher = numberPattern.matcher(arch);
			if (matcher.find())
			{
				try
				{
					is32bit = Integer.parseInt(arch.substring(matcher.start(), matcher.end())) != 64;
				}
				catch (Exception e)
				{
					IdeLog.logError(CorePlugin.getDefault(),
							MessageFormat.format("Could not parse the OS architecture version for ''{0}''", arch), e); //$NON-NLS-1$
				}
			}
		}
		// Check if we have an installed JDK. We start by a simple check by executing a javac -version command.
		String javacFile = (Platform.OS_WIN32.equals(Platform.getOS())) ? JAVAC + ".exe" : JAVAC; //$NON-NLS-1$
		isJDKInstalled = hasJavac(javacFile);
		IPath javaPath = null;
		IPath javacPath = null;
		if (!isJDKInstalled)
		{
			// Replicate the Android's "find_java.bat" checks
			javaPath = new Path("Java"); //$NON-NLS-1$
			javacPath = new Path("bin").append(javacFile); //$NON-NLS-1$
			String env = System.getenv(PROGRAM_FILES_ENV);
			// if ProgramFiles not defined, do a 64bit check
			isJDKInstalled = (env == null) ? hasJavac64(javaPath, javacPath) : hasJavac6432(javaPath, javacPath);
			if (!isJDKInstalled)
			{
				isJDKInstalled = hasJavac64(javaPath, javacPath);
			}
			if (!isJDKInstalled)
			{
				// Still not found. Continue the check.
				// Check for the "default" 32-bit version if it's not the same path
				isJDKInstalled = hasJavac32(javaPath, javacPath);
			}
		}
	}

	private boolean hasJavac6432(IPath javaPath, IPath javacPath)
	{
		String env6432 = System.getenv(PROGRAM_W6432_ENV);
		if (env6432 != null)
		{
			return hasJavac(Path.fromOSString(env6432).append(javaPath).toFile().listFiles(), javacPath);
		}
		return false;
	}

	/*
	 * Check for the "default" 64-bit version if it's not the same path
	 */
	private boolean hasJavac64(IPath javaPath, IPath javacPath)
	{
		String env64 = System.getenv(PROGRAM_W6432_ENV);
		if (env64 == null || env64.equals(System.getenv(PROGRAM_FILES_ENV)))
		{
			return hasJavac32(javaPath, javacPath);
		}
		return hasJavac(Path.fromOSString(env64).append(javaPath).toFile().listFiles(), javacPath);
	}

	/*
	 * Check for the "default" 32-bit version if it's not the same path
	 */
	private boolean hasJavac32(IPath javaPath, IPath javacPath)
	{
		// Check for the "default" 32-bit version if it's not the same path
		String env32 = System.getenv(PROGRAM_FILES_X86_ENV);
		if (env32 == null)
		{
			// Fail. Note that the batch file also defines a check 'if "%ProgramFiles(x86)%"=="%ProgramFiles%" goto
			// :CheckFailed'.
			// This fail the check on a running 64bit system, since the System.getenv("ProgramFiles") returns the x86
			// dir, so we skip this test here.
			return false;
		}
		return hasJavac(Path.fromOSString(env32).append(javaPath).toFile().listFiles(), javacPath);
	}

	/*
	 * Locate a functional javac in one of the given directories.
	 */
	private boolean hasJavac(File[] folders, IPath javacPath)
	{
		if (folders != null)
		{
			for (File f : folders)
			{
				if (f.isDirectory())
				{
					if (hasJavac(Path.fromOSString(f.getAbsolutePath()).append(javacPath).toString()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * Run the javac commend on the given path
	 */
	private boolean hasJavac(String javacPath)
	{
		if (!javacPath.startsWith(JAVAC) && !new File(javacPath).exists())
		{
			return false;
		}
		ProcessBuilder pb = new ProcessBuilder(javacPath, VERSION_CMD);
		pb.redirectErrorStream(true);
		try
		{
			Process process = pb.start();
			IPath javac = null;
			String output = ProcessUtil.outputForProcess(process);
			if (output.startsWith(JAVAC))
			{
				// We found a valid javac, so we can also set up a detected JAVA_HOME in case we can't detect a valid
				// one from the system's environment.
				javac = Path.fromOSString(javacPath).removeLastSegments(1);
				if (Platform.OS_WIN32.equals(Platform.getOS()) && javac.segmentCount() == 0)
				{
					// Try to run the 'where' command on Windows to detect the full javac location.
					try
					{
						pb = new ProcessBuilder("where", JAVAC); //$NON-NLS-1$
						pb.redirectErrorStream(true);
						process = pb.start();
						output = ProcessUtil.outputForProcess(process);
						if (!StringUtil.isEmpty(output) && (new File(output).exists()))
						{
							javac = Path.fromOSString(output).removeLastSegments(1);
						}
					}
					catch (IOException e)
					{
						IdeLog.logWarning(CorePlugin.getDefault(),
								"Failed to detect the Java Home by calling 'where' on the 'javac'", e); //$NON-NLS-1$
					}
				}
				if ("bin".equals(javac.lastSegment())) //$NON-NLS-1$
				{
					javac = javac.removeLastSegments(1);
					if (isValidJavaHome(javac.toString()))
					{
						detectedJavaHome = javac.toString();
					}
				}

				return javac != null && javac.segmentCount() > 0;
			}
		}
		catch (IOException e)
		{
			IdeLog.logWarning(CorePlugin.getDefault(), "Failed detecting a JDK installation on the system", e); //$NON-NLS-1$
		}
		return false;
	}

	public IPath getJavacPath()
	{
		return javacPath;
	}
}

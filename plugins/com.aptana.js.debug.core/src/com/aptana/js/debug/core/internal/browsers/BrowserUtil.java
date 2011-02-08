/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.js.debug.core.internal.browsers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.IStatusHandler;
import org.osgi.framework.Bundle;

import com.aptana.core.util.FirefoxUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.js.debug.core.JSDebugPlugin;

/**
 * @author Max Stepanov
 * 
 */
public final class BrowserUtil {

	/**
	 * DEBUGGER_LAUNCH_URL
	 */
	public static final String DEBUGGER_LAUNCH_URL = "http://www.aptana.com/?debugger=true&port="; //$NON-NLS-1$

	private static final String[] EXTENSION_ID = {
		"debugger@aptana.com", //$NON-NLS-1$
		"firebug@software.joehewitt.com" //$NON-NLS-1$
	};

	private static final String[] EXTENSION_LOCAL_PATH = {
		"/res/firefox/aptanadebugger.xpi", //$NON-NLS-1$
		"/res/firefox/firebug.xpi", //$NON-NLS-1$
		"/res/ie/AptanaDebugger.dll" //$NON-NLS-1$
	};

	private static final String FIREBUG_MIN_VERSION = "1.1.0"; //$NON-NLS-1$
	private static final String IE_PLUGIN_ID = JSDebugPlugin.PLUGIN_ID + ".ie"; //$NON-NLS-1$
	private static final String EXTENSIONS = "extensions/"; //$NON-NLS-1$
	private static final String DEBUGGER_FILE = "chrome/aptanadebugger.jar"; //$NON-NLS-1$

	private static final long INSTALL_TIMEOUT = 5000;

	private static final Map<String, Boolean> browserCache = new HashMap<String, Boolean>(4);

	private static final IStatus installDebuggerPromptStatus = new Status(IStatus.INFO, JSDebugPlugin.PLUGIN_ID, 301, StringUtil.EMPTY, null);

	private BrowserUtil() {
	}

	/**
	 * getMacOSXApplicationIdentifier
	 * 
	 * @param application
	 * @return String
	 */
	public static String getMacOSXApplicationIdentifier(String application) {
		return PlatformUtil.getApplicationInfo(application, "CFBundleIdentifier"); //$NON-NLS-1$
	}

	/**
	 * isBrowserRunning
	 * 
	 * @param browserExecutable
	 * @return boolean
	 */
	public static boolean isBrowserRunning(String browserExecutable) {
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			browserExecutable = PlatformUtil.getApplicationExecutable(browserExecutable).getAbsolutePath();
		}
		PlatformUtil.ProcessItem[] processes = PlatformUtil.getRunningProcesses();
		if (processes != null) {
			String browserExecutable2 = browserExecutable;
			if (Platform.OS_LINUX.equals(Platform.getOS())) {
				// TODO: find a better solution
				browserExecutable2 += "-bin"; //$NON-NLS-1$
			}
			for (int i = 0; i < processes.length; ++i) {
				if (browserExecutable.equals(processes[i].getExecutableName())
						|| browserExecutable2.equals(processes[i].getExecutableName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * isBrowserDebugCompatible
	 * 
	 * @param browserExecutable
	 * @return boolean
	 */
	public static boolean isBrowserDebugCompatible(String browserExecutable) {
		return Firefox.isBrowserExecutable(browserExecutable)
				|| (InternetExplorer.isBrowserExecutable(browserExecutable) && isIEDebuggerAvailable());
	}

	private static boolean isIEDebuggerAvailable() {
		Bundle bundle = Platform.getBundle(IE_PLUGIN_ID);
		if (bundle != null) {
			return bundle.getEntry(EXTENSION_LOCAL_PATH[2]) != null;
		}
		return false;
	}

	/**
	 * resetBrowserCache
	 * 
	 * @param browserExecutable
	 */
	public static void resetBrowserCache(String browserExecutable) {
		browserCache.remove(browserExecutable);
		if (InternetExplorer.isBrowserExecutable(browserExecutable)) {
			IPath dllPath = JSDebugPlugin.getDefault().getStateLocation().append(".dll").addTrailingSeparator() //$NON-NLS-1$
					.append("AptanaDebugger.dll"); //$NON-NLS-1$
			if (dllPath.toFile().exists()) {
				dllPath.addFileExtension("registered").toFile().delete(); //$NON-NLS-1$
			}
		}
	}

	/**
	 * isBrowserDebugAvailable
	 * 
	 * @param browserExecutable
	 * @return boolean
	 */
	public static boolean isBrowserDebugAvailable(String browserExecutable) {
		Boolean value = (Boolean) browserCache.get(browserExecutable);
		if (value != null) {
			return value.booleanValue();
		} else if ("true".equals(Platform.getDebugOption("com.aptana.debug.core/skip_debugger_install"))) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}

		/**
		 * Firefox
		 */
		if (Firefox.isBrowserExecutable(browserExecutable)) {
			IPath profile = FirefoxUtil.findDefaultProfileLocation();
			if (profile != null) {
				boolean available = false;
				if (FirefoxUtil.getExtensionVersion(EXTENSION_ID[0], profile) != null) {
					String version = FirefoxUtil.getExtensionVersion(EXTENSION_ID[1], profile);
					// Check for compatible Firebug version
					if (version != null && VersionUtil.compareVersions(version, FIREBUG_MIN_VERSION) >= 0) { //$NON-NLS-1$
						IPath extension = profile.append(EXTENSIONS).append(EXTENSION_ID[1]);
						available = extension.toFile().exists() && !extension.append(DEBUGGER_FILE).toFile().exists();
					}

				}
				browserCache.put(browserExecutable, Boolean.valueOf(available));
				return available;
			}
			/**
			 * Internet Explorer
			 */
		} else if (InternetExplorer.isBrowserExecutable(browserExecutable) && isIEDebuggerAvailable()) {
			IPath dllPath = JSDebugPlugin.getDefault().getStateLocation().append(".dll").addTrailingSeparator() //$NON-NLS-1$
					.append("AptanaDebugger.dll"); //$NON-NLS-1$
			boolean available = dllPath.toFile().exists() && dllPath.addFileExtension("registered").toFile().exists(); //$NON-NLS-1$

			if (available) {
				/* refresh dll file */
				try {
					File file = dllPath.toFile();
					String currentDllPath = PlatformUtil.queryRegestryStringValue(
							"HKCR\\CLSID\\{B8ADD4EA-ADE3-4DEB-A957-9BBD17D6D0C8}\\InprocServer32", null); //$NON-NLS-1$
					boolean pathMatch = (currentDllPath != null && file.getAbsolutePath().compareTo(
							new File(currentDllPath).getAbsolutePath()) == 0);

					// Update dll
					if (file.exists()) {
						if (file.delete()) {
							extractFile(IE_PLUGIN_ID, EXTENSION_LOCAL_PATH[2], file);
						} else if (!pathMatch) {
							resetBrowserCache(browserExecutable);
							return false;
						}
					}

					if (!pathMatch) {
						if (BrowserUtil.isBrowserRunning(browserExecutable)) {
							return false;
						}
						if (System.getProperty("os.version").charAt(0) >= '6' //$NON-NLS-1$
								&& !PlatformUtil.isUserAdmin()) {
							PlatformUtil.runAsAdmin("regsvr32.exe", new String[] { //$NON-NLS-1$
									"/s", //$NON-NLS-1$
											dllPath.toOSString() });
							// Delay to let dll be registered
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
							}
						} else {
							execProcess(new String[] { "regsvr32.exe", //$NON-NLS-1$
									"/s", //$NON-NLS-1$
									dllPath.toOSString() }, -1);
						}
						currentDllPath = PlatformUtil.queryRegestryStringValue(
								"HKCR\\CLSID\\{B8ADD4EA-ADE3-4DEB-A957-9BBD17D6D0C8}\\InprocServer32", null); //$NON-NLS-1$
						if (currentDllPath == null
								|| file.getAbsolutePath().compareTo(new File(currentDllPath).getAbsolutePath()) != 0) {
							available = false;
						}

					}
				} catch (IOException e) {
					JSDebugPlugin.log(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e));
				}
			}

			browserCache.put(browserExecutable, Boolean.valueOf(available));
			return available;
		}
		return false;
	}

	/**
	 * execProcess
	 * 
	 * @param cmdline
	 * @param timeout
	 * @return int
	 * @throws IOException
	 */
	private static int execProcess(String[] cmdline, final long timeout) throws IOException {
		Process process = Runtime.getRuntime().exec(cmdline);
		final Thread thread = Thread.currentThread();
		Thread waitTimeout = new Thread() {
			public void run() {
				try {
					Thread.sleep(timeout);
					thread.interrupt();
				} catch (InterruptedException ignore) {
				}
			}
		};

		int exitcode = 0;
		if (timeout != -1) {
			try {
				waitTimeout.start();
				exitcode = process.waitFor();
				waitTimeout.interrupt();
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
			process.destroy();
		}
		try {
			exitcode = process.waitFor();
		} catch (InterruptedException e) {
		}
		return exitcode;
	}

	/**
	 * installDebugExtension
	 * 
	 * @param browserExecutable
	 * @param prompter
	 * @param monitor
	 * @return boolean
	 * @throws CoreException
	 */
	public static boolean installDebugExtension(String browserExecutable, IStatusHandler prompter,
			IProgressMonitor monitor) throws CoreException {
		boolean installDebugger = false;

		String browserName = StringUtil.EMPTY;
		if (Firefox.isBrowserExecutable(browserExecutable)) {
			browserName = Firefox.NAME;
		} else if (InternetExplorer.isBrowserExecutable(browserExecutable)) {
			browserName = InternetExplorer.NAME;
		}

		Object result = prompter.handleStatus(installDebuggerPromptStatus, browserName);
		if (result instanceof Boolean) {
			installDebugger = ((Boolean) result).booleanValue();
		}
		if (installDebugger) {
			monitor.subTask(Messages.BrowserUtil_InstallingDebugExtension);

			resetBrowserCache(browserExecutable);

			boolean installed = false;
			if (Firefox.isBrowserExecutable(browserExecutable)) {
				IPath profile = FirefoxUtil.findDefaultProfileLocation();
				if (profile != null) {
					try {
						IPath extension = profile.append(EXTENSIONS).append(EXTENSION_ID[1]);
						if (extension.toFile().exists() && extension.append(DEBUGGER_FILE).toFile().exists()) {
							prompter
									.handleStatus(
											installDebuggerPromptStatus,
											"warning_" + "Previous version of Firebug extension for Firefox has been found.\n Uninstall it and try again."); //$NON-NLS-1$ //$NON-NLS-2$
							return false;
						}
						String version = FirefoxUtil.getExtensionVersion(EXTENSION_ID[1], profile);
						if (version != null && VersionUtil.compareVersions(version, FIREBUG_MIN_VERSION) < 0) { //$NON-NLS-1$
							prompter
									.handleStatus(
											installDebuggerPromptStatus,
											"warning_" + "Previous version of Firebug extension for Firefox has been found.\n Uninstall it and try again."); //$NON-NLS-1$ //$NON-NLS-2$
							return false;
						}

						if (FirefoxUtil.getExtensionVersion(EXTENSION_ID[0], profile) == null) {
							installed = FirefoxUtil.installExtension(Platform.getBundle(JSDebugPlugin.PLUGIN_ID).getEntry(
									EXTENSION_LOCAL_PATH[0]), EXTENSION_ID[0], profile.append(EXTENSIONS).toFile());
						}
						if (FirefoxUtil.getExtensionVersion(EXTENSION_ID[1], profile) == null) {
							installed = FirefoxUtil.installExtension(Platform.getBundle(JSDebugPlugin.PLUGIN_ID).getEntry(
									EXTENSION_LOCAL_PATH[1]), EXTENSION_ID[1], profile.append(EXTENSIONS).toFile());
						}

						if (installed) {
							int max_retry = 3;
							while (BrowserUtil.isBrowserRunning(browserExecutable) && (max_retry-- > 0)) {
								prompter.handleStatus(installDebuggerPromptStatus, "quit_" + browserName); //$NON-NLS-1$
							}
						}
					} catch (Exception e) {
						JSDebugPlugin.log(e);
					}
				} else {
					JSDebugPlugin.log(Messages.BrowserUtil_FirefoxProfileNotFound);
				}

				if (installed && Platform.OS_MACOSX.equals(Platform.getOS())) {
					/* workaround for FF install bug on Mac */
					try {
						execProcess(new String[] {
								"/usr/bin/open", //$NON-NLS-1$
								"-b", //$NON-NLS-1$
								getMacOSXApplicationIdentifier(browserExecutable) }, -1); //$NON-NLS-1$
						try {
							Thread.sleep(INSTALL_TIMEOUT);
						} catch (InterruptedException e) {
							Thread.interrupted();
						}
					} catch (IOException e) {
						throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
								Messages.BrowserUtil_InstallError, e));
					}
				}
			} else if (InternetExplorer.isBrowserExecutable(browserExecutable)) {
				// XXX: temporary check if PDM is installed
				while (true) {
					if (PlatformUtil.queryRegestryStringValue(
							"HKCR\\CLSID\\{04CCE2FF-A7D3-11D0-B436-00A0244A1DD2}\\InprocServer32", null) == null) { //$NON-NLS-1$
						Boolean retry = (Boolean) prompter.handleStatus(installDebuggerPromptStatus, "nopdm"); //$NON-NLS-1$
						if (retry != null && retry.booleanValue()) {
							continue;
						}
						return false;
					}
					break;
				}

				int max_retry = 3;
				while (BrowserUtil.isBrowserRunning(browserExecutable) && (max_retry-- > 0)) {
					prompter.handleStatus(installDebuggerPromptStatus, "quit_" + browserName); //$NON-NLS-1$
				}

				IPath dllPath = JSDebugPlugin.getDefault().getStateLocation().append(".dll").addTrailingSeparator() //$NON-NLS-1$
						.append("AptanaDebugger.dll"); //$NON-NLS-1$
				try {
					File file = dllPath.toFile();
					if (file.exists()) {
						file.delete();
					} else {
						dllPath.removeLastSegments(1).toFile().mkdirs();
					}
					extractFile(IE_PLUGIN_ID, EXTENSION_LOCAL_PATH[2], file);

					if (System.getProperty("os.version").charAt(0) >= '6' //$NON-NLS-1$
							&& !PlatformUtil.isUserAdmin()) {
						PlatformUtil.runAsAdmin("regsvr32.exe", new String[] { //$NON-NLS-1$
								"/s", //$NON-NLS-1$
								dllPath.toOSString() });
						// Delay to let dll be registered
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
					} else {
						execProcess(new String[] {
								"regsvr32.exe", //$NON-NLS-1$
								"/s", //$NON-NLS-1$
								dllPath.toOSString() }, -1);
					}
					dllPath.addFileExtension("registered").toFile().createNewFile(); //$NON-NLS-1$
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID, IStatus.OK,
							Messages.BrowserUtil_InstallError, e));
				}
			}

			installed = BrowserUtil.isBrowserDebugAvailable(browserExecutable);
			if (installed) {
				prompter.handleStatus(installDebuggerPromptStatus, "installed_" + browserName); //$NON-NLS-1$
			} else {
				BrowserUtil.resetBrowserCache(browserExecutable);
				prompter.handleStatus(installDebuggerPromptStatus, "failed_" + browserName); //$NON-NLS-1$
			}
			return installed;
		}
		return false;
	}

	/**
	 * extractFile
	 * 
	 * @param path
	 * @param file
	 * @throws IOException
	 */
	private static void extractFile(String bundleId, String path, File file) throws IOException {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = Platform.getBundle(bundleId).getEntry(path).openStream();
			out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = in.read(buffer)) > 0) {
				out.write(buffer, 0, n);
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;

import com.aptana.terminal.IProcessConfiguration;

/**
 * @author Max Stepanov
 */
public class ProcessLauncher {

	private IProcessConfiguration configuration;
	private IPath initialDirectory;
	private Process process;
	private ListenerList processListeners = new ListenerList();

	/**
	 * 
	 */
	public ProcessLauncher(IProcessConfiguration configuration, IPath initialDirectory) {
		this.configuration = configuration;
		this.initialDirectory = initialDirectory;
	}

	public void launch() throws IOException, CoreException {
		ProcessBuilder builder = new ProcessBuilder(configuration.getCommandLine());
		builder.environment().putAll(configuration.getEnvironment());
		builder.directory((initialDirectory != null) ? initialDirectory.toFile() : null);
		process = builder.start();

		new Thread("Process watcher") { //$NON-NLS-1$
			@Override
			public void run() {
				try {
					process.waitFor();
				} catch (InterruptedException ignore) {
					ignore.getCause();
				} finally {
					notofyProcessCompleted();
				}
			}
		}.start();
	}

	public void destroy() {
		if (process != null) {
			try {
				process.exitValue();
			} catch (IllegalThreadStateException e) {
				process.destroy();
			}
		}
	}

	/**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	public void addProcessListener(IProcessListener listener) {
		processListeners.add(listener);
	}

	public void removeProcessListener(IProcessListener listener) {
		processListeners.remove(listener);
	}

	private void notofyProcessCompleted() {
		for (Object listener : processListeners.getListeners()) {
			((IProcessListener) listener).processCompleted();
		}
	}

}

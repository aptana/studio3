/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.terminal.internal;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.IProcessConfiguration;

/**
 * @author Max Stepanov
 *
 */
public class ProcessLauncher {

	// TODO: These shouldn't be in here. We're pulling the values from the explorer plugin
	// so as not to create a dependency on the two projects.
	private static final String ACTIVE_PROJECT_PROPERTY = "activeProject"; //$NON-NLS-1$
	private static final String EXPLORER_PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$
	
	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$

	private IProcessConfiguration configuration;
	private File initialDirectory;
	private Process process;
	private ListenerList processListeners = new ListenerList();
	
	/**
	 * 
	 */
	public ProcessLauncher(IProcessConfiguration configuration, File initialDirectory) {
		this.configuration = configuration;
		this.initialDirectory = initialDirectory;
	}
	
	public void launch() throws IOException {
		ProcessBuilder builder = new ProcessBuilder(configuration.getCommandLine());
		builder.environment().putAll(configuration.getEnvironment());
		builder.directory(getInitialDirectory());
		process = builder.start();
		
		new Thread("Process watcher") { //$NON-NLS-1$
			@Override
			public void run() {
				try {
					process.waitFor();
				} catch (InterruptedException e) {
				} finally {
					notofyProcessCompleted();
				}
			}
		}.start();
	}
	
	public void destroy() {
		try {
			process.exitValue();
		} catch (IllegalThreadStateException e) {
			process.destroy();
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

	private File getInitialDirectory() {
		if (initialDirectory != null && initialDirectory.isDirectory()) {
			return initialDirectory;
		}
		String activeProjeectName = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID, ACTIVE_PROJECT_PROPERTY, null, null);
		if (activeProjeectName != null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjeectName);
			if (project != null) {
				IPath location = project.getLocation();
				if (location != null) {
					return location.toFile();
				}
			}
		}
		String home = System.getProperty(USER_HOME_PROPERTY);
		if (home != null) {
			File file = new File(home);
			if (file.isDirectory()) {
				return file;
			}
		}
		return null;
	}
	
	private void notofyProcessCompleted() {
		for (Object listener : processListeners.getListeners()) {
			((IProcessListener) listener).processCompleted();
		}
	}

}

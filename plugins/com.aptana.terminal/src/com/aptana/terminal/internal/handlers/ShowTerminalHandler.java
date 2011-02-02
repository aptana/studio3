/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.terminal.Activator;
import com.aptana.terminal.preferences.IPreferenceConstants;
import com.aptana.terminal.views.TerminalView;

public class ShowTerminalHandler extends AbstractHandler {

	private static final String EXPLORER_PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$
	private static final String EXPLORER_ACTIVE_PROJECT = "activeProject"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPath workingDirectory = null;
		String title = Messages.ShowTerminalHandler_LBL_Terminal;
		String viewId = null;
		
		String workingDirectoryPref = Activator.getDefault().getPreferenceStore().getString(IPreferenceConstants.WORKING_DIRECTORY);
		if (workingDirectoryPref != null && workingDirectoryPref.length() > 0) {
			workingDirectory = Path.fromOSString(workingDirectoryPref);
			if (!workingDirectory.toFile().isDirectory()) {
				workingDirectory = null;
			}
		}

		if (workingDirectory == null) {
			String activeProjectName = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID, EXPLORER_ACTIVE_PROJECT, null, null);
			if (activeProjectName != null) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
				if (project != null) {
					workingDirectory = project.getLocation();
					title = project.getName();
					viewId = project.getName();
				}
			}
		}

		TerminalView.openView(viewId, title, workingDirectory);
		return null;
	}
}

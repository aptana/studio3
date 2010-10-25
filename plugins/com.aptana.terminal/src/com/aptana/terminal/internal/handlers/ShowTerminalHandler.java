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
		String title = "Terminal";
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

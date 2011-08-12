/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.core.ShellExecutable;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.preferences.IPreferenceConstants;

public class EarlyStartup implements IStartup {
	private static final String WEB_PERSPECTIVE_ID = "com.aptana.ui.WebPerspective"; //$NON-NLS-1$

	private IPerspectiveListener4 _perspectiveListener;
	private IWindowListener _windowListener;

	/**
	 * createPerspectiveListener
	 * 
	 * @return
	 */
	private IPerspectiveListener4 createPerspectiveListener() {
		return new PerspectiveAdapter() {
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
				if (WEB_PERSPECTIVE_ID.equals(perspective.getId()) && "resetComplete".equals(changeId)) //$NON-NLS-1$
				{
					EarlyStartup.this.openTerminalEditor(page.getWorkbenchWindow());
				}
			}

			public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if (WEB_PERSPECTIVE_ID.equals(perspective.getId())) {
					EarlyStartup.this.openTerminalEditor(page.getWorkbenchWindow());
				}
			}
		};
	}

	/**
	 * createWindowListener
	 * 
	 * @return
	 */
	private IWindowListener createWindowListener() {
		return new IWindowListener() {
			public void windowActivated(IWorkbenchWindow window) {
			}

			public void windowClosed(IWorkbenchWindow window) {
				window.removePerspectiveListener(_perspectiveListener);
			}

			public void windowDeactivated(IWorkbenchWindow window) {
			}

			public void windowOpened(IWorkbenchWindow window) {
				window.addPerspectiveListener(_perspectiveListener);

				// treat opening a new window like a first run
				EarlyStartup.this.openTerminalEditor(window);
			}
		};
	}

	/**
	 * createWorkbenchJob
	 * 
	 * @return
	 */
	private WorkbenchJob createWorkbenchJob() {
		return new WorkbenchJob("Terminal Perspective Listener") //$NON-NLS-1$
		{
			private void addPerspectiveListeners() {
				IWorkbench workbench = PlatformUI.getWorkbench();

				// add our perspective listener to each workbench window
				for (IWorkbenchWindow workbenchWindow : workbench.getWorkbenchWindows()) {
					workbenchWindow.addPerspectiveListener(_perspectiveListener);
				}
			}

			private void addWindowListener() {
				IWorkbench workbench = PlatformUI.getWorkbench();

				workbench.addWindowListener(_windowListener);
			}

			public IStatus runInUIThread(IProgressMonitor monitor) {
				// listen for window and perspective changes
				addWindowListener();
				addPerspectiveListeners();

				IPreferenceStore prefs = TerminalPlugin.getDefault().getPreferenceStore();
				boolean firstRun = prefs.getBoolean(IPreferenceConstants.FIRST_RUN);

				if (firstRun) {
					// possibly open a terminal editor
					IWorkbench workbench = PlatformUI.getWorkbench();

					// potentially open a terminal editor in each workbench window
					for (IWorkbenchWindow workbenchWindow : workbench.getWorkbenchWindows()) {
						IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
						String perspectiveId = workbenchPage.getPerspective().getId();

						// only open a terminal editor if the Rails perspective is the active perspective
						if (WEB_PERSPECTIVE_ID.equals(perspectiveId)) {
							EarlyStartup.this.openTerminalEditor(workbenchWindow);
						}
					}

					// set firstRun to false
					prefs.setValue(IPreferenceConstants.FIRST_RUN, false);
				}

				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * earlyStartup
	 */
	public void earlyStartup() {
		this._perspectiveListener = this.createPerspectiveListener();
		this._windowListener = this.createWindowListener();

		createWorkbenchJob().schedule();
	}

	/**
	 * openTerminalEditor
	 */
	private void openTerminalEditor(IWorkbenchWindow workbenchWindow) {
		try {
			if (ShellExecutable.getPath().toFile().exists()) {
				Utils.openTerminalEditor(workbenchWindow, TerminalEditor.ID, true);
			}
		} catch (CoreException e) {
			TerminalPlugin.log("Skip opening terminal editor", e.getCause()); //$NON-NLS-1$
		}
	}
}

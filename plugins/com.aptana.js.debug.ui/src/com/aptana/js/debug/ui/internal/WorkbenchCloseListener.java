/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.debug.core.DebugOptionsManager;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public final class WorkbenchCloseListener implements Listener {
	private WorkbenchCloseListener() {
	}

	/*
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	public void handleEvent(Event event) {
		if (event.widget instanceof Shell && PlatformUI.getWorkbench().getWorkbenchWindowCount() == 1
				&& PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell() == event.widget) {
			// last workbench window is about to close
			if (DebugOptionsManager.isDebuggerActive(JSDebugModel.getModelIdentifier())) { //$NON-NLS-1$
				IEclipsePreferences preferences = new InstanceScope().getNode(JSDebugUIPlugin.PLUGIN_ID);
				if (preferences.getBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, true) == false) {
					return;
				}
				event.doit = false;
				MessageDialogWithToggle dlg = MessageDialogWithToggle.openOkCancelConfirm((Shell) event.widget,
						Messages.WorkbenchCloseListener_ConfirmDebuggerExit,
						Messages.WorkbenchCloseListener_AptanaDebuggerIsActive_DoYouWantToExit,
						Messages.WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt, false, null, null);
				int returnValue = dlg.getReturnCode();
				if (returnValue != IDialogConstants.OK_ID) {
					// SWT hack - discard close event
					event.type = SWT.None;
					return;
				}
				if (dlg.getToggleState()) {
					preferences.putBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, false);
					try {
						preferences.flush();
					} catch (BackingStoreException e) {
						JSDebugUIPlugin.log(e);
					}
				}
			}
		}
	}

	public static void init() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				Display.getCurrent().addFilter(SWT.Close, new WorkbenchCloseListener());
			}
		});
	}
}

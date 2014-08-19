/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
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

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.debug.core.DebugOptionsManager;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public final class WorkbenchCloseListener implements Listener
{
	private WorkbenchCloseListener()
	{
	}

	/*
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets. Event)
	 */
	public void handleEvent(Event event)
	{
		if (event.widget instanceof Shell && PlatformUI.getWorkbench().getWorkbenchWindowCount() == 1
				&& PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell().equals(event.widget))
		{
			// last workbench window is about to close

			String modelIdentifier = JSDebugModel.getModelIdentifier();
			boolean debuggerActive = DebugOptionsManager.isDebuggerActive(modelIdentifier);
			boolean profilerActive = DebugOptionsManager.isProfilerActive(modelIdentifier);
			if (debuggerActive || profilerActive)
			{
				IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(JSDebugUIPlugin.PLUGIN_ID);
				if (!preferences.getBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, true))
				{
					return;
				}
				event.doit = false;
				MessageDialogWithToggle dlg;

				if (debuggerActive)
				{
					dlg = MessageDialogWithToggle.openOkCancelConfirm((Shell) event.widget,
							Messages.WorkbenchCloseListener_ConfirmDebuggerExit,
							Messages.WorkbenchCloseListener_DebuggerIsActive_DoYouWantToExit,
							Messages.WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt, false, null, null);
				}
				else
				{
					dlg = MessageDialogWithToggle.openOkCancelConfirm((Shell) event.widget,
							Messages.WorkbenchCloseListener_ConfirmProfilerExit,
							Messages.WorkbenchCloseListener_ProfilerIsActive_DoYouWantToExit,
							Messages.WorkbenchCloseListener_AlwaysExitProfilerWithoutPrompt, false, null, null);
				}
				int returnValue = dlg.getReturnCode();
				if (returnValue != IDialogConstants.OK_ID)
				{
					// SWT hack - discard close event
					event.type = SWT.None;
					return;
				}
				if (dlg.getToggleState())
				{
					preferences.putBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, false);
					try
					{
						preferences.flush();
					}
					catch (BackingStoreException e)
					{
						IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
					}
				}
			}
		}
	}

	public static void init()
	{
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				Display.getCurrent().addFilter(SWT.Close, new WorkbenchCloseListener());
			}
		});
	}
}

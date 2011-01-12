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
package com.aptana.debug.internal.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.JSDebugOptionsManager;

/**
 * @author Max Stepanov
 */
public final class WorkbenchCloseListener implements Listener
{
	private WorkbenchCloseListener()
	{
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event)
	{
		if (event.widget instanceof Shell && PlatformUI.getWorkbench().getWorkbenchWindowCount() == 1
				&& PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell() == event.widget)
		{
			// last workbench window is about to close
			if ("true".equals(System.getProperty(JSDebugOptionsManager.DEBUGGER_ACTIVE))) { //$NON-NLS-1$
				IPreferenceStore store = DebugUiPlugin.getDefault().getPreferenceStore();
				if (store.contains(IDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER))
				{
					if (store.getBoolean(IDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER) == false)
					{
						return;
					}
				}
				event.doit = false;
				MessageDialogWithToggle dlg = MessageDialogWithToggle.openOkCancelConfirm((Shell) event.widget,
						Messages.WorkbenchCloseListener_ConfirmDebuggerExit,
						Messages.WorkbenchCloseListener_AptanaDebuggerIsActive_DoYouWantToExit,
						Messages.WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt, false, null, null);
				int returnValue = dlg.getReturnCode();
				if (returnValue != IDialogConstants.OK_ID)
				{
					// SWT hack - discard close event
					event.type = SWT.None;
					return;
				}
				if (dlg.getToggleState())
				{
					store.setValue(IDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, false);
					DebugUiPlugin.getDefault().savePluginPreferences();
				}
			}
		}
	}

	/**
	 * init
	 */
	protected static void init()
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

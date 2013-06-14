/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public final class ShowFilteredPreferencePageHandler extends AbstractHandler
{

	public final Object execute(final ExecutionEvent event)
	{
		final String preferencePageId = event.getParameter(IWorkbenchCommandConstants.WINDOW_PREFERENCES_PARM_PAGEID);
		final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);

		final Shell shell;
		if (activeWorkbenchWindow == null)
		{
			shell = null;
		}
		else
		{
			shell = activeWorkbenchWindow.getShell();
		}

		final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(shell, preferencePageId,
				new String[] { preferencePageId }, null);
		dialog.open();

		return null;
	}

}
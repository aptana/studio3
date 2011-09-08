/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.theme.preferences.ThemePreferencePage;
import com.aptana.ui.util.UIUtils;

/**
 * Handler for command to open the Theme preference page in a dialog.
 * 
 * @author cwilliams
 */
public class OpenThemePreferencesHandler extends AbstractHandler implements IHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(UIUtils.getActiveShell(),
				ThemePreferencePage.ID, null, null);
		UIUtils.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				dialog.open();
			}
		});
		return null;
	}
}

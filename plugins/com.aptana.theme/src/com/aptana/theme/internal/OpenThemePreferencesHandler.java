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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.progress.UIJob;

import com.aptana.theme.preferences.ThemePreferencePage;
import com.aptana.ui.PopupSchedulingRule;
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
		UIJob job = new UIJob("Open Theme Preferences") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(UIUtils.getActiveShell(),
						ThemePreferencePage.ID, null, null);
				dialog.open();
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setRule(PopupSchedulingRule.INSTANCE);
		job.schedule();
		return null;
	}
}

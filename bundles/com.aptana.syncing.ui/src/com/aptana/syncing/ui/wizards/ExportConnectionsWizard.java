/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.ui.wizards;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class ExportConnectionsWizard extends Wizard implements IExportWizard
{

	private ExportConnectionsPage mainPage;

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
	}

	@Override
	public void addPages()
	{
		addPage(mainPage = new ExportConnectionsPage());
	}

	@Override
	public boolean performFinish()
	{
		IPath path = mainPage.getLocation();
		boolean isOverwriting = mainPage.isOverwritingExistingFile();

		// saves the preferences
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(SyncingUIPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.EXPORT_INITIAL_PATH, path.toOSString());
		prefs.putBoolean(IPreferenceConstants.EXPORT_OVEWRITE_FILE_WITHOUT_WARNING, isOverwriting);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(SyncingUIPlugin.getDefault(), Messages.ExportConnectionsWizard_ERR_FailSaveExportPrefs, e);
		}

		File file = path.toFile();
		if (file.exists())
		{
			if (!isOverwriting)
			{
				if (!MessageDialog
						.openConfirm(
								getShell(),
								Messages.ExportConnectionsWizard_Overwrite_Title,
								MessageFormat.format(Messages.ExportConnectionsWizard_Overwrite_Message,
										file.getAbsolutePath())))
				{
					return false;
				}
			}
			if (!file.canWrite())
			{
				MessageDialog.openError(getShell(), Messages.ExportConnectionsWizard_Error_Title,
						MessageFormat.format(Messages.ExportConnectionsWizard_Error_Message, file.getAbsolutePath()));
				return false;
			}
		}
		CoreIOPlugin.getConnectionPointManager().saveState(path);

		return true;
	}
}

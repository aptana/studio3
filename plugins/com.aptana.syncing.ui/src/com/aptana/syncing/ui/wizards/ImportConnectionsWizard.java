/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.ui.wizards;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.syncing.core.SyncingPlugin;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class ImportConnectionsWizard extends Wizard implements IImportWizard
{

	private static final String STATE_LOCATION_ROOT = ".metadata/.plugins/"; //$NON-NLS-1$

	private ImportConnectionsPage mainPage;

	public ImportConnectionsWizard()
	{
	}

	@Override
	public void addPages()
	{
		addPage(mainPage = new ImportConnectionsPage());
	}

	@Override
	public boolean performFinish()
	{
		IPath location = mainPage.getLocation();
		if (mainPage.isWorkspaceSelected())
		{
			// importing from workspace
			// imports connection points
			IPath connectionPath = location.append(STATE_LOCATION_ROOT).append("com.aptana.ide.core.io"); //$NON-NLS-1$
			File dir = connectionPath.toFile();
			if (dir.isDirectory())
			{
				// this is a 2.0 workspace
				loadConnectionPoints(dir);
			}
			else
			{
				// checks if it is a 3.0 workspace
				connectionPath = location.append(STATE_LOCATION_ROOT).append(CoreIOPlugin.PLUGIN_ID);
				dir = connectionPath.toFile();
				if (dir.isDirectory())
				{
					loadConnectionPoints(dir);
				}
			}

			// imports site connections
			IPath sitePath = location.append(STATE_LOCATION_ROOT).append("com.aptana.ide.syncing.core"); //$NON-NLS-1$
			dir = sitePath.toFile();
			if (dir.isDirectory())
			{
				// this is a 2.0 workspace
				loadSiteConnections(dir);
			}
			else
			{
				// checks if it is a 3.0 workspace
				sitePath = location.append(STATE_LOCATION_ROOT).append(SyncingPlugin.PLUGIN_ID);
				dir = sitePath.toFile();
				if (dir.isDirectory())
				{
					loadSiteConnections(dir);
				}
			}
		}
		else
		{
			// importing from file
			CoreIOPlugin.getConnectionPointManager().addConnectionsFrom(location);
			SyncingPlugin.getSiteConnectionManager().addConnectionsFrom(location);
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		setWindowTitle(Messages.ImportConnectionsWizard_Title);
	}

	private void loadConnectionPoints(File dir)
	{
		File[] files = dir.listFiles(new FilenameFilter()
		{

			public boolean accept(File dir, String name)
			{
				return name.startsWith("connections"); //$NON-NLS-1$
			}
		});
		File file = getLatestFile(files);
		if (file != null)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionsFrom(Path.fromOSString(file.getAbsolutePath()));
		}
	}

	private void loadSiteConnections(File dir)
	{
		File[] files = dir.listFiles(new FilenameFilter()
		{

			public boolean accept(File dir, String name)
			{
				return name.startsWith("sites"); //$NON-NLS-1$
			}
		});
		File file = getLatestFile(files);
		if (file != null)
		{
			SyncingPlugin.getSiteConnectionManager().addConnectionsFrom(Path.fromOSString(file.getAbsolutePath()));
		}
	}

	private static File getLatestFile(File[] files)
	{
		// picks the one with the largest number
		File latestFile = null;
		long maxNumber = 0;
		for (File file : files)
		{
			String filename = file.getName();
			int index = filename.lastIndexOf("."); //$NON-NLS-1$
			if (index > -1)
			{
				try
				{
					long number = Long.parseLong(filename.substring(index + 1));
					if (number > maxNumber)
					{
						latestFile = file;
						maxNumber = number;
					}
				}
				catch (NumberFormatException e)
				{
					// should not happen, but just in case
				}
			}
		}
		return latestFile;
	}
}

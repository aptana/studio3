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
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ui.util.UIUtils;

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
		int connectionCount = 0, siteCount = 0;
		if (mainPage.isWorkspaceSelected())
		{
			// importing from workspace
			// imports connection points
			IPath connectionPath = location.append(STATE_LOCATION_ROOT).append("com.aptana.ide.core.io"); //$NON-NLS-1$
			File dir = connectionPath.toFile();
			if (dir.isDirectory())
			{
				// this is a 2.0 workspace
				connectionCount = loadConnectionPoints(dir);
			}
			else
			{
				// checks if it is a 3.0 workspace
				connectionPath = location.append(STATE_LOCATION_ROOT).append(CoreIOPlugin.PLUGIN_ID);
				dir = connectionPath.toFile();
				if (dir.isDirectory())
				{
					connectionCount = loadConnectionPoints(dir);
				}
			}

			// imports site connections
			IPath sitePath = location.append(STATE_LOCATION_ROOT).append("com.aptana.ide.syncing.core"); //$NON-NLS-1$
			dir = sitePath.toFile();
			if (dir.isDirectory())
			{
				// this is a 2.0 workspace
				siteCount = loadSiteConnections(dir);
			}
			else
			{
				// checks if it is a 3.0 workspace
				sitePath = location.append(STATE_LOCATION_ROOT).append(SyncingPlugin.PLUGIN_ID);
				dir = sitePath.toFile();
				if (dir.isDirectory())
				{
					siteCount = loadSiteConnections(dir);
				}
			}
		}
		else
		{
			// importing from file
			List<IConnectionPoint> connections = CoreIOPlugin.getConnectionPointManager().addConnectionsFrom(location);
			connectionCount = connections.size();
			List<ISiteConnection> sites = SyncingPlugin.getSiteConnectionManager().addConnectionsFrom(location);
			siteCount = sites.size();
		}
		final int cCount = connectionCount;
		final int sCount = siteCount;
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				MessageDialog.openInformation(getShell(), Messages.ImportConnectionsWizard_Info_Title,
						MessageFormat.format(Messages.ImportConnectionsWizard_Info_Message, cCount, sCount));
			}
		});
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		setWindowTitle(Messages.ImportConnectionsWizard_Title);
	}

	private int loadConnectionPoints(File dir)
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
			List<IConnectionPoint> connections = CoreIOPlugin.getConnectionPointManager().addConnectionsFrom(
					Path.fromOSString(file.getAbsolutePath()));
			return connections.size();
		}
		return 0;
	}

	private int loadSiteConnections(File dir)
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
			List<ISiteConnection> sites = SyncingPlugin.getSiteConnectionManager().addConnectionsFrom(
					Path.fromOSString(file.getAbsolutePath()));
			return sites.size();
		}
		return 0;
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

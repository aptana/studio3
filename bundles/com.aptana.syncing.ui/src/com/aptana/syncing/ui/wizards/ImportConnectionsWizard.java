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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.io.ConnectionPoint;
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
		int[] applyToAllAction = new int[] { -1 };
		if (mainPage.isWorkspaceSelected())
		{
			// importing from workspace
			// imports connection points
			IPath connectionPath = location.append(STATE_LOCATION_ROOT).append("com.aptana.ide.core.io"); //$NON-NLS-1$
			File dir = connectionPath.toFile();
			if (dir.isDirectory())
			{
				// this is a 2.0 workspace
				connectionCount = loadConnectionPoints(dir, applyToAllAction);
				if (connectionCount == -1)
				{
					return false;
				}
			}
			else
			{
				// checks if it is a 3.0 workspace
				connectionPath = location.append(STATE_LOCATION_ROOT).append(CoreIOPlugin.PLUGIN_ID);
				dir = connectionPath.toFile();
				if (dir.isDirectory())
				{
					connectionCount = loadConnectionPoints(dir, applyToAllAction);
					if (connectionCount == -1)
					{
						return false;
					}
				}
			}

			// imports site connections
			IPath sitePath = location.append(STATE_LOCATION_ROOT).append("com.aptana.ide.syncing.core"); //$NON-NLS-1$
			dir = sitePath.toFile();
			if (dir.isDirectory())
			{
				// this is a 2.0 workspace
				siteCount = loadSiteConnections(dir, applyToAllAction);
			}
			else
			{
				// checks if it is a 3.0 workspace
				sitePath = location.append(STATE_LOCATION_ROOT).append(SyncingPlugin.PLUGIN_ID);
				dir = sitePath.toFile();
				if (dir.isDirectory())
				{
					siteCount = loadSiteConnections(dir, applyToAllAction);
				}
			}
		}
		else
		{
			// importing from file
			List<IConnectionPoint> connections = validateConnectionsFrom(location, applyToAllAction);
			if (connections == null)
			{
				return false;
			}

			connectionCount = connections.size();
			List<ISiteConnection> sites = validateSitesFrom(location, applyToAllAction);
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

	private int loadConnectionPoints(File dir, final int[] applyToAllAction)
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
			List<IConnectionPoint> acceptedConnections = validateConnectionsFrom(
					Path.fromOSString(file.getAbsolutePath()), applyToAllAction);

			if (acceptedConnections == null)
			{
				return -1;
			}

			return acceptedConnections.size();
		}
		return 0;
	}

	/**
	 * Validate logic: If the types are the same, prompt for override/skip. If the types are different, rename the
	 * connection
	 * 
	 * @param filePath
	 * @return
	 */
	private List<IConnectionPoint> validateConnectionsFrom(IPath filePath, final int[] applyToAllAction)
	{
		int action = 0;
		List<IConnectionPoint> acceptedConnections = new ArrayList<IConnectionPoint>();
		List<IConnectionPoint> removedConnections = new ArrayList<IConnectionPoint>();

		List<IConnectionPoint> newConnections = CoreIOPlugin.getConnectionPointManager().readConnectionsFrom(filePath);
		Map<String, IConnectionPoint> renames = new HashMap<String, IConnectionPoint>();
		Map<String, IConnectionPoint> currentNames = new HashMap<String, IConnectionPoint>();

		IConnectionPoint[] currentPoints = CoreIOPlugin.getConnectionPointManager().getConnectionPoints();
		for (IConnectionPoint current : currentPoints)
		{
			currentNames.put(current.getName(), current);
		}

		for (IConnectionPoint newConnection : newConnections)
		{
			boolean shouldAdd = true;
			if (currentNames.containsKey(newConnection.getName()))
			{
				IConnectionPoint current = currentNames.get(newConnection.getName());
				if (((ConnectionPoint) current).getType().equals(((ConnectionPoint) newConnection).getType()))
				{
					action = applyToAllAction[0];
					if (action == -1)
					{
						action = promptConflictDialog(newConnection.getName(), applyToAllAction);
					}

					switch (action)
					{
						case 0:
							shouldAdd = true;
							removedConnections.add(current);
							((ConnectionPoint) newConnection).setId(current.getId());
							break;
						case 1:
							shouldAdd = false;
							break;
						case 2:
							renames.put(newConnection.getName(), newConnection);
							break;
						default:
							return null;
					}
				}
				else
				{
					renames.put(newConnection.getName(), newConnection);
				}
			}

			if (shouldAdd)
			{
				acceptedConnections.add(newConnection);
				currentNames.put(newConnection.getName(), newConnection);
			}
		}

		for (IConnectionPoint removed : removedConnections)
		{
			CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(removed);
		}
		for (IConnectionPoint added : acceptedConnections)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(added);
		}

		for (String key : renames.keySet())
		{
			String name = key;
			int count = 1;
			ConnectionPoint point = (ConnectionPoint) renames.get(key);
			while (currentNames.containsKey(name))
			{
				name = MessageFormat.format(Messages.ImportConnectionsWizard_Conflict_Renamed, key, count++);
			}

			currentNames.put(name, point);
			point.setName(name);
		}

		return acceptedConnections;
	}

	private int loadSiteConnections(File dir, final int[] applyAllAction)
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
			List<ISiteConnection> newSites = validateSitesFrom(Path.fromOSString(file.getAbsolutePath()),
					applyAllAction);
			return newSites.size();
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
			int index = filename.lastIndexOf('.');
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

	private List<ISiteConnection> validateSitesFrom(IPath filePath, final int[] applyToAllAction)
	{
		int action = 0;
		List<ISiteConnection> acceptedConnections = new ArrayList<ISiteConnection>();
		List<ISiteConnection> removedConnections = new ArrayList<ISiteConnection>();

		List<ISiteConnection> newConnections = SyncingPlugin.getSiteConnectionManager().readConnectionsFrom(filePath);
		Map<String, ISiteConnection> renames = new HashMap<String, ISiteConnection>();
		Map<String, ISiteConnection> currentNames = new HashMap<String, ISiteConnection>();

		ISiteConnection[] currentPoints = SyncingPlugin.getSiteConnectionManager().getSiteConnections();
		for (ISiteConnection current : currentPoints)
		{
			currentNames.put(current.getName(), current);
		}

		for (ISiteConnection newConnection : newConnections)
		{
			boolean shouldAdd = true;
			if (currentNames.containsKey(newConnection.getName()))
			{
				ISiteConnection current = currentNames.get(newConnection.getName());
				action = applyToAllAction[0];
				if (action == -1)
				{
					action = promptConflictDialog(newConnection.getName(), applyToAllAction);
				}

				switch (action)
				{
					case 0:
						shouldAdd = true;
						removedConnections.add(current);
						break;
					case 1:
						shouldAdd = false;
						break;
					case 2:
						renames.put(newConnection.getName(), newConnection);
					default:
						break;
				}
			}

			if (shouldAdd)
			{
				acceptedConnections.add(newConnection);
				currentNames.put(newConnection.getName(), newConnection);
			}
		}

		for (ISiteConnection removed : removedConnections)
		{
			SyncingPlugin.getSiteConnectionManager().removeSiteConnection(removed);
		}
		for (ISiteConnection added : acceptedConnections)
		{
			SyncingPlugin.getSiteConnectionManager().addSiteConnection(added);
		}
		for (String key : renames.keySet())
		{
			String name = key;
			int count = 1;
			ISiteConnection point = (ISiteConnection) renames.get(key);
			while (currentNames.containsKey(name))
			{
				name = MessageFormat.format(Messages.ImportConnectionsWizard_Conflict_Renamed, key, count++);
			}

			currentNames.put(name, point);
			point.setName(name);
		}

		List<ISiteConnection> sites = SyncingPlugin.getSiteConnectionManager().readConnectionsFrom(filePath);

		return sites;
	}

	private int promptConflictDialog(String name, final int[] applyToAllAction)
	{
		MessageDialog dialog = new MessageDialog(getShell(), Messages.ImportConnectionsWizard_Conflict_Title, null,
				MessageFormat.format(Messages.ImportConnectionsWizard_Conflict_Message, name), 0, new String[] {
						com.aptana.ui.IDialogConstants.OVERWRITE_LABEL, IDialogConstants.SKIP_LABEL,
						com.aptana.ui.IDialogConstants.RENAME_LABEL }, 0)
		{

			@Override
			protected Control createCustomArea(Composite parent)
			{
				final Button applyToAll = new Button(parent, SWT.CHECK);
				GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
				applyToAll.setLayoutData(data);
				applyToAll.setText(Messages.ImportConnectionsWizard_Conflict_LBL_Apply);
				applyToAll.addSelectionListener(new SelectionListener()
				{

					public void widgetSelected(SelectionEvent e)
					{
						if (applyToAll.getSelection())
						{
							applyToAllAction[0] = 0;
						}
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});

				return applyToAll;
			}
		};

		int action = dialog.open();

		if (applyToAllAction[0] == -1)
		{
			applyToAllAction[0] = action;
		}

		return action;
	}
}

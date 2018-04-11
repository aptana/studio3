/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.ui.old.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.core.old.handlers.SyncEventHandlerAdapter;
import com.aptana.ide.syncing.ui.navigator.ProjectSiteConnection;
import com.aptana.ide.syncing.ui.old.views.SmartSyncDialog;
import com.aptana.ui.util.UIUtils;

/**
 * @author Ingo Muschenetz
 */
public class SiteConnectionSynchronizeAction implements IObjectActionDelegate
{
	private ISiteConnection fConnection;

	public SiteConnectionSynchronizeAction()
	{
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
	}

	public void run(IAction action)
	{
		final IConnectionPoint source = fConnection.getSource();
		IConnectionPoint dest = fConnection.getDestination();
		SmartSyncDialog dialog;
		try
		{
			dialog = new SmartSyncDialog(UIUtils.getActiveShell(), source, dest, source.getRoot(), dest.getRoot(),
					source.getName(), dest.getName());
			dialog.open();
			dialog.setHandler(new SyncEventHandlerAdapter()
			{

				public void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor)
				{
					IResource resource = (IResource) source.getAdapter(IResource.class);
					if (resource != null)
					{
						try
						{
							resource.refreshLocal(IResource.DEPTH_INFINITE, null);
						}
						catch (CoreException e)
						{
						}
					}
				}
			});
		}
		catch (CoreException e)
		{
			MessageBox error = new MessageBox(UIUtils.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			error.setMessage(Messages.SiteConnectionSynchronizeAction_UnableToOpenSyncDialog);
			error.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		fConnection = null;
		if (selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof ProjectSiteConnection)
			{
				fConnection = ((ProjectSiteConnection) element).getSiteConnection();
			}
		}
	}

}

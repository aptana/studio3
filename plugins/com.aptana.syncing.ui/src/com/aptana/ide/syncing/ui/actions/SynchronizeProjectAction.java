/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.old.ConnectionPointSyncPair;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.core.old.handlers.SyncEventHandlerAdapter;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.old.views.SmartSyncDialog;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;

public class SynchronizeProjectAction extends BaseSyncAction
{
	private static String MESSAGE_TITLE = StringUtil.ellipsify(Messages.SynchronizeAction_MessageTitle);

	protected void performAction(final IAdaptable[] files, final ISiteConnection site) throws CoreException
	{
		final IConnectionPoint source = site.getSource();
		final IConnectionPoint dest = site.getDestination();
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					IFileStore[] fileStores = SyncUtils.getFileStores(files);
					ConnectionPointSyncPair cpsp = new ConnectionPointSyncPair(source, dest);
					SmartSyncDialog dialog = new SmartSyncDialog(getShell(), cpsp, fileStores, null);
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
					error.setMessage(Messages.SynchronizeProjectAction_ERR_OpeningSyncDialog);
					error.open();
				}
				finally
				{
					StudioAnalytics.getInstance().sendEvent(
							new FeatureEvent("remote.sync." + site.getDestination().getType(), null)); //$NON-NLS-1$
				}
			}
		});
	}

	@Override
	protected String getMessageTitle()
	{
		return MESSAGE_TITLE;
	}
}

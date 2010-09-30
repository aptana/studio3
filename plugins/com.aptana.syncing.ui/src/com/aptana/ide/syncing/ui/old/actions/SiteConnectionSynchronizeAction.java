/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

package com.aptana.ide.syncing.ui.old.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import com.aptana.ui.UIUtils;

/**
 * @author Ingo Muschenetz
 */
public class SiteConnectionSynchronizeAction implements IObjectActionDelegate
{
	private ISiteConnection fConnection;

    public SiteConnectionSynchronizeAction() {
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    public void run(IAction action) {
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
				public void syncDone(VirtualFileSyncPair item)
				{
					Object file = source.getAdapter(IResource.class);
					if (file != null && file instanceof IResource)
					{
						try
						{
							((IResource) file).refreshLocal(IResource.DEPTH_INFINITE, null);
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

    public void selectionChanged(IAction action, ISelection selection) {
        fConnection = null;
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection).getFirstElement();
            if (element instanceof ProjectSiteConnection) {
            	fConnection = ((ProjectSiteConnection) element).getSiteConnection();
            }
        }
    }

}

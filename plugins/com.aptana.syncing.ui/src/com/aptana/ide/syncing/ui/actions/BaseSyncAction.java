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
package com.aptana.ide.syncing.ui.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.dialogs.ChooseSiteConnectionDialog;
import com.aptana.ide.syncing.ui.editors.EditorUtils;
import com.aptana.ide.syncing.ui.internal.SyncUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public abstract class BaseSyncAction implements IObjectActionDelegate, IViewActionDelegate {

    private IWorkbenchPart fActivePart;
    private List<IAdaptable> fSelectedElements;
    private ISiteConnection fSite;

    protected IFileStore fSourceRoot;
    protected IFileStore fDestinationRoot;
    protected boolean fSelectedFromSource;

    public BaseSyncAction() {
        fSelectedElements = new ArrayList<IAdaptable>();
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        fActivePart = targetPart;
    }

    public void run(IAction action) {
        if (fSelectedElements.size() == 0) {
            return;
        }

		if (fSite == null)
		{
			// gets the site connection user wants to use
			ISiteConnection[] sites = getSiteConnections();
			if (sites.length == 0)
			{
				// the selected elements do not belong to a common source location
				MessageDialog
						.openWarning(getShell(), getMessageTitle(), Messages.BaseSyncAction_Warning_NoCommonParent);
				return;
			}

			if (sites.length == 1)
			{
				fSite = sites[0];
			}
			else
			{
				// multiple connections on the selected source
				Object firstElement = fSelectedElements.get(0);
				if (firstElement instanceof IResource)
				{
					IContainer container = null;
					boolean remember = false;
					if (firstElement instanceof IContainer)
					{
						remember = ResourceSynchronizationUtils.isRememberDecision((IContainer) firstElement);
						if (remember)
						{
							container = (IContainer) firstElement;
						}
					}
					if (!remember)
					{
						IProject project = ((IResource) firstElement).getProject();
						remember = ResourceSynchronizationUtils.isRememberDecision(project);
						if (remember)
						{
							container = project;
						}
					}

					fSite = getLastSyncConnection(container);
				}

				if (fSite == null)
				{
					ChooseSiteConnectionDialog dialog = new ChooseSiteConnectionDialog(getShell(), sites);
					dialog.setShowRememberMyDecision(true);
					dialog.open();

					fSite = dialog.getSelectedSite();
					if (fSite != null)
					{
						setRememberMyDecision(fSite, dialog.isRememberMyDecision());
					}
				}
			}
		}

		if (fSite != null)
		{
			try
			{
				performAction(fSelectedElements.toArray(new IAdaptable[fSelectedElements.size()]), fSite);
			}
			catch (CoreException e)
			{
				// TODO: Opens an error dialog
			}
		}
    }

    public void selectionChanged(IAction action, ISelection selection) {
        action.setEnabled(false);
        setSelection(selection);
        setSelectedSite(null);
        action.setEnabled(fSelectedElements.size() > 0);
    }

	public void setSelectedSite(ISiteConnection site)
	{
		fSite = site;
	}

	/**
	 * Specifies a particular file store as the root for the source side.
	 * 
	 * @param sourceRoot
	 *            a file store
	 */
	public void setSourceRoot(IFileStore sourceRoot)
	{
		fSourceRoot = sourceRoot;
	}

	/**
	 * Specifies a particular file store as the root for the destination side.
	 * 
	 * @param destinationRoot
	 *            a file store
	 */
	public void setDestinationRoot(IFileStore destinationRoot)
	{
		fDestinationRoot = destinationRoot;
	}

	public void init(IViewPart view) {
		fActivePart = view;
	}

	public void setSelection(ISelection selection)
	{
		fSelectedElements.clear();

		Object[] elements = ((IStructuredSelection) selection).toArray();
		IAdaptable adaptable;
		boolean fromSource = true;
		for (Object element : elements)
		{
			if (element instanceof IAdaptable)
			{
				adaptable = (IAdaptable) element;
				if (SiteConnectionUtils.findSitesForSource(adaptable).length > 0)
				{
					fSelectedElements.add(adaptable);
				}
				else if (SiteConnectionUtils.findSitesWithDestination(adaptable).length > 0)
				{
					if (fromSource)
					{
						fromSource = false;
					}
					fSelectedElements.add(adaptable);
				}
			}
		}
		fSelectedFromSource = fromSource;
	}

    public void setSelection(ISelection selection, boolean fromSource) {
    	fSelectedFromSource = fromSource;
        fSelectedElements.clear();

        if (!(selection instanceof IStructuredSelection) || selection.isEmpty()) {
            return;
        }

        Object[] elements = ((IStructuredSelection) selection).toArray();
        ISiteConnection[] sites;
        for (Object element : elements) {
            if (element instanceof IAdaptable) {
            	if (fSelectedFromSource) {
            		sites = SiteConnectionUtils.findSitesForSource((IAdaptable) element);
            	} else {
            		sites = SiteConnectionUtils.findSitesWithDestination((IAdaptable) element);
            	}
                if (sites.length > 0) {
                    fSelectedElements.add((IAdaptable) element);
                }
            }
        }
    }

    protected abstract void performAction(IAdaptable[] files, ISiteConnection site)
            throws CoreException;

    protected String getMessageTitle() {
        return StringUtil.ellipsify(Messages.BaseSyncAction_MessageTitle);
    }

    protected Shell getShell() {
        return fActivePart.getSite().getShell();
    }

    /**
     * @return an array of all sites that contains the selected elements in
     *         their source locations
     */
    @SuppressWarnings("unchecked")
	protected ISiteConnection[] getSiteConnections() {
        List<Set<ISiteConnection>> sitesList = new ArrayList<Set<ISiteConnection>>();
        Set<ISiteConnection> sitesSet = new HashSet<ISiteConnection>();
        ISiteConnection[] sites;
        for (IAdaptable element : fSelectedElements) {
        	if (fSelectedFromSource) {
        		sites = SiteConnectionUtils.findSitesForSource(element);
        	} else {
        		sites = SiteConnectionUtils.findSitesWithDestination(element);
        	}
            sitesSet.clear();
            for (ISiteConnection site : sites) {
                sitesSet.add(site);
            }
            sitesList.add(sitesSet);
        }
        Set<ISiteConnection> sitesSets = SyncUtils.getIntersection(
        		sitesList.toArray(new Set[sitesList.size()]));

        return sitesSets.toArray(new ISiteConnection[sitesSets.size()]);
    }

    /**
     * Opens the connection editor.
     */
    protected void openConnectionEditor() {
        ISiteConnection[] sites = getSiteConnections();
        if (sites.length > 0) {
            EditorUtils.openConnectionEditor(sites[0]);
        }
    }

    private ISiteConnection getLastSyncConnection(IContainer container) {
        if (container == null) {
            return null;
        }

        String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(container);
        if (lastConnection == null) {
            return null;
        }

        ISiteConnection[] sites;
        if (fSelectedFromSource) {
        	sites = SiteConnectionUtils.findSitesForSource(container, true);
        } else {
        	sites = SiteConnectionUtils.findSitesWithDestination(container, true);
        }
        IConnectionPoint destination;
        String target;
        for (ISiteConnection site : sites) {
        	destination = site.getDestination();
        	if (destination == null) {
        		continue;
        	}
            target = destination.getName();
            if (target.equals(lastConnection)) {
                return site;
            }
        }
        return null;
    }

    private void setRememberMyDecision(ISiteConnection site, boolean rememberMyDecision) {
        IConnectionPoint source = site.getSource();
        IContainer container = (IContainer) source.getAdapter(IContainer.class);
        if (container == null) {
        	return;
        }
        if (rememberMyDecision) {
            ResourceSynchronizationUtils.setRememberDecision(container, rememberMyDecision);
        }

        // remembers the last sync connection
        ResourceSynchronizationUtils.setLastSyncConnection(container, site.getDestination()
                .getName());
    }
}

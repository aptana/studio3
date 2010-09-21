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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.syncing.ui.old.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.ide.syncing.core.old.ISyncResource;
import com.aptana.ide.syncing.core.old.SyncFolder;
import com.aptana.ide.syncing.core.old.SyncState;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
public class SmartSyncContentProvider implements ITreeContentProvider
{

	private List<ISyncResource> fCurrentResources;

	private int fSyncDirection;
	private int fPresentationType;
	private boolean fDeleteRemoteFiles;
	private boolean fDeleteLocalFiles;

	/**
	 * Constructor.
	 */
	public SmartSyncContentProvider()
	{
		fCurrentResources = new ArrayList<ISyncResource>();
	}

	/**
	 * Gets the current list of displayed resources.
	 * 
	 * @return an array of current resources displayed
	 */
	public ISyncResource[] getCurrentResources()
	{
		return fCurrentResources.toArray(new ISyncResource[fCurrentResources.size()]);
	}

	/**
	 * Sets the sync direction.
	 * 
	 * @param direction
	 *            the direction for doing the sync
	 */
	public void setSyncDirection(int direction)
	{
		fSyncDirection = direction;
	}

	/**
	 * Sets the presentation type.
	 * 
	 * @param type
	 *            the type of presentation for the viewer
	 */
	public void setPresentationType(int type)
	{
		fPresentationType = type;
	}

	/**
	 * Sets the indication of if deleting remote files is selected.
	 * 
	 * @param delete
	 *            true if deleting remote files is selected, false otherwise
	 */
	public void setDeleteRemoteFiles(boolean delete)
	{
		fDeleteRemoteFiles = delete;
	}

	/**
	 * Sets the indication of if deleting local files is selected.
	 * 
	 * @param delete
	 *            true if deleting local files is selected, false otherwise
	 */
	public void setDeleteLocalFiles(boolean delete)
	{
		fDeleteLocalFiles = delete;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof SyncFolder && fPresentationType == OptionsToolBar.TREE_VIEW)
		{
			// finds the direct children that are in the list of files to be synced
			ISyncResource[] children = ((SyncFolder) parentElement).members();
			List<ISyncResource> resources = new ArrayList<ISyncResource>();
			for (ISyncResource child : children)
			{
				if (fCurrentResources.contains(child))
				{
					resources.add(child);
				}
				else if (child instanceof SyncFolder && !resources.contains(child))
				{
					// if a folder contains a file in the list, it needs to be
					// included
					SyncFolder folder = (SyncFolder) child;
					List<ISyncResource> subs = folder.getAllFiles();
					for (ISyncResource resource : fCurrentResources)
					{
						if (subs.contains(resource))
						{
							resources.add(child);
							break;
						}
					}
				}
			}
			return resources.toArray(new ISyncResource[resources.size()]);
		}
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof ISyncResource)
		{
			return ((ISyncResource) element).getParent();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof SyncFolder && fPresentationType == OptionsToolBar.TREE_VIEW)
		{
			return !((SyncFolder) element).isEmpty();
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		fCurrentResources.clear();

		if (inputElement instanceof SyncFolder)
		{
			ISyncResource[] all = ((SyncFolder) inputElement).getAllFiles().toArray(new ISyncResource[0]);
			if (fSyncDirection == DirectionToolBar.UPLOAD || fSyncDirection == DirectionToolBar.DOWNLOAD
					|| fSyncDirection == DirectionToolBar.FORCE_UPLOAD
					|| fSyncDirection == DirectionToolBar.FORCE_DOWNLOAD)
			{
				int state;
				for (ISyncResource resource : all)
				{
					state = resource.getSyncState();
					if (fSyncDirection == DirectionToolBar.UPLOAD || fSyncDirection == DirectionToolBar.FORCE_UPLOAD)
					{
						if (state == SyncState.ClientItemOnly || state == SyncState.ClientItemIsNewer)
						{
							fCurrentResources.add(resource);
						}
						else if (fDeleteRemoteFiles && state == SyncState.ServerItemOnly)
						{
							fCurrentResources.add(resource);
						}
						else if (state == SyncFolder.MIXED)
						{
							fCurrentResources.add(resource);
						}
					}
					else if (fSyncDirection == DirectionToolBar.DOWNLOAD
							|| fSyncDirection == DirectionToolBar.FORCE_DOWNLOAD)
					{
						if (state == SyncState.ServerItemIsNewer || state == SyncState.ServerItemOnly)
						{
							fCurrentResources.add(resource);
						}
						else if (fDeleteLocalFiles && state == SyncState.ClientItemOnly)
						{
							fCurrentResources.add(resource);
						}
						else if (state == SyncFolder.MIXED)
						{
							fCurrentResources.add(resource);
						}
					}
				}
				if (fPresentationType == OptionsToolBar.TREE_VIEW)
				{
					return getChildren(inputElement);
				}
			}
			else
			{
				for (ISyncResource resource : all)
				{
					fCurrentResources.add(resource);
				}
				if (fPresentationType == OptionsToolBar.TREE_VIEW)
				{
					return getChildren(inputElement);
				}
			}
		}
		return fCurrentResources.toArray(new ISyncResource[fCurrentResources.size()]);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

}

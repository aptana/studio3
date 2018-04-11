/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.core.util.ArrayUtil;
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
		return ArrayUtil.NO_OBJECTS;
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

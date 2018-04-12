/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IPath;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SyncFolder implements ISyncResource
{

	/**
	 * MIXED
	 */
	public static final int MIXED = -2;

	private Set<ISyncResource> children;
	private SyncFolder parent;
	private VirtualFileSyncPair pair;
	private IPath path;
	private boolean skipped = false;
	private int syncState = -1;
	private int transferState = -1;
	private long transferredBytes;

	/**
	 * Creates a new sync folder object
	 * 
	 * @param path
	 * @param parent
	 * @param pair
	 */
	public SyncFolder(IPath path, SyncFolder parent, VirtualFileSyncPair pair)
	{
		children = new TreeSet<ISyncResource>(new Comparator<ISyncResource>()
		{

			public int compare(ISyncResource o1, ISyncResource o2)
			{
				return o1.getPath().lastSegment().compareTo(o2.getPath().lastSegment());
			}
		});
		this.parent = parent;
		this.path = path;
		this.pair = pair;
	}

	/**
	 * Finds a resource for the given pair
	 * 
	 * @param pair
	 * @return - resource found or null
	 */
	public ISyncResource find(VirtualFileSyncPair pair)
	{
		if (this.pair == pair)
		{
			return this;
		}
		for (ISyncResource resource : children)
		{
			if (resource instanceof SyncFolder)
			{
				ISyncResource pairResource = ((SyncFolder) resource).find(pair);
				if (pairResource != null)
				{
					return pairResource;
				}
			}
			else if (resource instanceof SyncFile)
			{
				if (pair == ((SyncFile) resource).getPair())
				{
					return resource;
				}
			}
		}
		return null;
	}

	/**
	 * Counts the number of files in this folder and its subfolders
	 * 
	 * @return - number of files
	 */
	public int updateCount()
	{
		int number = 0;
		for (ISyncResource resource : getNonEmptyMembers())
		{
			if (resource instanceof SyncFile)
			{
				number++;
			}
			else if (resource instanceof SyncFolder)
			{
				number += ((SyncFolder) resource).updateCount();
			}
		}
		return number;
	}

	/**
	 * Gets all the sync pairs inside this folder and all sub folders
	 * 
	 * @return - list of all child pairs
	 */
	public List<VirtualFileSyncPair> getPairs()
	{
		List<VirtualFileSyncPair> pairs = new ArrayList<VirtualFileSyncPair>();
		ISyncResource[] members = members();
		for (ISyncResource member : members)
		{
			if (!member.isSkipped())
			{
				if (member instanceof SyncFile)
				{
					pairs.add(((SyncFile) member).getPair());
				}
				else if (member instanceof SyncFolder)
				{
					SyncFolder folder = (SyncFolder) member;
					if (folder.getPair() != null)
					{
						pairs.add(folder.getPair());
					}
					pairs.addAll(folder.getPairs());
				}
			}
		}
		return pairs;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getName()
	 */
	public String getName()
	{
		return path.lastSegment();
	}

	/**
	 * Adds a member to this folder
	 * 
	 * @param child
	 */
	public void addMember(ISyncResource child)
	{
		children.add(child);
		if (child instanceof SyncFile)
		{
			int newState = ((SyncFile) child).getPair().getSyncState();
			if (syncState == -1 || syncState == newState)
			{
				syncState = newState;
			}
			else
			{
				syncState = MIXED;
			}
		}
	}

	/**
	 * Gets a list of the non empty folders for a compressed view
	 * 
	 * @return - list of sync resources
	 */
	public List<ISyncResource> getCompressedMembers()
	{
		List<ISyncResource> compressed = new ArrayList<ISyncResource>();
		ISyncResource[] members = members();
		for (ISyncResource member : members)
		{
			if (member instanceof SyncFolder)
			{
				SyncFolder folder = (SyncFolder) member;
				if (folder.containsFiles())
				{
					compressed.add(folder);
				}
				else
				{
					compressed.addAll(folder.getCompressedMembers());
				}
			}
			else
			{
				compressed.add(member);
			}
		}
		return compressed;
	}

	/**
	 * Removes a member from this folder
	 * 
	 * @param child
	 */
	public void removeMember(ISyncResource child)
	{
		children.remove(child);
	}

	/**
	 * Clears all members from this folder
	 */
	public void clearMembers()
	{
		children.clear();
	}

	/**
	 * True if this folder contains files
	 * 
	 * @return - true if has direct folder children
	 */
	public boolean containsFiles()
	{
		for (ISyncResource resource : children)
		{
			if (resource instanceof SyncFile)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets a member directly under this folder with the given name
	 * 
	 * @param name
	 * @return - resource if found
	 */
	public ISyncResource getMember(String name)
	{
		for (ISyncResource resource : children)
		{
			if (name.equals(resource.getName()))
			{
				return resource;
			}
		}
		return null;
	}

	/**
	 * True if this folder is empty, meaning it doesn't contain any files and all folders are empty as well
	 * 
	 * @return - true if empty
	 */
	public boolean isEmpty()
	{
		return children.isEmpty();
	}

	/**
	 * Gets all the files located in this folder or subfolders
	 * 
	 * @return - list of sync files
	 */
	public List<ISyncResource> getAllFiles()
	{
		List<ISyncResource> files = new ArrayList<ISyncResource>();
		for (ISyncResource resource : children)
		{
			if (resource instanceof SyncFile)
			{
				files.add(resource);
			}
			else if (resource instanceof SyncFolder)
			{
				if (!((SyncFolder) resource).isEmpty())
				{
					if (resource.getPair() != null
							&& (resource.getSyncState() == SyncState.ClientItemOnly || resource.getSyncState() == SyncState.ServerItemOnly))
					{
						files.add(resource);
					}
					files.addAll(((SyncFolder) resource).getAllFiles());
				}
				else
				{
					files.add(resource);
				}
			}
		}
		return files;
	}

	/**
	 * Gets all the children folder and files with nothing omitted
	 * 
	 * @return - collection of entire folders contents
	 */
	public Collection<ISyncResource> getAllChildren()
	{
		Set<ISyncResource> members = new HashSet<ISyncResource>();
		for (ISyncResource resource : children)
		{
			members.add(resource);
			if (resource instanceof SyncFolder)
			{
				members.addAll(((SyncFolder) resource).getAllChildren());
			}
		}
		return members;
	}

	/**
	 * Gets the non empty members directly under this resources consisting of files and non empty folders
	 * 
	 * @return - resources
	 */
	public ISyncResource[] getNonEmptyMembers()
	{
		Set<ISyncResource> members = new HashSet<ISyncResource>();
		for (ISyncResource resource : children)
		{
			if (resource instanceof SyncFile)
			{
				members.add(resource);
			}
			else if (resource instanceof SyncFolder)
			{
				if (!((SyncFolder) resource).isEmpty())
				{
					members.add(resource);
				}
			}
		}
		return members.toArray(new ISyncResource[members.size()]);
	}

	/**
	 * Gets all members directly under this resource
	 * 
	 * @return - resources
	 */
	public ISyncResource[] members()
	{
		return children.toArray(new ISyncResource[children.size()]);
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getParent()
	 */
	public SyncFolder getParent()
	{
		return parent;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getPath()
	 */
	public IPath getPath()
	{
		return path;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#isSkipped()
	 */
	public boolean isSkipped()
	{
		return skipped;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#setSkipped(boolean)
	 */
	public void setSkipped(boolean skipped)
	{
		this.skipped = skipped;
		for (ISyncResource resource : members())
		{
			resource.setSkipped(this.skipped);
		}
	}

	/**
	 * Sets this folder as skipped but with the option of not propogating to all children
	 * 
	 * @param skipped
	 * @param propogate
	 */
	public void setSkipped(boolean skipped, boolean propogate)
	{
		this.skipped = skipped;
		if (propogate)
		{
			setSkipped(this.skipped);
		}
	}

	/**
	 * Gets the sync state
	 * 
	 * @return - state from SyncState or -2 to denote mixed states
	 */
	public int getSyncState()
	{
		if (this.pair != null && syncState == -1)
		{
			return this.pair.getSyncState();
		}
		return syncState;
	}

	public void setSyncState(int state)
	{
		this.syncState = state;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getPair()
	 */
	public VirtualFileSyncPair getPair()
	{
		return pair;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getTransferState()
	 */
	public int getTransferState()
	{
		return transferState;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#setTransferState(int)
	 */
	public void setTransferState(int state)
	{
		this.transferState = state;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#getTransferredBytes()
	 */
	public long getTransferredBytes()
	{
		return transferredBytes;
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.ISyncResource#setTransferredBytes(long)
	 */
	public void setTransferredBytes(long bytes)
	{
		this.transferredBytes = bytes;
	}

}

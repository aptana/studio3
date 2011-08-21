/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class SyncModelBuilder
{

	private SyncModelBuilder()
	{
		// Does nothing
	}

	/**
	 * Builds a root sync model from an array of virtual file sync pairs
	 * 
	 * @param pairs
	 * @return - the root of the syncing model
	 */
	@SuppressWarnings("deprecation")
	public static SyncFolder buildSyncFolder(IConnectionPoint sourceConnectionPoint,
			IConnectionPoint destConnectionPoint, VirtualFileSyncPair[] pairs)
	{
		SyncFolder root = new SyncFolder(new Path("/"), null, null); //$NON-NLS-1$
		List<IFileStore> ignoredFiles = new ArrayList<IFileStore>();
		for (VirtualFileSyncPair pair : pairs)
		{
			IFileStore virtualFile = null;
			switch (pair.getSyncState())
			{
				case SyncState.ClientItemOnly:
				case SyncState.ClientItemIsNewer:
					virtualFile = pair.getSourceFile();
					break;
				case SyncState.ServerItemOnly:
				case SyncState.ServerItemIsNewer:
					virtualFile = pair.getDestinationFile();
					break;
				case SyncState.ItemsMatch:
				case SyncState.Ignore:
					ignoredFiles.add(pair.getSourceFile());
					break;
				default:
					if (pair.getDestinationFile() == null)
					{
						virtualFile = pair.getSourceFile();
					}
					else
					{
						virtualFile = pair.getDestinationFile();
					}
					break;
			}

			if (virtualFile != null)
			{
				IPath realPath = new Path(pair.getRelativePath()); // EFSUtils.getRelativePath(pair.virtualFile));
				SyncFolder parent = root;
				for (int i = 0; i < realPath.segmentCount(); i++)
				{
					String segment = realPath.segment(i);
					if (i < realPath.segmentCount() - 1)
					{
						ISyncResource currFolder = parent.getMember(segment);
						if (currFolder != null)
						{
							if (currFolder instanceof SyncFolder)
							{
								parent = (SyncFolder) currFolder;
							}
							else if (currFolder instanceof SyncFile)
							{
								SyncFile file = (SyncFile) currFolder;
								SyncFolder convertedFolder = new SyncFolder(file.getPath(), parent, null);
								parent.removeMember(file);
								parent.addMember(convertedFolder);
								parent = convertedFolder;
							}
						}
						else
						{
							currFolder = new SyncFolder(parent.getPath().append(segment), parent, null);
							parent.addMember(currFolder);
							parent = (SyncFolder) currFolder;
						}
					}
					else
					{
						if (!virtualFile.fetchInfo().isDirectory())
						{
							SyncFile file = new SyncFile(parent.getPath().append(segment), pair, parent);
							parent.addMember(file);
						}
						else if (virtualFile.fetchInfo().isDirectory())
						{
							SyncFolder folder = new SyncFolder(parent.getPath().append(segment), parent, pair);
							parent.addMember(folder);
						}

					}
				}
			}
		}

		IPath realPath;
		SyncFolder parent;
		String segment;
		boolean found;
		for (IFileStore file : ignoredFiles)
		{
			realPath = new Path(EFSUtils.getRelativePath(sourceConnectionPoint, file, null));
			parent = root;
			found = true;
			for (int i = 0; i < realPath.segmentCount(); i++)
			{
				segment = realPath.segment(i);
				if (i < realPath.segmentCount() - 1)
				{
					ISyncResource currFolder = parent.getMember(segment);
					if (currFolder == null || !(currFolder instanceof SyncFolder))
					{
						found = false;
						break;
					}
					parent = (SyncFolder) currFolder;
				}
			}
			if (found && parent.getParent() != null)
			{
				int state = parent.getSyncState();
				switch (state)
				{
					case SyncState.ClientItemOnly:
					case SyncState.ClientItemIsNewer:
						parent.setSyncState(SyncState.ClientItemIsNewer);
						break;
					case SyncState.ServerItemOnly:
					case SyncState.ServerItemIsNewer:
						parent.setSyncState(SyncState.ServerItemIsNewer);
						break;
				}
			}
		}
		return root;
	}
}

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
package com.aptana.ide.syncing.core.old;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;

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
	public static SyncFolder buildSyncFolder(IConnectionPoint sourceConnectionPoint, IConnectionPoint destConnectionPoint, VirtualFileSyncPair[] pairs)
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
				IPath realPath = new Path(pair.getRelativePath()); //EFSUtils.getRelativePath(pair.virtualFile));
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

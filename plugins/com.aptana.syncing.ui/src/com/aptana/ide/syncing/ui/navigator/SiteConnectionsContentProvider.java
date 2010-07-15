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
package com.aptana.ide.syncing.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.ui.io.navigator.FileSystemObject;
import com.aptana.ide.ui.io.navigator.FileTreeContentProvider;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SiteConnectionsContentProvider extends FileTreeContentProvider { /* using FileTreeContentProvider is correct here! */

	private static final Object[] EMPTY = new Object[0];

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IWorkspaceRoot) {
            inputElement = SyncingPlugin.getSiteConnectionManager();
        }
        return super.getElements(inputElement);
    }

    @Override
    public Object[] getChildren(Object element) {
        if (element instanceof IProject) {
        	IProject project = (IProject) element;
        	if (project.isAccessible())
        	{
        		Object[] children = new Object[1];
        		children[0] = ProjectSitesManager.getInstance().getProjectSites(project);
        		return children;
        	}
		}
		else if (element instanceof ProjectSiteConnection)
		{
			IConnectionPoint connectionPoint = ((ProjectSiteConnection) element).getSiteConnection().getDestination();
			if (connectionPoint instanceof LocalConnectionPoint)
			{
				try
				{
					return fetchFileSystemChildren(((LocalConnectionPoint) connectionPoint).getRoot(),
							new NullProgressMonitor());
				}
				catch (CoreException e)
				{
					return EMPTY;
				}
			}
			else if (connectionPoint instanceof WorkspaceConnectionPoint)
			{
				try
				{
					return ((WorkspaceConnectionPoint) connectionPoint).getResource().members();
				}
				catch (CoreException e)
				{
					return EMPTY;
				}
			}
		}
		return super.getChildren(element);
    }

	private static FileSystemObject[] fetchFileSystemChildren(IFileStore parent, IProgressMonitor monitor)
			throws CoreException
	{
		IFileInfo[] fileInfos = parent.childInfos(EFS.NONE, monitor);
		List<FileSystemObject> list = new ArrayList<FileSystemObject>();
		for (IFileInfo fi : fileInfos)
		{
			list.add(new FileSystemObject(parent.getChild(fi.getName()), fi));
		}
		return list.toArray(new FileSystemObject[list.size()]);
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.navigator.FileSystemObject;
import com.aptana.ide.ui.io.navigator.FileTreeContentProvider;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SiteConnectionsContentProvider extends FileTreeContentProvider
{

	/* using FileTreeContentProvider is correct here! */

	private static final Object[] EMPTY = new Object[0];
	// @formatter:off
	/**
	 * The natures correspond
	 */
	private static final String[] APTANA_NATURES = new String[] {
			"com.aptana.projects.webnature", //Web Nature //$NON-NLS-1$
			"com.aptana.editor.php.phpNature",  //PHP Nature //$NON-NLS-1$
			"org.python.pydev.pythonNature",  // PyDev Nature //$NON-NLS-1$
			"org.radrails.rails.core.railsnature",  // Rails Nature //$NON-NLS-1$
			"com.aptana.ruby.core.rubynature" }; //Ruby nature //$NON-NLS-1$
	// @formatter:on

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IWorkspaceRoot)
		{
			inputElement = SyncingPlugin.getSiteConnectionManager();
		}
		return super.getElements(inputElement);
	}

	@Override
	public Object[] getChildren(Object element)
	{
		if (element instanceof IProject)
		{
			IProject project = (IProject) element;
			if (project.isAccessible())
			{
				boolean isAptanaProject = isAptanaProject(project);
				Object[] children;
				if (isAptanaProject)
				{
					children = new Object[1];
					children[0] = ProjectSitesManager.getInstance().getProjectSites(project);
				}
				else
				{
					children = new Object[0];
				}
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

	private boolean isAptanaProject(IProject project)
	{
		try
		{
			String[] natures = project.getDescription().getNatureIds();
			if (ArrayUtil.isEmpty(natures))
			{
				return false;
			}
			// Verify whether the primary nature is one of Aptana projects.
			if (StringUtil.contains(APTANA_NATURES, natures[0]))
			{
				return true;
			}

		}
		catch (CoreException ignore)
		{
		}
		return false;
	}

	private static FileSystemObject[] fetchFileSystemChildren(IFileStore parent, IProgressMonitor monitor)
			throws CoreException
	{
		IFileInfo[] fileInfos = FileSystemUtils.childInfos(parent, EFS.NONE, monitor);
		List<FileSystemObject> list = new ArrayList<FileSystemObject>();
		for (IFileInfo fi : fileInfos)
		{
			list.add(new FileSystemObject(parent.getChild(fi.getName()), fi));
		}
		return list.toArray(new FileSystemObject[list.size()]);
	}
}

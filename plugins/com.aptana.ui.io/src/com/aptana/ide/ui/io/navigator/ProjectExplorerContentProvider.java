/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.core.util.ArrayUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ui.util.UIUtils;

public class ProjectExplorerContentProvider extends FileTreeContentProvider
{

	private static final String LOCAL_SHORTCUTS_ID = "com.aptana.ide.core.io.localShortcuts"; //$NON-NLS-1$

	private Viewer treeViewer;

	private IResourceChangeListener resourceListener = new IResourceChangeListener()
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			// to fix https://jira.appcelerator.org/browse/TISTUD-1695, we need to force a selection update when a
			// project is closed or opened
			if (shouldUpdateActions(event.getDelta()))
			{
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						treeViewer.setSelection(treeViewer.getSelection());
					}
				});
			}
		}
	};

	public ProjectExplorerContentProvider()
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public void dispose()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		super.dispose();
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof IResource)
		{
			return ArrayUtil.NO_OBJECTS;
		}
		return super.getChildren(parentElement);
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IWorkspaceRoot)
		{
			List<Object> children = new ArrayList<Object>();
			children.add(LocalFileSystems.getInstance());
			children.add(CoreIOPlugin.getConnectionPointManager().getConnectionPointCategory(LOCAL_SHORTCUTS_ID));
			return children.toArray(new Object[children.size()]);
		}
		return super.getElements(inputElement);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		treeViewer = viewer;
		super.inputChanged(viewer, oldInput, newInput);
	}

	private boolean shouldUpdateActions(IResourceDelta delta)
	{
		if (delta.getFlags() == IResourceDelta.OPEN)
		{
			return true;
		}
		IResourceDelta[] children = delta.getAffectedChildren();
		for (IResourceDelta child : children)
		{
			if (shouldUpdateActions(child))
			{
				return true;
			}
		}
		return false;
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.ClassElement;
import com.aptana.js.core.model.ClassGroupElement;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.JSElement;
import com.aptana.js.core.model.TypeElement;

/**
 * JSIndexViewContentProvider
 */
public class JSIndexViewContentProvider implements ITreeContentProvider
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		List<? extends Object> result = Collections.emptyList();

		if (parentElement instanceof JSElement)
		{
			JSElement root = (JSElement) parentElement;
			List<ClassGroupElement> temp = CollectionsUtil.newList(new ClassGroupElement(
					Messages.JSIndexViewContentProvider_WorkspaceGroupLabel, JSIndexQueryHelper.getJSCoreIndex()),
					new ClassGroupElement(Messages.JSIndexViewContentProvider_ProjectGroupLabel, root.getIndex()));

			IProject project = root.getProject();
			if (project != null)
			{
				// Grab the project build paths and set up the indices
				Set<IBuildPathEntry> entries = getBuildPathManager().getBuildPaths(project);
				for (IBuildPathEntry entry : entries)
				{
					Index index = getIndexManager().getIndex(entry.getPath());
					temp.add(new ClassGroupElement(index.getRoot().toASCIIString(), index));
				}
			}
			result = temp;
		}
		else if (parentElement instanceof ClassGroupElement)
		{
			ClassGroupElement group = (ClassGroupElement) parentElement;

			result = group.getClasses();
		}
		else if (parentElement instanceof ClassElement)
		{
			TypeElement type = (ClassElement) parentElement;
			// NOTE: have to do this "temp" acrobatics to make the compiler happy, due to use of generics and differing
			// return types when grabbing properties vs events
			List<Object> temp = new ArrayList<Object>();

			temp.addAll(type.getProperties());
			temp.addAll(type.getEvents());

			result = temp;
		}
		else if (parentElement instanceof EventElement)
		{
			EventElement event = (EventElement) parentElement;

			result = event.getProperties();
		}

		return result.toArray(new Object[result.size()]);
	}

	protected BuildPathManager getBuildPathManager()
	{
		return BuildPathManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] result;

		if (inputElement instanceof IProject)
		{
			IProject project = (IProject) inputElement;

			result = new Object[] { new JSElement(project) };
		}
		else
		{
			result = new Object[0];
		}

		return result;
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof ClassGroupElement)
		{
			ClassGroupElement classGroup = (ClassGroupElement) element;

			return classGroup.hasChildren();
		}
		else
		{
			return getChildren(element).length > 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// do nothing
	}
}

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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.ClassElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

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

		if (parentElement instanceof ClassElement)
		{
			TypeElement type = (ClassElement) parentElement;

			result = type.getProperties();
		}

		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List<ClassElement> classes = Collections.emptyList();

		if (inputElement instanceof IProject)
		{
			IProject project = (IProject) inputElement;
			Index index = IndexManager.getInstance().getIndex(project.getLocationURI());
			JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
			List<TypeElement> types = new ArrayList<TypeElement>();

			// TODO: add preference to show/hide types from the built-in metadata
			types.addAll(queryHelper.getTypes());
			types.addAll(queryHelper.getTypes(index));

			classes = JSTypeUtil.typesToClasses(types);
		}

		return classes.toArray();
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
		return getChildren(element).length > 0;
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

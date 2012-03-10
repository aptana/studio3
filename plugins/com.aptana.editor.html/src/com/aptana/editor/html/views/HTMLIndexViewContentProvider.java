/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.views;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.html.contentassist.model.CSSReferencesGroup;
import com.aptana.editor.html.contentassist.model.HTMLElement;
import com.aptana.editor.html.contentassist.model.JSReferencesGroup;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;

public class HTMLIndexViewContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_ELEMENTS = new Object[0];

	public void dispose()
	{
		// do nothing
	}

	@SuppressWarnings("unchecked")
	public Object[] getChildren(Object parentElement)
	{
		List<?> result = Collections.emptyList();

		if (parentElement instanceof HTMLElement)
		{
			HTMLElement root = (HTMLElement) parentElement;

			// @formatter:off
			result = CollectionsUtil.newList(
				new CSSReferencesGroup(root.getIndex()),
				new JSReferencesGroup(root.getIndex())
			);
			// @formatter:on
		}
		else if (parentElement instanceof CSSReferencesGroup)
		{
			result = ((CSSReferencesGroup) parentElement).getReferences();
		}
		else if (parentElement instanceof JSReferencesGroup)
		{
			result = ((JSReferencesGroup) parentElement).getReferences();
		}

		return result.toArray(new Object[result.size()]);
	}

	public Object[] getElements(Object inputElement)
	{
		Object[] result;

		if (inputElement instanceof IProject)
		{
			IProject project = (IProject) inputElement;
			Index index = getIndexManager().getIndex(project.getLocationURI());

			result = new Object[] { new HTMLElement(index) };
		}
		else
		{
			result = NO_ELEMENTS;
		}

		return result;
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	public Object getParent(Object element)
	{
		return null;
	}

	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// do nothing
	}
}

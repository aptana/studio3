/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * IndexViewContentProvider
 */
public class IndexViewContentProvider extends AbstractProvider<ITreeContentProvider> implements ITreeContentProvider
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
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List<ITreeContentProvider> providers = this.getProcessors();
		List<Object> result = new ArrayList<Object>();

		for (ITreeContentProvider provider : providers)
		{
			Object[] items = provider.getElements(inputElement);

			result.addAll(Arrays.asList(items));
		}

		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		List<ITreeContentProvider> providers = this.getProcessors();
		List<Object> result = new ArrayList<Object>();

		for (ITreeContentProvider provider : providers)
		{
			Object[] items = provider.getChildren(parentElement);

			result.addAll(Arrays.asList(items));
		}

		return result.toArray();
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
		List<ITreeContentProvider> providers = this.getProcessors();
		boolean result = false;

		for (ITreeContentProvider provider : providers)
		{
			if (provider.hasChildren(element))
			{
				result = true;
				break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.ui.views.ClassProcessor#getAttributeName()
	 */
	@Override
	public String getAttributeName()
	{
		return "content-provider"; //$NON-NLS-1$
	}
}

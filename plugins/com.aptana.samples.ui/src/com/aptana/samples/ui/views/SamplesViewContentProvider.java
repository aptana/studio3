/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.model.SampleEntry;
import com.aptana.samples.model.SamplesReference;

/**
 * @author Kevin Lindsey
 * @author Michael Xia
 */
public class SamplesViewContentProvider implements ITreeContentProvider
{

	public SamplesViewContentProvider()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ISamplesManager)
		{
			List<SampleCategory> categories = ((ISamplesManager) parentElement).getCategories();
			return categories.toArray(new SampleCategory[categories.size()]);
		}
		if (parentElement instanceof SampleCategory)
		{
			List<SamplesReference> samplesRefs = SamplesPlugin.getDefault().getSamplesManager()
					.getSamplesForCategory(((SampleCategory) parentElement).getId());
			List<Object> children = new ArrayList<Object>();
			for (SamplesReference ref : samplesRefs)
			{
				if (ref.isRemote())
				{
					// uses the reference directly
					children.add(ref);
				}
				else
				{
					// uses the folders the root contains
					children.addAll(ref.getSamples());
				}
			}
			return children.toArray(new Object[children.size()]);
		}
		if (parentElement instanceof SampleEntry)
		{
			return ((SampleEntry) parentElement).getSubEntries();
		}
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof SampleEntry)
		{
			Object parent = ((SampleEntry) element).getParent();
			if (parent instanceof SamplesReference)
			{
				return ((SamplesReference) parent).getCategory();
			}
			return parent;
		}
		if (element instanceof SamplesReference)
		{
			return ((SamplesReference) element).getCategory();
		}
		if (element instanceof SampleCategory)
		{
			return SamplesPlugin.getDefault().getSamplesManager();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}

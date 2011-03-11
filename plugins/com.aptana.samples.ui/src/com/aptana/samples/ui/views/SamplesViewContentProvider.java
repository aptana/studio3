/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
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
			return ((ISamplesManager) parentElement).getSamples();
		}
		if (parentElement instanceof SamplesReference)
		{
			return ((SamplesReference) parentElement).getSamples();
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
			return ((SampleEntry) element).getParent();
		}
		if (element instanceof SamplesReference)
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

package com.aptana.scripting.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.scripting.model.BundleManager;

class BundleViewContentProvider implements ITreeContentProvider
{
	private static final Object[] NO_ELEMENTS = new Object[0];

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		Object[] children = NO_ELEMENTS;

		if (parentElement instanceof IBundleViewNode)
		{
			children = ((IBundleViewNode) parentElement).getChildren();
		}

		return children;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		Object[] elements = NO_ELEMENTS;

		if (inputElement instanceof BundleManager)
		{
			BundleManager manager = (BundleManager) inputElement;
			String[] names = manager.getBundleNames();

			elements = new BundleEntryNode[names.length];

			for (int i = 0; i < names.length; i++)
			{
				elements[i] = new BundleEntryNode(manager.getBundleEntry(names[i]));
			}
		}

		return elements;
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
		boolean result = false;

		if (element instanceof IBundleViewNode)
		{
			result = ((IBundleViewNode) element).hasChildren();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}

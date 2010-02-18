package com.aptana.scripting.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.SnippetElement;

class BundleViewLabelProvider implements ILabelProvider
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result = null;

		if (element instanceof CollectionNode)
		{
			result = ((CollectionNode) element).getLabel();
		}
		else if (element instanceof SnippetElement)
		{
			SnippetElement snippet = (SnippetElement) element;
			String[] triggers = snippet.getTriggers();

			if (triggers != null && triggers.length > 0)
			{
				result = triggers[0] + ": " + snippet.getDisplayName(); //$NON-NLS-1$
			}
			else
			{
				result = snippet.getDisplayName();
			}
		}
		else if (element instanceof AbstractElement)
		{
			result = ((AbstractElement) element).getDisplayName();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
	}
}

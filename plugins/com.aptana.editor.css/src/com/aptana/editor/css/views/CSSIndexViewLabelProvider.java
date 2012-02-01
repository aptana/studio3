package com.aptana.editor.css.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.contentassist.model.BaseElement;
import com.aptana.editor.css.contentassist.model.CSSElement;
import com.aptana.editor.css.contentassist.model.ClassGroupElement;
import com.aptana.editor.css.contentassist.model.ColorGroupElement;
import com.aptana.editor.css.contentassist.model.IdGroupElement;

public class CSSIndexViewLabelProvider implements ILabelProvider
{
	private static final Image CSS_ICON = CSSPlugin.getImage("icons/css.png"); //$NON-NLS-1$
	private static final Image FOLDER_ICON = CSSPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = CSSPlugin.getImage("icons/property.png"); //$NON-NLS-1$

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public Image getImage(Object element)
	{
		Image result = null;

		if (element instanceof CSSElement)
		{
			result = CSS_ICON;
		}
		else if (element instanceof ClassGroupElement || element instanceof IdGroupElement
				|| element instanceof ColorGroupElement)
		{
			result = FOLDER_ICON;
		}
		else if (element instanceof String)
		{
			result = PROPERTY_ICON;
		}

		return result;
	}

	public String getText(Object element)
	{
		String result = null;

		if (element instanceof BaseElement)
		{
			result = ((BaseElement<?>) element).getName();
		}
		else if (element instanceof String)
		{
			result = (String) element;
		}

		return result;
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
	}
}

package com.aptana.editor.html.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;

public class HTMLOutlineLabelProvider extends LabelProvider
{
	private static final Image ELEMENT_ICON = Activator.getImage("icons/element.gif"); //$NON-NLS-1$

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof HTMLElementNode)
		{
			return ELEMENT_ICON;
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof HTMLElementNode)
		{
			return ((HTMLElementNode) element).getName();
		}
		return super.getText(element);
	}
}

package com.aptana.editor.xml.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;

public class XMLOutlineLabelProvider extends LabelProvider
{

	private static final Image ELEMENT_ICON = XMLPlugin.getImage("icons/element.png"); //$NON-NLS-1$

	public XMLOutlineLabelProvider()
	{
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return ELEMENT_ICON;
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getText(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof XMLElementNode)
		{
			return ((XMLElementNode) element).getName();
		}
		return super.getText(element);
	}
}

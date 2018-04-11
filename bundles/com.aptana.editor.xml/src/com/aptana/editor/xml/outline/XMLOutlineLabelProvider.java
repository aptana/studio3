/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.xml.core.parsing.ast.XMLElementNode;

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
			XMLElementNode node = (XMLElementNode) element;
			IParseNodeAttribute[] attributes = node.getAttributes();
			
			if (attributes != null && attributes.length > 0)
			{
				return node.getName() + " : " + attributes[0].getValue(); //$NON-NLS-1$
			}
			else
			{
				return node.getName();
			}
		}
		return super.getText(element);
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.json.JSONPlugin;
import com.aptana.json.core.parsing.ast.JSONEntryNode;
import com.aptana.json.core.parsing.ast.JSONNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JSONOutlineLabelProvider
 */
public class JSONOutlineLabelProvider extends LabelProvider
{
	private static final Image ARRAY = JSONPlugin.getImage("icons/array-literal.png"); //$NON-NLS-1$
	private static final Image BOOLEAN = JSONPlugin.getImage("icons/boolean.png"); //$NON-NLS-1$
	private static final Image NULL = JSONPlugin.getImage("icons/null.png"); //$NON-NLS-1$
	private static final Image NUMBER = JSONPlugin.getImage("icons/number.png"); //$NON-NLS-1$
	private static final Image OBJECT = JSONPlugin.getImage("icons/object-literal.png"); //$NON-NLS-1$
	private static final Image STRING = JSONPlugin.getImage("icons/string.png"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element)
	{
		Image result = null;

		if (element instanceof JSONNode)
		{
			JSONNode node = (JSONNode) element;

			switch (node.getType())
			{
				case ARRAY:
					result = ARRAY;
					break;

				case TRUE:
				case FALSE:
					result = BOOLEAN;
					break;

				case NULL:
					result = NULL;
					break;

				case NUMBER:
					result = NUMBER;
					break;

				case OBJECT:
					result = OBJECT;
					break;

				case STRING:
					result = STRING;
					break;
			}
		}
		else if (element instanceof CommonOutlineItem)
		{
			CommonOutlineItem item = (CommonOutlineItem) element;
			
			result = this.getImage(item.getReferenceNode());
		}

		return (result == null) ? super.getImage(element) : result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element)
	{
		String result = null;

		if (element instanceof JSONNode)
		{
			JSONNode node = (JSONNode) element;
			IParseNode parent = node.getParent();

			if (parent instanceof JSONEntryNode)
			{
				result = parent.getFirstChild().getText();
			}
			else
			{
				result = node.getText();
			}
		}
		else if (element instanceof CommonOutlineItem)
		{
			CommonOutlineItem item = (CommonOutlineItem) element;
			
			result = this.getText(item.getReferenceNode());
		}

		return (result == null) ? super.getText(element) : result;
	}

}

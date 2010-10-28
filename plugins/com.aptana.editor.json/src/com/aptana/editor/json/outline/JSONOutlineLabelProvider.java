/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.json.JSONPlugin;
import com.aptana.editor.json.parsing.ast.JSONEntryNode;
import com.aptana.editor.json.parsing.ast.JSONNode;
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

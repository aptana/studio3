/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSSelectorNode;

public class CSSOutlineLabelProvider extends CommonOutlineLabelProvider
{

	private static final Image SELECTOR_ICON = CSSPlugin.getImage("icons/selector.png"); //$NON-NLS-1$
	private static final Image DECLARATION_ICON = CSSPlugin.getImage("icons/declaration.png"); //$NON-NLS-1$

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CSSSelectorNode)
		{
			return SELECTOR_ICON;
		}
		if (element instanceof CSSDeclarationNode)
		{
			return DECLARATION_ICON;
		}
		return super.getImage(element);
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.css.core.parsing.ast.CSSAtRuleNode;
import com.aptana.css.core.parsing.ast.CSSCharSetNode;
import com.aptana.css.core.parsing.ast.CSSDeclarationNode;
import com.aptana.css.core.parsing.ast.CSSFontFaceNode;
import com.aptana.css.core.parsing.ast.CSSImportNode;
import com.aptana.css.core.parsing.ast.CSSMediaNode;
import com.aptana.css.core.parsing.ast.CSSNamespaceNode;
import com.aptana.css.core.parsing.ast.CSSNode;
import com.aptana.css.core.parsing.ast.CSSPageNode;
import com.aptana.css.core.parsing.ast.CSSSelectorNode;
import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.editor.css.CSSPlugin;

public class CSSOutlineLabelProvider extends CommonOutlineLabelProvider
{

	private static final Image SELECTOR_ICON = CSSPlugin.getImage("icons/selector.png"); //$NON-NLS-1$
	private static final Image DECLARATION_ICON = CSSPlugin.getImage("icons/declaration.png"); //$NON-NLS-1$
	private static final Image IMPORT_ICON = CSSPlugin.getImage("icons/import_obj.png"); //$NON-NLS-1$
	private static final Image AT_RULE_ICON = CSSPlugin.getImage("icons/at_rule.png"); //$NON-NLS-1$

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
		if (element instanceof CSSImportNode)
		{
			return IMPORT_ICON;
		}
		if (element instanceof CSSAtRuleNode || element instanceof CSSCharSetNode || element instanceof CSSMediaNode
				|| element instanceof CSSPageNode || element instanceof CSSFontFaceNode
				|| element instanceof CSSNamespaceNode)
		{
			return AT_RULE_ICON;
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CSSNode)
		{
			String text = ((CSSNode) element).getText();
			if (element instanceof CSSAtRuleNode || element instanceof CSSCharSetNode
					|| element instanceof CSSMediaNode || element instanceof CSSPageNode
					|| element instanceof CSSFontFaceNode || element instanceof CSSNamespaceNode
					|| element instanceof CSSImportNode)
			{
				// removes the leading @
				text = text.substring(1);
			}
			return text;
		}
		return super.getText(element);
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;

import com.aptana.css.core.parsing.ast.CSSCommentNode;
import com.aptana.css.core.parsing.ast.CSSFontFaceNode;
import com.aptana.css.core.parsing.ast.CSSMediaNode;
import com.aptana.css.core.parsing.ast.CSSPageNode;
import com.aptana.css.core.parsing.ast.CSSRuleNode;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.preferences.IPreferenceConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CSSFoldingComputer extends AbstractFoldingComputer
{

	public CSSFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	public boolean isFoldable(IParseNode child)
	{
		return (child instanceof CSSCommentNode) || (child instanceof CSSRuleNode) || (child instanceof CSSMediaNode)
				|| (child instanceof CSSPageNode) || (child instanceof CSSFontFaceNode);
	}

	protected IParseNode[] getChildren(IParseNode parseNode)
	{
		if (parseNode instanceof CSSMediaNode)
		{
			CSSMediaNode mediaNode = (CSSMediaNode) parseNode;
			return mediaNode.getStatements();
		}
		return super.getChildren(parseNode);
	}

	@Override
	protected boolean traverseInto(IParseNode child)
	{
		return (child instanceof CSSMediaNode)
				|| (((child instanceof ParseRootNode) || (child instanceof CSSPageNode)) && child.hasChildren());
	}

	@Override
	public boolean isCollapsed(IParseNode node)
	{
		if (node instanceof CSSCommentNode)
		{
			return Platform.getPreferencesService().getBoolean(CSSPlugin.PLUGIN_ID,
					IPreferenceConstants.INITIALLY_FOLD_COMMENTS, false, null);
		}
		if (node instanceof CSSRuleNode)
		{
			return Platform.getPreferencesService().getBoolean(CSSPlugin.PLUGIN_ID,
					IPreferenceConstants.INITIALLY_FOLD_RULES, false, null);
		}
		return false;
	}
}

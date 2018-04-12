/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.jface.text.IDocument;

import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.editor.css.internal.text.CSSFoldingComputer;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLTextNode;
import com.aptana.editor.js.internal.text.JSFoldingComputer;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.ast.IParseNode;

public class HTMLFoldingComputer extends AbstractFoldingComputer
{

	public HTMLFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	public boolean isFoldable(IParseNode child)
	{
		String language = child.getLanguage();
		if (IJSConstants.CONTENT_TYPE_JS.equals(language))
		{
			return getJSFoldingComputer().isFoldable(child);
		}
		else if (ICSSConstants.CONTENT_TYPE_CSS.equals(language))
		{
			return getCSSFoldingComputer().isFoldable(child);
		}
		return child instanceof HTMLNode && !(child instanceof HTMLTextNode);
	}

	@Override
	public boolean isCollapsed(IParseNode child)
	{
		String language = child.getLanguage();
		if (IJSConstants.CONTENT_TYPE_JS.equals(language))
		{
			return getJSFoldingComputer().isCollapsed(child);
		}
		else if (ICSSConstants.CONTENT_TYPE_CSS.equals(language))
		{
			return getCSSFoldingComputer().isCollapsed(child);
		}
		return false;
	}

	private AbstractFoldingComputer getJSFoldingComputer()
	{
		return new JSFoldingComputer(getEditor(), getDocument());
	}

	private AbstractFoldingComputer getCSSFoldingComputer()
	{
		return new CSSFoldingComputer(getEditor(), getDocument());
	}

}

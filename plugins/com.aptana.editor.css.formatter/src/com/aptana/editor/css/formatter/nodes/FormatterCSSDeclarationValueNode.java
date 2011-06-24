/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.nodes;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A CSS formatter node for values of a declaration.<br>
 * This node is used to represent a value part of a declaration statemenet.
 */

public class FormatterCSSDeclarationValueNode extends FormatterBlockWithBeginNode
{

	private boolean isLastNodeInDeclaration;

	public FormatterCSSDeclarationValueNode(IFormatterDocument document, boolean isLastNodeInDeclaration)
	{
		super(document);
		this.isLastNodeInDeclaration = isLastNodeInDeclaration;
	}


	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return isLastNodeInDeclaration ? getInt(CSSFormatterConstants.LINES_AFTER_DECLARATION) : super
				.getBlankLinesAfter(context);
	}

	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return true;
	}

}

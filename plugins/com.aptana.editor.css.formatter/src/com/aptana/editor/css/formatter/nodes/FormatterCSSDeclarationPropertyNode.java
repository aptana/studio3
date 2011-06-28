/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.nodes;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A CSS formatter node for declaration properties.<br>
 * This node is used for the property name of every declaration (this should be the first node in a declaration
 * statement)
 */

public class FormatterCSSDeclarationPropertyNode extends FormatterBlockWithBeginNode
{

	/**
	 * @param document
	 */
	public FormatterCSSDeclarationPropertyNode(IFormatterDocument document)
	{
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	protected boolean isAddingBeginNewLine()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */

	protected boolean isAddingEndNewLine()
	{
		return false;
	}

}

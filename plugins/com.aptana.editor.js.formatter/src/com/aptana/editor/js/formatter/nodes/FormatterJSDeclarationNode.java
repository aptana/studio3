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
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A JS declaration formatter node.<br>
 * This node represents a declaration part of a javascript block. It can be a function declaration, an if statement
 * part, a while statement declaration etc. Everything up to the open bracket (if exists) will be in this 'declaration'.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSDeclarationNode extends FormatterBlockWithBeginNode
{

	private boolean hasBlockedChild;
	private JSNode parent;

	/**
	 * @param document
	 * @param hasBlockedChild
	 * @param noNewLine
	 *            Provide a hint flag to block any new line added before this node. Note that this is just a hint which
	 *            can be overwritten by a preference setting.
	 */
	public FormatterJSDeclarationNode(IFormatterDocument document, boolean hasBlockedChild, JSNode parent)
	{
		super(document);
		this.hasBlockedChild = hasBlockedChild;
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		// To change this behavior, it's recommended to create a designated subclass and override this method to return
		// the value set in the preferences.
		if (parent instanceof JSBinaryOperatorNode)
		{
			return false;
		}
		switch (parent.getNodeType())
		{
			case JSNodeTypes.DECLARATION:
			case JSNodeTypes.ASSIGN:
			case JSNodeTypes.RETURN:
			case JSNodeTypes.INVOKE:
			case JSNodeTypes.GROUP:
			case JSNodeTypes.ARGUMENTS:
			case JSNodeTypes.CONDITIONAL:
			case JSNodeTypes.NAME_VALUE_PAIR:
			case JSNodeTypes.FUNCTION:
				return false;
//			case JSNodeTypes.DO:
//			case JSNodeTypes.TRY:
//			case JSNodeTypes.SWITCH:
//			case JSNodeTypes.FOR:
//			case JSNodeTypes.FOR_IN:
//			case JSNodeTypes.WHILE:
//				return !hasBlockedChild || getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_BLOCKS);
			case JSNodeTypes.IF:
				return getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_IF_STATEMENT);
			case JSNodeTypes.CATCH:
				return !hasBlockedChild || getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT);
			case JSNodeTypes.FINALLY:
				return !hasBlockedChild || getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return !hasBlockedChild;
	}
}

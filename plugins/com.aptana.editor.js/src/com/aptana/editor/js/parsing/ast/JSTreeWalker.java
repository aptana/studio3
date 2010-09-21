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
package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSTreeWalker
{
	public void visit(JSArgumentsNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSArrayNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSAssignmentNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSBinaryArithmeticOperatorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSBinaryBooleanOperatorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSBreakNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSCaseNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSCatchNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSCommaNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSConditionalNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSConstructNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSContinueNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSDeclarationNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSDefaultNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSDoNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSElementsNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSElisionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSEmptyNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSErrorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSFalseNode node)
	{
		// leaf
	}

	public void visit(JSFinallyNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSForInNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSForNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSFunctionNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSGetElementNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSGetPropertyNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSGroupNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSIdentifierNode node)
	{
		// leaf
	}

	public void visit(JSIfNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSInvokeNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSLabelledNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSNameValuePairNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSNode node)
	{
		node.accept(this);
	}

	public void visit(JSNullNode node)
	{
		// leaf
	}

	public void visit(JSNumberNode node)
	{
		// leaf
	}

	public void visit(JSObjectNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSParametersNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSParseRootNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof JSNode)
			{
				((JSNode) child).accept(this);
			}
		}
	}

	public void visit(JSPostUnaryOperatorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSPreUnaryOperatorNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSRegexNode node)
	{
		// leaf
	}

	public void visit(JSReturnNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSStatementsNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSStringNode node)
	{
		// leaf
	}

	public void visit(JSSwitchNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSThisNode node)
	{
		// leaf
	}

	public void visit(JSThrowNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSTrueNode node)
	{
		// leaf
	}

	public void visit(JSTryNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSVarNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSWhileNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSWithNode node)
	{
		this.visitChildren(node);
	}

	/**
	 * visitChildren
	 * 
	 * @param node
	 */
	protected void visitChildren(JSNode node)
	{
		for (IParseNode child : node)
		{
			if (child instanceof JSNode)
			{
				((JSNode) child).accept(this);
			}
		}
	}
}

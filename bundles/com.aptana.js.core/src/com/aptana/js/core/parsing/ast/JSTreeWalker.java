/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

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

	public void visit(JSArrowFunctionNode node)
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

	public void visit(JSClassNode node)
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

	public void visit(JSDestructuringNode node)
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

	public void visit(JSExportNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSExportSpecifierNode node)
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

	public void visit(JSForOfNode node)
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

	public void visit(JSGeneratorFunctionNode node)
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

	public void visit(JSNamedImportsNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSImportNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSImportSpecifierNode node)
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

	public void visit(JSYieldNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSSpreadElementNode node)
	{
		this.visitChildren(node);
	}

	public void visit(JSRestElementNode node)
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

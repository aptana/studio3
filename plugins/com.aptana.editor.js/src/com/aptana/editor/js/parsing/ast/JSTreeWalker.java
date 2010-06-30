package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class JSTreeWalker
{
	public void visit(JSArgumentsNode node)
	{
	}

	public void visit(JSBinaryArithmeticOperatorNode node)
	{
	}

	public void visit(JSArrayNode node)
	{
	}

	public void visit(JSAssignmentNode node)
	{
	}

	public void visit(JSBinaryBooleanOperatorNode node)
	{
	}

	public void visit(JSBreakNode node)
	{
	}

	public void visit(JSCaseNode node)
	{
	}

	public void visit(JSCatchNode node)
	{
	}

	public void visit(JSCommaNode node)
	{
	}

	public void visit(JSConditionalNode node)
	{
	}

	public void visit(JSConstructNode node)
	{
	}

	public void visit(JSContinueNode node)
	{
	}

	public void visit(JSDeclarationNode node)
	{
	}

	public void visit(JSDefaultNode node)
	{
	}

	public void visit(JSDoNode node)
	{
	}

	public void visit(JSElementsNode node)
	{
	}

	public void visit(JSElisionNode node)
	{
	}

	public void visit(JSEmptyNode node)
	{
	}

	public void visit(JSErrorNode node)
	{
	}

	public void visit(JSFalseNode node)
	{
		// leaf
	}

	public void visit(JSFinallyNode node)
	{
	}

	public void visit(JSForInNode node)
	{
	}

	public void visit(JSForNode node)
	{
	}

	public void visit(JSFunctionNode node)
	{
	}

	public void visit(JSGetElementNode node)
	{
	}

	public void visit(JSGetPropertyNode node)
	{
	}

	public void visit(JSGroupNode node)
	{
	}

	public void visit(JSIdentifierNode node)
	{
		// leaf
	}

	public void visit(JSIfNode node)
	{
	}

	public void visit(JSInvokeNode node)
	{
	}

	public void visit(JSLabelledNode node)
	{
	}

	public void visit(JSLabelStatementNode node)
	{
	}

	public void visit(JSNameValuePairNode node)
	{
	}

	public void visit(JSNaryAndExpressionNode node)
	{
	}

	public void visit(JSNaryNode node)
	{
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
	}

	public void visit(JSParametersNode node)
	{
	}

	public void visit(JSParseRootNode node)
	{
	}

	public void visit(JSPostUnaryOperatorNode node)
	{
	}

	public void visit(JSPrimitiveNode node)
	{
		// leaf
	}

	public void visit(JSRegexNode node)
	{
		// leaf
	}

	public void visit(JSReturnNode node)
	{
	}

	public void visit(JSStatementsNode node)
	{
	}

	public void visit(JSStringNode node)
	{
		// leaf
	}

	public void visit(JSSwitchNode node)
	{
	}

	public void visit(JSThisNode node)
	{
		// leaf
	}

	public void visit(JSThrowNode node)
	{
	}

	public void visit(JSTrueNode node)
	{
	}

	public void visit(JSTryNode node)
	{
	}

	public void visit(JSUnaryOperatorNode node)
	{
	}

	public void visit(JSVarNode node)
	{
	}

	public void visit(JSWhileNode node)
	{
	}

	public void visit(JSWithNode node)
	{
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

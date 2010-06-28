package com.aptana.editor.js.parsing.ast;

import java.text.MessageFormat;

import com.aptana.editor.js.Activator;
import com.aptana.parsing.ast.IParseNode;

public class TreeWalker
{
	public void visit(JSParseRootNode node)
	{
	}
	
	protected void visitChildren(IParseNode node)
	{
		for (IParseNode child : node)
		{
			switch (child.getNodeType())
			{
				case JSNodeTypes.EMPTY:
					this.visit((JSEmptyNode) node);
					break;
					
				case JSNodeTypes.ASSIGN:
				case JSNodeTypes.ADD_AND_ASSIGN:
				case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
				case JSNodeTypes.BITWISE_AND_AND_ASSIGN:
				case JSNodeTypes.BITWISE_OR_AND_ASSIGN:
				case JSNodeTypes.BITWISE_XOR_AND_ASSIGN:
				case JSNodeTypes.DIVIDE_AND_ASSIGN:
				case JSNodeTypes.MOD_AND_ASSIGN:
				case JSNodeTypes.MULTIPLY_AND_ASSIGN:
				case JSNodeTypes.SHIFT_LEFT_AND_ASSIGN:
				case JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
				case JSNodeTypes.SUBTRACT_AND_ASSIGN:
					this.visit((JSAssignmentNode) node);
					break;
					
				case JSNodeTypes.NULL:
					this.visit((JSNullNode) node);
					break;
					
				case JSNodeTypes.TRUE:
					this.visit((JSTrueNode) node);
					break;
					
				case JSNodeTypes.FALSE:
					this.visit((JSFalseNode) node);
					break;
					
				case JSNodeTypes.NUMBER:
					this.visit((JSNumberNode) node);
					break;
					
				case JSNodeTypes.STRING:
					this.visit((JSStringNode) node);
					break;
					
				case JSNodeTypes.REGEX:
					this.visit((JSRegexNode) node);
					break;
					
				case JSNodeTypes.IDENTIFIER:
					this.visit((JSIdentifierNode) node);
					break;
					
				case JSNodeTypes.THIS:
					this.visit((JSThisNode) node);
					break;
					
				case JSNodeTypes.STATEMENTS:
					this.visit((JSStatementsNode) node);
					break;
					
				case JSNodeTypes.CONTINUE:
					this.visit((JSContinueNode) node);
					break;
					
				case JSNodeTypes.BREAK:
					this.visit((JSBreakNode) node);
					break;
					
				case JSNodeTypes.EQUAL:
				case JSNodeTypes.GREATER_THAN:
				case JSNodeTypes.GREATER_THAN_OR_EQUAL:
				case JSNodeTypes.IDENTITY:
				case JSNodeTypes.IN:
				case JSNodeTypes.INSTANCE_OF:
				case JSNodeTypes.LESS_THAN:
				case JSNodeTypes.LESS_THAN_OR_EQUAL:
				case JSNodeTypes.LOGICAL_AND:
				case JSNodeTypes.LOGICAL_OR:
				case JSNodeTypes.NOT_EQUAL:
				case JSNodeTypes.NOT_IDENTITY:
				case JSNodeTypes.ADD:
				case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT:
				case JSNodeTypes.BITWISE_AND:
				case JSNodeTypes.BITWISE_OR:
				case JSNodeTypes.BITWISE_XOR:
				case JSNodeTypes.DIVIDE:
				case JSNodeTypes.MOD:
				case JSNodeTypes.MULTIPLY:
				case JSNodeTypes.SHIFT_LEFT:
				case JSNodeTypes.SHIFT_RIGHT:
				case JSNodeTypes.SUBTRACT:
				case JSNodeTypes.GET_ELEMENT:
				case JSNodeTypes.GET_PROPERTY:
					this.visit((JSBinaryOperatorNode) node);
					break;
					
				case JSNodeTypes.DELETE:
				case JSNodeTypes.LOGICAL_NOT:
				case JSNodeTypes.NEGATIVE:
				case JSNodeTypes.PRE_DECREMENT:
				case JSNodeTypes.POSITIVE:
				case JSNodeTypes.PRE_INCREMENT:
				case JSNodeTypes.BITWISE_NOT:
				case JSNodeTypes.TYPEOF:
				case JSNodeTypes.VOID:
					this.visit((JSUnaryOperatorNode) node);
					break;
					
				case JSNodeTypes.GROUP:
					this.visit((JSGroupNode) node);
					break;
					
				case JSNodeTypes.POST_DECREMENT:
				case JSNodeTypes.POST_INCREMENT:
					this.visit((JSPostUnaryOperatorNode) node);
					break;
					
				case JSNodeTypes.ARGUMENTS:
					this.visit((JSArgumentsNode) node);
					break;
					
				case JSNodeTypes.INVOKE:
					this.visit((JSInvokeNode) node);
					break;
					
				case JSNodeTypes.DECLARATION:
					this.visit((JSDeclarationNode) node);
					break;
					
				case JSNodeTypes.VAR:
					this.visit((JSVarNode) node);
					break;
					
				case JSNodeTypes.TRY:
					this.visit((JSTryNode) node);
					break;
					
				case JSNodeTypes.CATCH:
					this.visit((JSCatchNode) node);
					break;
					
				case JSNodeTypes.FINALLY:
					this.visit((JSFinallyNode) node);
					break;
					
				case JSNodeTypes.CONDITIONAL:
					this.visit((JSConditionalNode) node);
					break;
					
				case JSNodeTypes.PARAMETERS:
					this.visit((JSParametersNode) node);
					break;
					
				case JSNodeTypes.FUNCTION:
					this.visit((JSFunctionNode) node);
					break;
					
				case JSNodeTypes.ELISION:
					this.visit((JSElisionNode) node);
					break;
					
				case JSNodeTypes.ELEMENTS:
					this.visit((JSElementsNode) node);
					break;
					
				case JSNodeTypes.ARRAY_LITERAL:
					this.visit((JSArrayNode) node);
					break;
					
				case JSNodeTypes.COMMA:
					this.visit((JSCommaNode) node);
					break;
					
				case JSNodeTypes.CONSTRUCT:
					this.visit((JSConstructNode) node);
					break;
					
				case JSNodeTypes.NAME_VALUE_PAIR:
					this.visit((JSNameValuePairNode) node);
					break;
					
				case JSNodeTypes.OBJECT_LITERAL:
					this.visit((JSObjectNode) node);
					break;
					
				case JSNodeTypes.THROW:
					this.visit((JSThrowNode) node);
					break;
					
				case JSNodeTypes.LABELLED:
					this.visit((JSLabelledNode) node);
					break;
					
				case JSNodeTypes.WHILE:
					this.visit((JSWhileNode) node);
					break;
					
				case JSNodeTypes.WITH:
					this.visit((JSWithNode) node);
					break;
					
				case JSNodeTypes.SWITCH:
					this.visit((JSSwitchNode) node);
					break;
					
				case JSNodeTypes.CASE:
					this.visit((JSCaseNode) node);
					break;
					
				case JSNodeTypes.DEFAULT:
					this.visit((JSDefaultNode) node);
					break;
					
				case JSNodeTypes.RETURN:
					this.visit((JSReturnNode) node);
					break;
					
				case JSNodeTypes.IF:
					this.visit((JSIfNode) node);
					break;
					
				case JSNodeTypes.DO:
					this.visit((JSDoNode) node);
					break;
					
				case JSNodeTypes.FOR:
					this.visit((JSForNode) node);
					break;
					
				case JSNodeTypes.FOR_IN:
					this.visit((JSForInNode) node);
					break;
					
				default:
					String message = MessageFormat.format(
						"Unrecognized JS node type: {0}",
						node.getNodeType()
					);
					
					Activator.logError(message, null);
			}
		}
	}
	
	public void visit(JSArgumentsNode node)
	{
	}
	
	public void visit(JSArrayNode node)
	{
	}
	
	public void visit(JSAssignmentNode node)
	{
	}
	
	public void visit(JSBinaryOperatorNode node)
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
}

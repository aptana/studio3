/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.util.EnumSet;

import beaver.Symbol;

import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSArrayNode;
import com.aptana.js.core.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.js.core.parsing.ast.JSBreakNode;
import com.aptana.js.core.parsing.ast.JSCaseNode;
import com.aptana.js.core.parsing.ast.JSCatchNode;
import com.aptana.js.core.parsing.ast.JSConditionalNode;
import com.aptana.js.core.parsing.ast.JSConstructNode;
import com.aptana.js.core.parsing.ast.JSContinueNode;
import com.aptana.js.core.parsing.ast.JSDeclarationNode;
import com.aptana.js.core.parsing.ast.JSDefaultNode;
import com.aptana.js.core.parsing.ast.JSDoNode;
import com.aptana.js.core.parsing.ast.JSErrorNode;
import com.aptana.js.core.parsing.ast.JSFalseNode;
import com.aptana.js.core.parsing.ast.JSFinallyNode;
import com.aptana.js.core.parsing.ast.JSForInNode;
import com.aptana.js.core.parsing.ast.JSForNode;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetElementNode;
import com.aptana.js.core.parsing.ast.JSGetPropertyNode;
import com.aptana.js.core.parsing.ast.JSGroupNode;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.js.core.parsing.ast.JSInvokeNode;
import com.aptana.js.core.parsing.ast.JSLabelledNode;
import com.aptana.js.core.parsing.ast.JSNameValuePairNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSNullNode;
import com.aptana.js.core.parsing.ast.JSNumberNode;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.js.core.parsing.ast.JSParametersNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.core.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSRegexNode;
import com.aptana.js.core.parsing.ast.JSReturnNode;
import com.aptana.js.core.parsing.ast.JSStatementsNode;
import com.aptana.js.core.parsing.ast.JSStringNode;
import com.aptana.js.core.parsing.ast.JSSwitchNode;
import com.aptana.js.core.parsing.ast.JSThisNode;
import com.aptana.js.core.parsing.ast.JSThrowNode;
import com.aptana.js.core.parsing.ast.JSTreeWalker;
import com.aptana.js.core.parsing.ast.JSTrueNode;
import com.aptana.js.core.parsing.ast.JSTryNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
import com.aptana.js.core.parsing.ast.JSWhileNode;
import com.aptana.js.core.parsing.ast.JSWithNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class JSLocationIdentifier extends JSTreeWalker
{
	private static final EnumSet<LocationType> IGNORED_TYPES = EnumSet.of(LocationType.UNKNOWN, LocationType.NONE); // $codepro.audit.disable
																													// declareAsInterface

	private int _offset;
	private IParseNode _targetNode;
	private IParseNode _statementNode;
	private LocationType _type;
	private IRange _replaceRange;

	/**
	 * JSLocationWalker
	 */
	public JSLocationIdentifier(int offset, IParseNode targetNode)
	{
		offset--;

		this._offset = offset;
		this._targetNode = targetNode;
		this._type = LocationType.UNKNOWN;
	}

	/**
	 * getReplaceRange
	 * 
	 * @return
	 */
	public IRange getReplaceRange()
	{
		return this._replaceRange;
	}

	/**
	 * getStatementNode
	 * 
	 * @return
	 */
	public IParseNode getStatementNode()
	{
		return this._statementNode;
	}

	/**
	 * getTargetNode
	 * 
	 * @return
	 */
	public IParseNode getTargetNode()
	{
		return this._targetNode;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public LocationType getType()
	{
		IParseNode ast = null;
		int actualOffset = this._offset + 1;

		this._statementNode = null;

		if (this._targetNode instanceof JSParseRootNode)
		{
			this._statementNode = this._targetNode;
			ast = this._targetNode;
		}
		else if (this._targetNode instanceof JSNode)
		{
			// set containing statement
			this._statementNode = ((JSNode) this._targetNode).getContainingStatementNode();

			// NOTE: We can't simply grab the AST since this will fail when JS
			// is embedded in other languages. In those cases, we'll get the
			// root node for the host language and not for JS

			// find JS root node
			IParseNode current = this._targetNode;

			while (current != null)
			{
				if (current instanceof JSParseRootNode)
				{
					ast = current;
					break;
				}
				else
				{
					current = current.getParent();
				}
			}
		}

		// try to determine the current offset's CA type via the AST
		if (ast == null)
		{
			this._type = LocationType.IN_GLOBAL;

			this._replaceRange = new Range(actualOffset, actualOffset - 1);
		}
		else if (ast instanceof JSParseRootNode)
		{
			((JSParseRootNode) ast).accept(this);

			if (!IGNORED_TYPES.contains(this._type))
			{
				JSRangeFinder rangeWalker = new JSRangeFinder(actualOffset);

				((JSParseRootNode) ast).accept(rangeWalker);

				this._replaceRange = rangeWalker.getRange();
			}
		}

		return this._type;
	}

	/**
	 * setType
	 * 
	 * @param node
	 */
	protected void setType(IParseNode node)
	{
		if (node instanceof JSNode && node.contains(this._offset))
		{
			((JSNode) node).accept(this);
		}
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	protected void setType(LocationType type)
	{
		this._type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArrayNode)
	 */
	@Override
	public void visit(JSArrayNode node)
	{
		if (node.contains(this._offset))
		{
			// TODO: Need to reconcile element-lists versus elision and need to
			// track left- and right-brackets
			this.setType(LocationType.IN_GLOBAL);
			this.visitChildren(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode)
	 */
	@Override
	public void visit(JSBinaryBooleanOperatorNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode lhs = node.getLeftHandSide();
			Symbol operator = node.getOperator();
			IParseNode rhs = node.getRightHandSide();

			if (lhs.contains(this._offset))
			{
				this.setType(lhs);
			}
			else if (operator != null && this._offset < operator.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (operator != null && this._offset < operator.getEnd())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < rhs.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (rhs.contains(this._offset))
			{
				this.setType(rhs);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBreakNode)
	 */
	@Override
	public void visit(JSBreakNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol label = node.getLabel();

			if (label != null && label.getStart() - 1 <= this._offset && this._offset <= label.getEnd())
			{
				this.setType(LocationType.IN_LABEL);
			}
			else if (this._offset == node.getEndingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCaseNode)
	 */
	@Override
	public void visit(JSCaseNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol colon = node.getColon();

			if (this._offset == colon.getEnd())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset > colon.getEnd())
			{
				this.setType(LocationType.IN_GLOBAL);

				for (int i = 1; i < node.getChildCount(); i++)
				{
					IParseNode child = node.getChild(i);

					if (child.contains(this._offset))
					{
						this.setType(child);
						break;
					}
				}
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSCatchNode)
	 */
	@Override
	public void visit(JSCatchNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode body = node.getBody();

			if (body instanceof JSNode && body.contains(this._offset))
			{
				((JSNode) body).accept(this);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConditionalNode)
	 */
	@Override
	public void visit(JSConditionalNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.IN_GLOBAL);

			// partition by operators
			Symbol questionMark = node.getQuestionMark();
			Symbol colon = node.getColon();

			if (this._offset < questionMark.getStart())
			{
				this.setType(LocationType.NONE);
				this.setType(node.getTestExpression());
			}
			else if (this._offset == questionMark.getStart())
			{ // $codepro.audit.disable emptyIfStatement
				// done
			}
			else if (this._offset < colon.getStart())
			{
				this.setType(node.getTrueExpression());
			}
			else if (this._offset == colon.getStart())
			{ // $codepro.audit.disable emptyIfStatement
				// done
			}
			else
			{
				this.setType(node.getFalseExpression());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSConstructNode)
	 */
	@Override
	public void visit(JSConstructNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode identifier = node.getExpression();
			IParseNode arguments = node.getArguments();

			if (this._offset < node.getStart() + 3)
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < identifier.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset <= identifier.getEndingOffset())
			{
				this.setType(identifier);
			}
			else if (this._offset <= arguments.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < arguments.getEndingOffset())
			{
				this.setType(arguments);
			}
			else if (this._offset == arguments.getEndingOffset())
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSContinueNode)
	 */
	@Override
	public void visit(JSContinueNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol label = node.getLabel();

			if (label != null && label.getStart() - 1 <= this._offset && this._offset <= label.getEnd())
			{
				this.setType(LocationType.IN_LABEL);
			}
			else if (this._offset == node.getEndingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDeclarationNode)
	 */
	@Override
	public void visit(JSDeclarationNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol equalSign = node.getEqualSign();

			if (equalSign != null)
			{
				IParseNode value = node.getValue();

				if (this._offset < equalSign.getStart())
				{
					if (node.getIdentifier().contains(_offset))
					{
						this.setType(LocationType.IN_VARIABLE_DECLARATION);
					}
				}
				else if (this._offset < value.getStartingOffset())
				{
					this.setType(LocationType.IN_GLOBAL);
				}
				else
				{
					this.setType(value);
				}
			}
			else
			{
				if (node.getIdentifier().contains(_offset))
				{
					this.setType(LocationType.IN_VARIABLE_DECLARATION);
				}
				else
				{
					this.setType(LocationType.NONE);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDefaultNode)
	 */
	@Override
	public void visit(JSDefaultNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol colon = node.getColon();

			if (this._offset == colon.getEnd())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset > colon.getEnd())
			{
				this.setType(LocationType.IN_GLOBAL);

				for (int i = 0; i < node.getChildCount(); i++)
				{
					IParseNode child = node.getChild(i);

					if (child.contains(this._offset))
					{
						this.setType(child);
						break;
					}
				}
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSDoNode)
	 */
	@Override
	public void visit(JSDoNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode body = node.getBody();
			Symbol lparen = node.getLeftParenthesis();
			IParseNode condition = node.getCondition();
			Symbol rparen = node.getRightParenthesis();

			if (this._offset < body.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (body.contains(this._offset) && this._offset != body.getEndingOffset())
			{
				this.setType(body);
			}
			else if (this._offset < lparen.getStart())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < condition.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (condition.contains(this._offset))
			{
				this.setType(condition);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset == rparen.getStart())
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSErrorNode)
	 */
	@Override
	public void visit(JSErrorNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFalseNode)
	 */
	@Override
	public void visit(JSFalseNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFinallyNode)
	 */
	@Override
	public void visit(JSFinallyNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);

			this.visitChildren(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForInNode)
	 */
	@Override
	public void visit(JSForInNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode initializer = node.getInitializer();
			Symbol in = node.getIn();
			IParseNode expression = node.getExpression();
			Symbol rightParen = node.getRightParenthesis();
			IParseNode body = node.getBody();

			if (this._offset < initializer.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (initializer.contains(this._offset))
			{
				this.setType(initializer);
			}
			else if (this._offset <= in.getEnd())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < expression.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else if (this._offset < rightParen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < body.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (body.contains(this._offset) && this._offset != body.getEndingOffset())
			{
				this.setType(body);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSForNode)
	 */
	@Override
	public void visit(JSForNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode initializer = node.getInitializer();
			Symbol semi1 = node.getSemicolon1();
			IParseNode condition = node.getCondition();
			Symbol semi2 = node.getSemicolon2();
			IParseNode advance = node.getAdvance();
			Symbol rparen = node.getRightParenthesis();
			IParseNode body = node.getBody();

			if (this._offset < initializer.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (initializer.contains(this._offset))
			{
				if (this._offset == initializer.getEndingOffset())
				{
					this.setType(LocationType.NONE);
				}
				else
				{
					this.setType(initializer);
				}
			}
			else if (this._offset < semi1.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < condition.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (condition.contains(this._offset))
			{
				if (this._offset == condition.getEndingOffset())
				{
					this.setType(LocationType.NONE);
				}
				else
				{
					this.setType(condition);
				}
			}
			else if (this._offset < semi2.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < advance.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (advance.contains(this._offset))
			{
				this.setType(advance);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < body.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (body.contains(this._offset) && this._offset != body.getEndingOffset())
			{
				this.setType(body);
			}
			else
			{
				this.setType(LocationType.IN_GLOBAL);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFunctionNode)
	 */
	@Override
	public void visit(JSFunctionNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode body = node.getBody();

			if (body.contains(this._offset))
			{
				this.setType(node.getBody());
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetElementNode)
	 */
	@Override
	public void visit(JSGetElementNode node)
	{
		this.visitChildren(node, node.getOperator());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGetPropertyNode)
	 */
	@Override
	public void visit(JSGetPropertyNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode lhs = node.getLeftHandSide();
			Symbol operator = node.getOperator();

			if (lhs.contains(this._offset) || lhs.getEndingOffset() <= this._offset
					&& this._offset < operator.getStart())
			{
				this.setType(lhs);
			}
			else
			{
				if (lhs instanceof JSThisNode)
				{
					this.setType(LocationType.IN_THIS);
				}
				else
				{
					this.setType(LocationType.IN_PROPERTY_NAME);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSGroupNode)
	 */
	@Override
	public void visit(JSGroupNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode expression = node.getExpression();
			Symbol rparen = node.getRightParenthesis();

			if (this._offset < expression.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.IN_VARIABLE_NAME);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIfNode)
	 */
	@Override
	public void visit(JSIfNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol lparen = node.getLeftParenthesis();
			IParseNode condition = node.getCondition();
			Symbol rparen = node.getRightParenthesis();
			IParseNode trueBlock = node.getTrueBlock();
			IParseNode falseBlock = node.getFalseBlock();

			if (this._offset < lparen.getStart())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < condition.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (condition.contains(this._offset))
			{
				this.setType(condition);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < trueBlock.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (trueBlock.contains(this._offset) && this._offset != trueBlock.getEndingOffset())
			{
				this.setType(trueBlock);
			}
			else if (trueBlock.getEndingOffset() == this._offset && falseBlock.isEmpty())
			{
				this.setType(trueBlock);
			}
			else if (this._offset < falseBlock.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (falseBlock.contains(this._offset))
			{
				this.setType(falseBlock);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	@Override
	public void visit(JSArgumentsNode node)
	{
		if (node.hasChildren())
		{
			for (IParseNode child : node)
			{
				if (child.contains(this._offset))
				{
					// If any child overlaps offset, use it's location type
					this.setType(child);
					return;
				}
			}
		}
		// otherwise assume in parameters
		this.setType(LocationType.IN_ARGUMENTS);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode expression = node.getExpression();
			IParseNode arguments = node.getArguments();

			if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else if (this._offset < arguments.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (arguments.contains(this._offset) && this._offset != arguments.getEndingOffset())
			{
				this.setType(arguments);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSLabelledNode)
	 */
	@Override
	public void visit(JSLabelledNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol colon = node.getColon();
			IParseNode block = node.getBlock();

			if (this._offset < colon.getStart())
			{
				this.setType(LocationType.IN_LABEL);
			}
			else if (this._offset < block.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (block.contains(this._offset))
			{
				this.setType(block);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNameValuePairNode)
	 */
	@Override
	public void visit(JSNameValuePairNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol colon = node.getColon();
			IParseNode value = node.getValue();

			if (this._offset < colon.getStart())
			{
				this.setType(LocationType.IN_OBJECT_LITERAL_PROPERTY);
			}
			else if (this._offset < value.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (value.contains(this._offset))
			{
				this.setType(value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNullNode)
	 */
	@Override
	public void visit(JSNullNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNumberNode)
	 */
	@Override
	public void visit(JSNumberNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSObjectNode)
	 */
	@Override
	public void visit(JSObjectNode node)
	{
		if (node.contains(this._offset) && node.getEndingOffset() != this._offset)
		{
			this.setType(LocationType.IN_OBJECT_LITERAL_PROPERTY);
			this.visitChildren(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSParametersNode)
	 */
	@Override
	public void visit(JSParametersNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSParseRootNode)
	 */
	@Override
	public void visit(JSParseRootNode node)
	{
		this.setType(LocationType.IN_GLOBAL);

		if (node.contains(this._offset) && node.hasChildren())
		{
			for (IParseNode child : node)
			{
				if (child.contains(this._offset))
				{
					this.setType(child);

					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPostUnaryOperatorNode)
	 */
	@Override
	public void visit(JSPostUnaryOperatorNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode expression = node.getExpression();
			Symbol operator = node.getOperator();

			if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else if (this._offset < operator.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSPreUnaryOperatorNode)
	 */
	@Override
	public void visit(JSPreUnaryOperatorNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode expression = node.getExpression();
			Symbol operator = node.getOperator();

			if (this._offset < operator.getEnd())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < expression.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else
			{
				this.setType(LocationType.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
	 */
	@Override
	public void visit(JSRegexNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSReturnNode)
	 */
	@Override
	public void visit(JSReturnNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode expression = node.getExpression();

			if (this._offset < expression.getStartingOffset())
			{
				if (this._offset + 1 == expression.getStartingOffset())
				{
					this.setType(LocationType.IN_GLOBAL);
				}
				else
				{
					this.setType(LocationType.NONE);
				}
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStatementsNode)
	 */
	@Override
	public void visit(JSStatementsNode node)
	{
		if (node.contains(this._offset) && node.getEndingOffset() != this._offset)
		{
			this.setType(LocationType.IN_GLOBAL);

			this.visitChildren(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSSwitchNode)
	 */
	@Override
	public void visit(JSSwitchNode node)
	{
		if (node.contains(this._offset) && this._offset != node.getEndingOffset())
		{
			Symbol lparen = node.getLeftParenthesis();
			IParseNode expression = node.getExpression();
			Symbol rparen = node.getRightParenthesis();
			Symbol lcurly = node.getLeftBrace();
			IParseNode firstStatement = node.getChild(1);
			IParseNode lastStatement = node.getLastChild();

			if (this._offset < lparen.getStart())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < expression.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < lcurly.getStart())
			{
				this.setType(LocationType.NONE);
			}
			else if (firstStatement != null && this._offset < firstStatement.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (lastStatement != null && lastStatement.getEndingOffset() < this._offset)
			{
				this.setType(LocationType.NONE);
			}
			else
			{
				this.setType(LocationType.IN_GLOBAL);

				for (int i = 1; i < node.getChildCount(); i++)
				{
					IParseNode child = node.getChild(i);

					if (child.contains(this._offset))
					{
						this.setType(child);
						break;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSThisNode)
	 */
	@Override
	public void visit(JSThisNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSThrowNode)
	 */
	@Override
	public void visit(JSThrowNode node)
	{
		if (node.contains(this._offset))
		{
			IParseNode expression = node.getExpression();

			if (this._offset < expression.getStartingOffset())
			{
				if (this._offset + 1 == expression.getStartingOffset())
				{
					this.setType(LocationType.IN_GLOBAL);
				}
				else
				{
					this.setType(LocationType.NONE);
				}
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		if (node.contains(this._offset))
		{
			this.setType(LocationType.NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTryNode)
	 */
	@Override
	public void visit(JSTryNode node)
	{
		if (node.contains(this._offset) && node.getEndingOffset() != this._offset)
		{
			this.setType(LocationType.NONE);

			this.visitChildren(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSVarNode)
	 */
	@Override
	public void visit(JSVarNode node)
	{
		// @formatter:off
		if (node.contains(this._offset) && (this._offset != node.getEndingOffset() || !node.getSemicolonIncluded()))
		// @formatter:on
		{
			IParseNode firstDeclaration = node.getFirstChild();

			if (this._offset < firstDeclaration.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else
			{
				this.setType(LocationType.NONE);
				this.visitChildren(node);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWhileNode)
	 */
	@Override
	public void visit(JSWhileNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol lparen = node.getLeftParenthesis();
			IParseNode condition = node.getCondition();
			Symbol rparen = node.getRightParenthesis();
			IParseNode body = node.getBody();

			if (this._offset < lparen.getStart())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < condition.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (condition.contains(this._offset))
			{
				this.setType(condition);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < body.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (body.contains(this._offset))
			{
				this.setType(body);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSWithNode)
	 */
	@Override
	public void visit(JSWithNode node)
	{
		if (node.contains(this._offset))
		{
			Symbol lparen = node.getLeftParenthesis();
			IParseNode expression = node.getExpression();
			Symbol rparen = node.getRightParenthesis();
			IParseNode body = node.getBody();

			if (this._offset < lparen.getStart())
			{
				this.setType(LocationType.NONE);
			}
			else if (this._offset < expression.getStartingOffset())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (expression.contains(this._offset))
			{
				this.setType(expression);
			}
			else if (this._offset < rparen.getStart())
			{
				this.setType(LocationType.IN_GLOBAL);
			}
			else if (this._offset < body.getStartingOffset())
			{
				this.setType(LocationType.NONE);
			}
			else if (body.contains(this._offset))
			{
				this.setType(body);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visitChildren(com.aptana.editor.js.parsing.ast.JSNode)
	 */
	@Override
	protected void visitChildren(JSNode node)
	{
		if (node.contains(this._offset) && node.hasChildren())
		{
			for (IParseNode child : node)
			{
				if (child.contains(this._offset))
				{
					this.setType(child);

					break;
				}
			}
		}
	}

	/**
	 * visitChildren
	 * 
	 * @param node
	 * @param operator
	 */
	protected void visitChildren(JSNode node, Symbol operator)
	{
		this.visitChildren(node);

		if (this._type == LocationType.UNKNOWN)
		{
			if (operator != null)
			{
				if (operator.getStart() == this._offset + 1 || operator.getEnd() <= this._offset)
				{
					this.setType(LocationType.IN_GLOBAL);
				}
				else
				{
					this.setType(LocationType.NONE);
				}
			}
		}
	}
}

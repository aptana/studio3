package com.aptana.editor.js.contentassist;

import beaver.Symbol;

import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetElementNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNullNode;
import com.aptana.editor.js.parsing.ast.JSNumberNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSRegexNode;
import com.aptana.editor.js.parsing.ast.JSStatementsNode;
import com.aptana.editor.js.parsing.ast.JSThisNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSTrueNode;
import com.aptana.parsing.ast.IParseNode;

public class JSLocationWalker extends JSTreeWalker
{
	private int _offset;
	private LocationType _type;

	/**
	 * JSLocationWalker
	 */
	public JSLocationWalker(int offset)
	{
		this._offset = offset;
		this._type = LocationType.UNKNOWN;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public LocationType getType()
	{
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
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSArgumentsNode)
	 */
	@Override
	public void visit(JSArgumentsNode node)
	{
		this.visitChildren(node);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSAssignmentNode)
	 */
	@Override
	public void visit(JSAssignmentNode node)
	{
		this.visitChildren(node, node.getOperator());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBinaryArithmeticOperatorNode
	 * )
	 */
	@Override
	public void visit(JSBinaryArithmeticOperatorNode node)
	{
		this.visitChildren(node, node.getOperator());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode)
	 */
	@Override
	public void visit(JSBinaryBooleanOperatorNode node)
	{
		this.visitChildren(node, node.getOperator());
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

			if (lhs.contains(this._offset) || lhs.getEndingOffset() <= this._offset && this._offset < operator.getStart())
			{
				this.setType(lhs);
			}
			else
			{
				this.setType(LocationType.IN_PROPERTY_NAME);
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
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSInvokeNode)
	 */
	@Override
	public void visit(JSInvokeNode node)
	{
		this.visitChildren(node);
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
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSParseRootNode)
	 */
	@Override
	public void visit(JSParseRootNode node)
	{
		this.setType(LocationType.IN_GLOBAL);
		
		this._offset--;
		
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
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStatementsNode)
	 */
	@Override
	public void visit(JSStatementsNode node)
	{
		this.visitChildren(node);
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

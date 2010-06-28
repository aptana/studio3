package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.ast.IParseNode;

public class JSBinaryOperatorNode extends JSNode
{
	private Symbol _operator;

	/**
	 * JSBinaryOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	protected JSBinaryOperatorNode(JSNode left, JSNode right)
	{
		this.start = left.getStart();
		this.end = right.getEnd();

		setChildren(new JSNode[] { left, right });
	}

	/**
	 * JSBinaryOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	public JSBinaryOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		this(left, right);

		this._operator = operator;

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get((String) operator.value);
		switch (token)
		{
			case EQUAL_EQUAL:
				type = JSNodeTypes.EQUAL;
				break;
			case GREATER:
				type = JSNodeTypes.GREATER_THAN;
				break;
			case GREATER_EQUAL:
				type = JSNodeTypes.GREATER_THAN_OR_EQUAL;
				break;
			case EQUAL_EQUAL_EQUAL:
				type = JSNodeTypes.IDENTITY;
				break;
			case IN:
				type = JSNodeTypes.IN;
				break;
			case INSTANCEOF:
				type = JSNodeTypes.INSTANCE_OF;
				break;
			case LBRACKET:
				type = JSNodeTypes.GET_ELEMENT;
				break;
			case LESS:
				type = JSNodeTypes.LESS_THAN;
				break;
			case LESS_EQUAL:
				type = JSNodeTypes.LESS_THAN_OR_EQUAL;
				break;
			case AMPERSAND_AMPERSAND:
				type = JSNodeTypes.LOGICAL_AND;
				break;
			case PIPE_PIPE:
				type = JSNodeTypes.LOGICAL_OR;
				break;
			case EXCLAMATION_EQUAL:
				type = JSNodeTypes.NOT_EQUAL;
				break;
			case EXCLAMATION_EQUAL_EQUAL:
				type = JSNodeTypes.NOT_IDENTITY;
				break;
			case PLUS:
				type = JSNodeTypes.ADD;
				break;
			case GREATER_GREATER_GREATER:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT;
				break;
			case AMPERSAND:
				type = JSNodeTypes.BITWISE_AND;
				break;
			case PIPE:
				type = JSNodeTypes.BITWISE_OR;
				break;
			case CARET:
				type = JSNodeTypes.BITWISE_XOR;
				break;
			case FORWARD_SLASH:
				type = JSNodeTypes.DIVIDE;
				break;
			case PERCENT:
				type = JSNodeTypes.MOD;
				break;
			case STAR:
				type = JSNodeTypes.MULTIPLY;
				break;
			case LESS_LESS:
				type = JSNodeTypes.SHIFT_LEFT;
				break;
			case GREATER_GREATER:
				type = JSNodeTypes.SHIFT_RIGHT;
				break;
			case MINUS:
				type = JSNodeTypes.SUBTRACT;
			case DOT:
				type = JSNodeTypes.GET_PROPERTY;
				break;
		}
		setType(type);
	}

	/**
	 * getLeftHandSide
	 * 
	 * @return
	 */
	public IParseNode getLeftHandSide()
	{
		return this.getChild(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#getLocationType(int)
	 */
	@Override
	LocationType getLocationType(int offset)
	{
		LocationType result = LocationType.UNKNOWN;

		if (this.contains(offset))
		{
			for (IParseNode child : this)
			{
				if (child.contains(offset))
				{
					if (child instanceof JSNode)
					{
						result = ((JSNode) child).getLocationType(offset);
					}

					break;
				}
			}

			if (result == LocationType.UNKNOWN && this._operator != null)
			{
				if (this._operator.getStart() == offset + 1 || this._operator.getEnd() <= offset)
				{
					result = LocationType.IN_GLOBAL;
				}
				else if (offset <= this._operator.getStart())
				{
					result = LocationType.NONE;
				}
			}
		}

		return result;
	}

	/**
	 * getOperator
	 * 
	 * @return
	 */
	protected Symbol getOperator()
	{
		return this._operator;
	}

	/**
	 * getRightHandSide
	 * 
	 * @return
	 */
	public IParseNode getRightHandSide()
	{
		return this.getChild(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append(this.getLeftHandSide());
		text.append(" ").append(this._operator.value).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append(this.getRightHandSide());

		this.appendSemicolon(text);

		return text.toString();
	}
}

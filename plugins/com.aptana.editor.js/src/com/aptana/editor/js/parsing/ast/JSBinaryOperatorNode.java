package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.parsing.ast.IParseNode;

public abstract class JSBinaryOperatorNode extends JSNode
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
	protected JSBinaryOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		this(left, right);

		this._operator = operator;
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

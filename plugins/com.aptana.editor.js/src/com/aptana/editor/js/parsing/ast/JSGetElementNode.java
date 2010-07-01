package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSGetElementNode extends JSBinaryOperatorNode
{
	private Symbol _rightBracket;

	/**
	 * JSGetElementOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	public JSGetElementNode(JSNode left, Symbol leftBracket, JSNode right, Symbol rightBracket)
	{
		super(left, leftBracket, right);
		
		this._rightBracket = rightBracket;
		this.setType(JSNodeTypes.GET_ELEMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
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

			Symbol leftBracket = this.getOperator();

			if (result == LocationType.UNKNOWN)
			{
				if (leftBracket.getStart() == offset + 1 || leftBracket.getEnd() <= offset || this._rightBracket.getEnd() <= offset)
				{
					result = LocationType.IN_GLOBAL;
				}
				else if (offset <= leftBracket.getStart())
				{
					result = LocationType.NONE;
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append(this.getLeftHandSide());
		text.append("[");
		text.append(this.getRightHandSide());
		text.append("]"); //$NON-NLS-1$ //$NON-NLS-2$

		this.appendSemicolon(text);

		return text.toString();
	}
}

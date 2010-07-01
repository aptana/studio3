package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.parsing.ast.IParseNode;

public class JSStatementsNode extends JSNaryNode
{
	/**
	 * JSStatementsNode
	 * 
	 * @param children
	 */
	public JSStatementsNode(JSNode... children)
	{
		super(JSNodeTypes.STATEMENTS, children);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendCloseText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendCloseText(StringBuilder buffer)
	{
		buffer.append("}"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("{"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#getDelimiter()
	 */
	@Override
	protected String getDelimiter()
	{
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#getLocationType(int)
	 */
	LocationType getLocationType(int offset)
	{
		LocationType result = LocationType.UNKNOWN;

		if (this.contains(offset))
		{
			result = LocationType.IN_GLOBAL;

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
		}

		return result;
	}
}

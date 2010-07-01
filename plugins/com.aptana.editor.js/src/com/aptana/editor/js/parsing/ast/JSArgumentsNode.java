package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.parsing.ast.IParseNode;

public class JSArgumentsNode extends JSNaryNode
{
	/**
	 * JSArgumentsNode
	 * 
	 * @param children
	 */
	public JSArgumentsNode(JSNode... children)
	{
		super(JSNodeTypes.ARGUMENTS, children);
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
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendCloseText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendCloseText(StringBuilder buffer)
	{
		buffer.append(")"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNaryNode#appendOpenText(java.lang.StringBuilder)
	 */
	@Override
	protected void appendOpenText(StringBuilder buffer)
	{
		buffer.append("("); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#getLocationType(int)
	 */
	@Override
	LocationType getLocationType(int offset)
	{
		LocationType result = LocationType.IN_GLOBAL;

		if (this.contains(offset) && this.hasChildren())
		{
			for (IParseNode child : this)
			{
				if (child.contains(offset))
				{
					if (child instanceof JSNode)
					{
						result = ((JSNode) child).getLocationType(offset);
					}
					else
					{
						result = LocationType.UNKNOWN;
					}

					break;
				}
			}
		}

		return result;
	}
}

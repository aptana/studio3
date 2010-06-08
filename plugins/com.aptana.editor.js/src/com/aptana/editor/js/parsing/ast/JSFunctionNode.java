package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseBaseNode;
import com.aptana.parsing.ast.ParseNodeAttribute;

public class JSFunctionNode extends JSNode
{
	/**
	 * JSFunctionNode
	 * 
	 * @param children
	 * @param start
	 * @param end
	 */
	public JSFunctionNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.FUNCTION, start, end, children);
	}

	/**
	 * getArgs
	 * 
	 * @return
	 */
	public IParseNode[] getArgs()
	{
		IParseNode[] result = NO_CHILDREN;
		IParseNode argsNode = this.getChild(1);

		if (argsNode != null && argsNode.getType() == JSNodeTypes.PARAMETERS)
		{
			result = argsNode.getChildren();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getAttributes()
	 */
	public IParseNodeAttribute[] getAttributes()
	{
		String name = getName();

		if (name != null && name.length() > 0)
		{
			// TODO: possibly cache this
			return new IParseNodeAttribute[] { new ParseNodeAttribute(this, "name", name) //$NON-NLS-1$
			};
		}
		else
		{
			return ParseBaseNode.NO_ATTRIBUTES;
		}
	}

	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(2);
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return getChild(0).getText();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getText()
	 */
	@Override
	public String getText()
	{
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("function "); //$NON-NLS-1$
		String name = getName();
		if (name.length() > 0)
		{
			text.append(name).append(" "); //$NON-NLS-1$
		}
		text.append(getChild(1)).append(" ").append(getChild(2)); //$NON-NLS-1$
		return appendSemicolon(text.toString());
	}
}

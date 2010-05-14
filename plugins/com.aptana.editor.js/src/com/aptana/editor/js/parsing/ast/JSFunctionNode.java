package com.aptana.editor.js.parsing.ast;

import java.util.LinkedList;
import java.util.List;

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
	public JSFunctionNode(JSNode[] children, int start, int end)
	{
		super(JSNodeTypes.FUNCTION, children, start, end);
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
			return new IParseNodeAttribute[] {
				new ParseNodeAttribute(this, "name", name)
			};
		}
		else
		{
			return ParseBaseNode.NO_ATTRIBUTES;
		}
	}
	
	/**
	 * getArgNames
	 * 
	 * @return
	 */
	public String[] getArgNames()
	{
		List<String> result = new LinkedList<String>();
		IParseNode argsNode = this.getChild(1);
		
		if (argsNode != null && argsNode.getType() == JSNodeTypes.PARAMETERS)
		{
			for (IParseNode arg : argsNode)
			{
				if (arg.getType() == JSNodeTypes.IDENTIFIER)
				{
					result.add(arg.toString());
				}
			}
		}
		
		return result.toArray(new String[result.size()]);
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

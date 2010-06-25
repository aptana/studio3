package com.aptana.editor.js.parsing.ast;

import java.util.List;

import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class JSConstructNode extends JSNode
{
	/**
	 * JSConstructNode
	 * 
	 * @param start
	 * @param end
	 * @param children
	 */
	public JSConstructNode(int start, int end, JSNode... children)
	{
		super(JSNodeTypes.CONSTRUCT, start, end, children);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#addTypes(java.util.List, com.aptana.parsing.Scope)
	 */
	@Override
	protected void addTypes(List<String> types, Scope<JSNode> scope)
	{
		IParseNode child = this.getChild(0);
		
		// TEMP: for debugging
		String name = child.getText();
		List<JSNode> nodes = scope.getSymbol(name);
		
		for (JSNode node : nodes)
		{
			if (node instanceof JSFunctionNode)
			{
				List<String> returnTypes = ((JSFunctionNode) node).getReturnTypes();
				
				types.addAll(returnTypes);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		IParseNode[] children = getChildren();

		buffer.append("new "); //$NON-NLS-1$
		buffer.append(children[0]);
		buffer.append(children[1]);

		this.appendSemicolon(buffer);

		return buffer.toString();
	}
}

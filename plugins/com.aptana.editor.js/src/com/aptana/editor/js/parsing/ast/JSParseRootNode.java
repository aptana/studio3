package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.ast.ParseRootNode;

public class JSParseRootNode extends ParseRootNode
{
	/**
	 * JSParseRootNode
	 */
	public JSParseRootNode()
	{
		this(new Symbol[0]);
	}

	/**
	 * JSParseRootNode
	 * 
	 * @param children
	 * @param start
	 * @param end
	 */
	public JSParseRootNode(Symbol[] children)
	{
		super(IJSParserConstants.LANGUAGE, children, (children != null && children.length > 0) ? children[0].getStart() : 0,
			(children != null && children.length > 0) ? children[0].getEnd() : 0);
	}

	/**
	 * accept
	 * 
	 * @param walker
	 */
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	public String toString()
	{
		JSFormatWalker walker = new JSFormatWalker();
		
		this.accept(walker);
		
		return walker.getText();
	}
}

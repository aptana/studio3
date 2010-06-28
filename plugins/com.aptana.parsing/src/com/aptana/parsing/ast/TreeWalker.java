package com.aptana.parsing.ast;

public class TreeWalker implements ITreeWalker
{
	/* (non-Javadoc)
	 * @see com.aptana.parsing.ast.ITreeWalker#visit(com.aptana.parsing.ast.IParseNode)
	 */
	@Override
	public void visit(IParseNode node)
	{
		node.accept(this);
	}

	/**
	 * visitChildren
	 * 
	 * @param node
	 */
	protected void visitChildren(IParseNode node)
	{
		for (IParseNode child : node)
		{
			child.accept(this);
		}
	}
}

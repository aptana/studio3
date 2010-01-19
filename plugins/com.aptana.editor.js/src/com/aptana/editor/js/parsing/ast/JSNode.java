package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.ParseBaseNode;

public class JSNode extends ParseBaseNode
{
	private boolean fSemicolonIncluded;

	public boolean getSemicolonIncluded()
	{
		return fSemicolonIncluded;
	}

	public void setSemicolonIncluded(boolean included)
	{
		fSemicolonIncluded = included;
	}
}

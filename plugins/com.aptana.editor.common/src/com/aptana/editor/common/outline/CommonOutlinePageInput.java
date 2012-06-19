package com.aptana.editor.common.outline;

import com.aptana.parsing.ast.IParseRootNode;

public class CommonOutlinePageInput
{
	public IParseRootNode ast;

	public CommonOutlinePageInput(IParseRootNode ast)
	{
		this.ast = ast;
	}
}
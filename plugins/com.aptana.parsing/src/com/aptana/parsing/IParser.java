package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;

public interface IParser
{
	/**
	 * Parse the content contained within the specified parse state
	 * 
	 * @param parseState
	 * @return
	 * @throws Exception
	 */
	public IParseNode parse(IParseState parseState) throws Exception;
}

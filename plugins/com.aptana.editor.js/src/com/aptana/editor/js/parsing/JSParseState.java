package com.aptana.editor.js.parsing;

import java.util.List;

import com.aptana.editor.js.sdoc.model.Block;
import com.aptana.parsing.ParseState;

public class JSParseState extends ParseState
{
	private List<Block> _documentationBlocks;

	/**
	 * getDocumentationBlocks
	 * 
	 * @return
	 */
	public List<Block> getDocumentationBlocks()
	{
		return this._documentationBlocks;
	}

	/**
	 * setDocumentationBlocks
	 * 
	 * @param blocks
	 */
	public void setDocumentationBlocks(List<Block> blocks)
	{
		this._documentationBlocks = blocks;
	}
}

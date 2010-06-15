package com.aptana.editor.js.parsing;

import java.util.List;

import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.sdoc.model.Block;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.Scope;

public class JSParseState extends ParseState
{
	private Scope<JSNode> _globalScope;
	private List<Block> _preBlocks;
	private List<Block> _postBlocks;

	/**
	 * findBlock
	 * 
	 * @param offset
	 * @return
	 */
	public Block findBlock(int offset)
	{
		Block block = this.findPreBlock(offset);
		
		if (block == null)
		{
			block = this.findPostBlock(offset);
		}
		
		return block;
	}
	
	/**
	 * Finds the comment block ending or just before the given offset
	 * 
	 * @param offset
	 * @return
	 */
	public Block findPreBlock(int offset)
	{
		Block result = null;

		if (this._preBlocks != null)
		{
			int index = this.getBlockIndex(this._preBlocks, offset);

			if (index < 0)
			{
				index = -index - 1;
			}

			if (index < this._preBlocks.size())
			{
				result = this._preBlocks.get(index);
			}
		}

		return result;
	}

	/**
	 * Finds the comment block ending or just before the given offset
	 * 
	 * @param offset
	 * @return
	 */
	public Block findPostBlock(int offset)
	{
		Block result = null;

		if (this._postBlocks != null)
		{
			int index = this.getBlockIndex(this._postBlocks, offset);

			if (index < 0)
			{
				index = -index - 1 + 1;
			}

			if (index < this._postBlocks.size())
			{
				result = this._postBlocks.get(index);
			}
		}

		return result;
	}

	/**
	 * getBlockIndex
	 * 
	 * @param list
	 * @param offset
	 * @return
	 */
	protected int getBlockIndex(List<Block> list, int offset)
	{
		int low = 0;
		int high = list.size() - 1;

		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			Block candidate = list.get(mid);

			if (offset < candidate.getStart())
			{
				high = mid - 1;
			}
			else if (candidate.getEnd() < offset)
			{
				low = mid + 1;
			}
			else
			{
				return mid;
			}
		}

		return -(low + 1);
	}

	/**
	 * getGlobalScope
	 * 
	 * @return
	 */
	public Scope<JSNode> getGlobalScope()
	{
		return this._globalScope;
	}

	/**
	 * getPreDocumentationBlocks
	 * 
	 * @return
	 */
	public List<Block> getPreDocumentationBlocks()
	{
		return this._preBlocks;
	}

	/**
	 * getPostDocumentationBlocks
	 * 
	 * @return
	 */
	public List<Block> getPostDocumentationBlocks()
	{
		return this._postBlocks;
	}

	/**
	 * setGlobalScope
	 * 
	 * @param globalScope
	 */
	public void setGlobalScope(Scope<JSNode> globalScope)
	{
		this._globalScope = globalScope;
	}

	/**
	 * setPreDocumentationBlocks
	 * 
	 * @param blocks
	 */
	public void setPreDocumentationBlocks(List<Block> blocks)
	{
		this._preBlocks = blocks;
	}

	/**
	 * setPostDocumentationBlocks
	 * 
	 * @param blocks
	 */
	public void setPostDocumentationBlocks(List<Block> blocks)
	{
		this._postBlocks = blocks;
	}
}

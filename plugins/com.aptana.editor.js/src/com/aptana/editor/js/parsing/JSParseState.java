package com.aptana.editor.js.parsing;

import java.util.List;

import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.sdoc.model.Block;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.Scope;

public class JSParseState extends ParseState
{
	private Scope<JSNode> _globalScope;
	private List<Block> _sdocBlocks;
	private List<Block> _vsdocBlocks;

	/**
	 * Finds the comment block ending or just before the given offset
	 * 
	 * @param offset
	 * @return
	 */
	public Block findSDocBlock(int offset)
	{
		Block result = null;

		if (this._sdocBlocks != null)
		{
			int index = this.getBlockIndex(this._sdocBlocks, offset);

			if (index < 0)
			{
				index = -index - 1;
			}

			if (index < this._sdocBlocks.size())
			{
				result = this._sdocBlocks.get(index);
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
	public Block findVSDocBlock(int offset)
	{
		Block result = null;

		if (this._vsdocBlocks != null)
		{
			int index = this.getBlockIndex(this._vsdocBlocks, offset);

			if (index < 0)
			{
				index = -index - 1 + 1;
			}

			if (index < this._vsdocBlocks.size())
			{
				result = this._vsdocBlocks.get(index);
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
	 * getSDocBlocks
	 * 
	 * @return
	 */
	public List<Block> getSDocBlocks()
	{
		return this._sdocBlocks;
	}

	/**
	 * getVSDocBlocks
	 * 
	 * @return
	 */
	public List<Block> getVSDocBlocks()
	{
		return this._vsdocBlocks;
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
	 * setSDocBlocks
	 * 
	 * @param blocks
	 */
	public void setSDocBlocks(List<Block> blocks)
	{
		this._sdocBlocks = blocks;
	}

	/**
	 * setVSDocBlocks
	 * 
	 * @param blocks
	 */
	public void setVSDocBlocks(List<Block> blocks)
	{
		this._vsdocBlocks = blocks;
	}
}

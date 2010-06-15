package com.aptana.editor.js.parsing.ast;

import java.util.List;

import beaver.Symbol;

import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.ParseRootNode;

public class JSParseRootNode extends ParseRootNode
{
	private Scope<JSNode> _globalScope;
	private List<DocumentationBlock> _preBlocks;
	private List<DocumentationBlock> _postBlocks;

	/**
	 * JSParseRootNode
	 */
	public JSParseRootNode()
	{
		this(new Symbol[0], 0, 0);
	}

	/**
	 * JSParseRootNode
	 * 
	 * @param children
	 * @param start
	 * @param end
	 */
	public JSParseRootNode(Symbol[] children, int start, int end)
	{
		super(IJSParserConstants.LANGUAGE, children, start, end);
	}

	/**
	 * findDocumentationBlock
	 * 
	 * @param offset
	 * @return
	 */
	public DocumentationBlock findDocumentationBlock(int offset)
	{
		DocumentationBlock block = this.findPreDocumentationBlock(offset);

		if (block == null)
		{
			block = this.findPostDocumentationBlock(offset);
		}

		return block;
	}

	/**
	 * Finds the comment block ending or just before the given offset
	 * 
	 * @param offset
	 * @return
	 */
	public DocumentationBlock findPostDocumentationBlock(int offset)
	{
		DocumentationBlock result = null;

		if (this._postBlocks != null)
		{
			int index = this.getDocumentationBlockIndex(this._postBlocks, offset);

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
	 * Finds the comment block ending or just before the given offset
	 * 
	 * @param offset
	 * @return
	 */
	public DocumentationBlock findPreDocumentationBlock(int offset)
	{
		DocumentationBlock result = null;

		if (this._preBlocks != null)
		{
			int index = this.getDocumentationBlockIndex(this._preBlocks, offset);

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
	 * getDocumentationBlockIndex
	 * 
	 * @param list
	 * @param offset
	 * @return
	 */
	protected int getDocumentationBlockIndex(List<DocumentationBlock> list, int offset)
	{
		int low = 0;
		int high = list.size() - 1;

		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			DocumentationBlock candidate = list.get(mid);

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
	 * getPostDocumentationBlocks
	 * 
	 * @return
	 */
	public List<DocumentationBlock> getPostDocumentationBlocks()
	{
		return this._postBlocks;
	}

	/**
	 * getPreDocumentationBlocks
	 * 
	 * @return
	 */
	public List<DocumentationBlock> getPreDocumentationBlocks()
	{
		return this._preBlocks;
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
	 * setPostDocumentationBlocks
	 * 
	 * @param blocks
	 */
	public void setPostDocumentationBlocks(List<DocumentationBlock> blocks)
	{
		this._postBlocks = blocks;
	}

	/**
	 * setPreDocumentationBlocks
	 * 
	 * @param blocks
	 */
	public void setPreDocumentationBlocks(List<DocumentationBlock> blocks)
	{
		this._preBlocks = blocks;
	}
}

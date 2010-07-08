package com.aptana.editor.js.contentassist;

import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNullNode;
import com.aptana.editor.js.parsing.ast.JSNumberNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSRegexNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.editor.js.parsing.ast.JSThisNode;
import com.aptana.editor.js.parsing.ast.JSTreeWalker;
import com.aptana.editor.js.parsing.ast.JSTrueNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class JSRangeWalker extends JSTreeWalker
{
	private int _offset;
	private IRange _range;

	/**
	 * JSRangeWalker
	 * 
	 * @param offset
	 */
	public JSRangeWalker(int offset)
	{
		this._offset = offset - 1;
		this._range = new Range(offset, offset - 1);
	}

	/**
	 * getRange
	 * 
	 * @return
	 */
	public IRange getRange()
	{
		return this._range;
	}
	
	/**
	 * setRange
	 * 
	 * @param startingOffset
	 * @param endingOffset
	 */
	public void setRange(int startingOffset, int endingOffset)
	{
		this._range = new Range(startingOffset, endingOffset);
	}
	
	/**
	 * setRange
	 * 
	 * @param node
	 */
	public void setRange(IParseNode node)
	{
		if (node instanceof JSNode)
		{
			((JSNode) node).accept(this);
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSFalseNode)
	 */
	@Override
	public void visit(JSFalseNode node)
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSIdentifierNode)
	 */
	@Override
	public void visit(JSIdentifierNode node)
	{
		if (node.contains(this._offset))
		{
			this.setRange(node.getStart(), this._offset);
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNullNode)
	 */
	@Override
	public void visit(JSNullNode node)
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSNumberNode)
	 */
	@Override
	public void visit(JSNumberNode node)
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSParseRootNode)
	 */
	@Override
	public void visit(JSParseRootNode node)
	{
		if (node.contains(this._offset))
		{
			for (IParseNode child : node)
			{
				if (child.contains(this._offset))
				{
					this.setRange(child);
					break;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSRegexNode)
	 */
	@Override
	public void visit(JSRegexNode node)
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSStringNode)
	 */
	@Override
	public void visit(JSStringNode node)
	{
		if (node.contains(this._offset))
		{
			this.setRange(node.getStart() + 1, this._offset - 1);
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSThisNode)
	 */
	@Override
	public void visit(JSThisNode node)
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visit(com.aptana.editor.js.parsing.ast.JSTrueNode)
	 */
	@Override
	public void visit(JSTrueNode node)
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSTreeWalker#visitChildren(com.aptana.editor.js.parsing.ast.JSNode)
	 */
	@Override
	protected void visitChildren(JSNode node)
	{
		if (node.contains(this._offset))
		{
			for (IParseNode child : node)
			{
				if (child.contains(this._offset))
				{
					this.setRange(child);
					break;
				}
			}
		}
	}
}

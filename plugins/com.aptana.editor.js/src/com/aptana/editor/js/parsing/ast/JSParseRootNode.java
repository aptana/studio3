package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSParseRootNode extends ParseRootNode
{
	private Scope<JSNode> _globalScope;

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
		super(
			IJSParserConstants.LANGUAGE,
			children,
			(children != null && children.length > 0) ? children[0].getStart() : 0,
			(children != null && children.length > 0) ? children[0].getEnd() : 0
		);
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
	 * getLocationType
	 * 
	 * @param offset
	 * @return
	 */
	public LocationType getLocationType(int offset)
	{
		LocationType result = LocationType.IN_GLOBAL;

		// shift offset to the left by one to avoid special casing starting and
		// ending conditions
		offset -= 1;

		if (this.contains(offset) && this.hasChildren())
		{
			for (IParseNode child : this)
			{
				if (child.contains(offset))
				{
					if (child instanceof JSNode)
					{
						result = ((JSNode) child).getLocationType(offset);
					}
					else
					{
						result = LocationType.UNKNOWN;
					}

					break;
				}
			}
		}

		return result;
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
}

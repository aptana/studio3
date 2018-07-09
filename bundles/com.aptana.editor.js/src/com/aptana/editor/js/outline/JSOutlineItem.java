/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.js.core.parsing.ast.JSFunctionNode;
import com.aptana.js.core.parsing.ast.JSGetterNode;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class JSOutlineItem extends CommonOutlineItem
{

	public static enum Type
	{
		PROPERTY, ARRAY, BOOLEAN, FUNCTION, NULL, NUMBER, OBJECT_LITERAL, REGEX, STRING, CLASS
	}

	private String fLabel;
	private Type fType;
	private int fChildrenCount;

	private List<IParseNode> fVirtualChildren;

	public JSOutlineItem(String label, Type type, IRange sourceRange, IParseNode referenceNode)
	{
		this(label, type, sourceRange, referenceNode, 0);
	}

	public JSOutlineItem(String label, Type type, IRange sourceRange, IParseNode referenceNode, int childrenCount)
	{
		super(sourceRange, referenceNode);
		fLabel = label;
		fType = type;
		fChildrenCount = childrenCount;
	}

	public void addVirtualChild(IParseNode target)
	{
		if (fVirtualChildren == null)
		{
			fVirtualChildren = new ArrayList<IParseNode>();
		}
		fVirtualChildren.add(target);
	}

	public int getChildrenCount()
	{
		if (hasVirtualChildren())
		{
			return fChildrenCount + fVirtualChildren.size();
		}
		return fChildrenCount;
	}

	public String getLabel()
	{
		return fLabel;
	}

	public IParseNode[] getAllReferenceNodes()
	{
		if (hasVirtualChildren())
		{
			IParseNode[] result = new IParseNode[fVirtualChildren.size() + 1];
			result = fVirtualChildren.toArray(result);
			result[result.length - 1] = getReferenceNode();
			return result;
		}
		return new IParseNode[] { getReferenceNode() };
	}

	public Type getType()
	{
		return fType;
	}

	private boolean hasVirtualChildren()
	{
		return fVirtualChildren != null && fVirtualChildren.size() > 0;
	}

	public void setLabel(String text)
	{
		fLabel = text;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof JSOutlineItem))
		{
			return false;
		}
		JSOutlineItem other = (JSOutlineItem) obj;
		return fLabel.equals(other.fLabel) && getSourceRange().equals(other.getSourceRange());
	}

	@Override
	public int hashCode()
	{
		return 31 * fLabel.hashCode() + getSourceRange().hashCode();
	}

	public boolean isExported()
	{
		IParseNode refNode = getReferenceNode().getParent();
		if (refNode instanceof JSNode) {
			JSNode jsNode = (JSNode) refNode;
			return jsNode.isExported();
		}
		return false;
	}

	public boolean isStatic()
	{
		IParseNode refNode = getReferenceNode().getParent();
		if (refNode instanceof JSGetterNode) {
			return ((JSGetterNode) refNode).isStatic();
		}
		else if (refNode instanceof JSFunctionNode) {
			return ((JSFunctionNode) refNode).isStatic();
		}
		return false;
	}
}

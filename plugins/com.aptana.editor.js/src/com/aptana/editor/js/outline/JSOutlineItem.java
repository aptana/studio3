/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class JSOutlineItem extends CommonOutlineItem
{

	public static enum Type
	{
		PROPERTY, ARRAY, BOOLEAN, FUNCTION, NULL, NUMBER, OBJECT_LITERAL, REGEX, STRING
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
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import com.aptana.parsing.ast.ILanguageNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class CommonOutlineItem implements ILanguageNode, IRange, Comparable<IRange>
{
	private IRange fSourceRange;
	private IParseNode fReferenceNode;

	public CommonOutlineItem(IRange sourceRange, IParseNode referenceNode)
	{
		fSourceRange = sourceRange;
		fReferenceNode = referenceNode;
	}

	public String getLabel()
	{
		return (fReferenceNode == null) ? "" : fReferenceNode.getText(); //$NON-NLS-1$
	}

	public int compareTo(IRange o)
	{
		return getStartingOffset() - o.getStartingOffset();
	}

	public boolean contains(int offset)
	{
		return fSourceRange.contains(offset);
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof CommonOutlineItem))
		{
			return false;
		}
		CommonOutlineItem item = (CommonOutlineItem) obj;
		return fReferenceNode.equals(item.fReferenceNode)
				&& fSourceRange.getStartingOffset() == item.fSourceRange.getStartingOffset()
				&& fSourceRange.getEndingOffset() == item.fSourceRange.getEndingOffset();
	}

	public int getEndingOffset()
	{
		return fSourceRange.getEndingOffset();
	}

	public int getLength()
	{
		return fSourceRange.getLength();
	}

	public IParseNode getReferenceNode()
	{
		return fReferenceNode;
	}

	public IRange getSourceRange()
	{
		return fSourceRange;
	}

	public int getStartingOffset()
	{
		return fSourceRange.getStartingOffset();
	}

	public String getLanguage()
	{
		return getReferenceNode().getLanguage();
	}

	public int hashCode()
	{
		int hash = fReferenceNode.hashCode();
		hash = hash * 31 + fSourceRange.getStartingOffset();
		hash = hash * 31 + fSourceRange.getEndingOffset();
		return hash;
	}

	public boolean isEmpty()
	{
		return fSourceRange.isEmpty();
	}

	public void setRange(IRange range)
	{
		fSourceRange = range;
	}
}

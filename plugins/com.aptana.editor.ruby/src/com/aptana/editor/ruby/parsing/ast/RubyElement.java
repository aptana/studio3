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
package com.aptana.editor.ruby.parsing.ast;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;

public class RubyElement extends ParseNode implements IRubyElement
{

	private static final String EMPTY = ""; //$NON-NLS-1$
	private int occurrenceCount = 1;

	protected RubyElement()
	{
		super(IRubyParserConstants.LANGUAGE);
	}

	public RubyElement(int start, int end)
	{
		super(IRubyParserConstants.LANGUAGE);
		this.start = start;
		this.end = end;
	}

	public String getName()
	{
		return EMPTY;
	}

	public IRubyElement[] getChildrenOfType(int type)
	{
		List<IRubyElement> list = new ArrayList<IRubyElement>();
		IParseNode[] children = getChildren();
		for (IParseNode child : children)
		{
			if (child.getNodeType() == type)
			{
				list.add((IRubyElement) child);
			}
		}
		return list.toArray(new IRubyElement[list.size()]);
	}

	public int getOccurrenceCount()
	{
		return occurrenceCount;
	}

	public void incrementOccurrence()
	{
		occurrenceCount++;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RubyElement))
			return false;

		return getName().equals(((RubyElement) obj).getName());
	}

	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + getName().hashCode();
	}
}

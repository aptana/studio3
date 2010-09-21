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

import com.aptana.editor.ruby.core.IImportContainer;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.parsing.ast.IParseNode;

public class RubyImportContainer extends RubyElement implements IImportContainer
{

	private static final String NAME = "require/load declarations"; //$NON-NLS-1$

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.IMPORT_CONTAINER;
	}

	@Override
	public int getStart()
	{
		if (getChildCount() == 0)
		{
			return super.getStart();
		}
		return getChild(0).getStartingOffset();
	}

	@Override
	public int getEnd()
	{
		int size = getChildCount();
		if (size == 0)
		{
			return super.getEnd();
		}
		return getChild(size - 1).getEndingOffset();
	}

	@Override
	public IParseNode getNodeAtOffset(int offset)
	{
		IParseNode[] children = getChildren();
		for (IParseNode child : children)
		{
			if (child.getStartingOffset() <= offset && offset <= child.getEndingOffset())
			{
				return child.getNodeAtOffset(offset);
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}

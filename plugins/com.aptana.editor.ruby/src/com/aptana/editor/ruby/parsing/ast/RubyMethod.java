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

import java.util.HashSet;
import java.util.Set;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyMethod;

public class RubyMethod extends NamedMember implements IRubyMethod
{

	private String[] fParameters;
	private Visibility fVisibility;
	private boolean isSingleton;
	private Set<String> blockVars;

	public RubyMethod(String name, String[] parameters, int start, int nameStart, int nameEnd)
	{
		super(name, start, nameStart, nameEnd);
		fParameters = parameters;
		blockVars = new HashSet<String>();
	}

	public void addBlockVar(String name)
	{
		blockVars.add(name);
	}

	public String[] getBlockVars()
	{
		return blockVars.toArray(new String[blockVars.size()]);
	}

	public String[] getParameters()
	{
		return fParameters;
	}

	public Visibility getVisibility()
	{
		return fVisibility;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.METHOD;
	}

	public boolean isSingleton()
	{
		return isSingleton;
	}

	public void setVisibility(Visibility visibility)
	{
		fVisibility = visibility;
	}

	public void setIsSingleton(boolean singleton)
	{
		isSingleton = singleton;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(getName());
		text.append("("); //$NON-NLS-1$
		String[] params = getParameters();
		for (int i = 0; i < params.length; ++i)
		{
			text.append(params[i]);
			if (i < params.length - 1)
			{
				text.append(", "); //$NON-NLS-1$
			}
		}
		text.append(")"); //$NON-NLS-1$
		return text.toString();
	}

	public boolean isConstructor()
	{
		return !isSingleton() && getName().equals("initialize"); //$NON-NLS-1$
	}
}

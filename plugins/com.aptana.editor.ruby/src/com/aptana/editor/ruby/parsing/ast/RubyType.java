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
import java.util.Arrays;
import java.util.List;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyField;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyType;

public class RubyType extends NamedMember implements IRubyType
{

	private static final String[] EMPTY_ARRAY = new String[0];

	/**
	 * the names of the module this type includes
	 */
	private String[] includedModuleNames;

	/**
	 * the name of the superclass for this type
	 */
	private String superclassName;

	public RubyType(String name, int start, int nameStart, int nameEnd)
	{
		super(name, start, nameStart, nameEnd);
		includedModuleNames = EMPTY_ARRAY;
	}

	public IRubyField[] getFields()
	{
		List<IRubyElement> elements = new ArrayList<IRubyElement>();
		elements.addAll(Arrays.asList(getChildrenOfType(IRubyElement.CONSTANT)));
		elements.addAll(Arrays.asList(getChildrenOfType(INSTANCE_VAR)));
		elements.addAll(Arrays.asList(getChildrenOfType(CLASS_VAR)));
		return elements.toArray(new IRubyField[elements.size()]);
	}

	public IRubyMethod[] getMethods()
	{
		IRubyElement[] elements = getChildrenOfType(IRubyElement.METHOD);
		IRubyMethod[] methods = new IRubyMethod[elements.length];
		for (int i = 0; i < elements.length; ++i)
		{
			methods[i] = (IRubyMethod) elements[i];
		}
		return methods;
	}

	public String[] getIncludedModuleNames()
	{
		if (isAnonymous())
		{
			return EMPTY_ARRAY;
		}
		return includedModuleNames;
	}

	public String getSuperclassName()
	{
		if (isAnonymous())
		{
			if (includedModuleNames.length > 0)
			{
				return includedModuleNames[0];
			}
		}
		return superclassName;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.TYPE;
	}

	public boolean isClass()
	{
		return true;
	}

	public boolean isModule()
	{
		return false;
	}

	/**
	 * Sets the names of the modules this type includes.
	 */
	public void setIncludedModuleNames(String[] includedModuleNames)
	{
		this.includedModuleNames = includedModuleNames;
	}

	/**
	 * Sets the name of this type's superclass.
	 */
	public void setSuperclassName(String superclassName)
	{
		this.superclassName = superclassName;
	}

	private boolean isAnonymous()
	{
		return getName() == null || getName().length() == 0;
	}
}

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
package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexQueryHelper
{
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return IndexManager.getInstance().getIndex(URI.create(JSIndexConstants.METADATA_INDEX_LOCATION));
	}

	private JSIndexReader _reader;

	/**
	 * JSContentAssistant
	 */
	public JSIndexQueryHelper()
	{
		this._reader = new JSIndexReader();
	}

	/**
	 * getCoreGlobals
	 * 
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getCoreGlobals()
	{
		return this.getMembers(getIndex(), JSTypeConstants.WINDOW_TYPE);
	}

	/**
	 * getFunction
	 * 
	 * @param index
	 * @param typeName
	 * @param methodName
	 * @param fields
	 * @return
	 */
	protected FunctionElement getFunction(Index index, String typeName, String methodName)
	{
		FunctionElement result = null;

		try
		{
			result = this._reader.getFunction(index, typeName, methodName);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	protected List<FunctionElement> getFunctions(Index index, List<String> typeNames)
	{
		List<FunctionElement> result = null;

		try
		{
			result = this._reader.getFunctions(index, typeNames);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	protected List<FunctionElement> getFunctions(Index index, String typeName)
	{
		List<FunctionElement> result = null;

		try
		{
			result = this._reader.getFunctions(index, typeName);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getGlobal
	 * 
	 * @param index
	 * @param name
	 * @param fields
	 * @return
	 */
	public PropertyElement getGlobal(Index index, String name)
	{
		PropertyElement result = this.getMember(index, JSTypeConstants.WINDOW_TYPE, name);

		if (result == null)
		{
			result = this.getMember(getIndex(), JSTypeConstants.WINDOW_TYPE, name);
		}

		return result;
	}

	/**
	 * getMember
	 * 
	 * @param index
	 * @param typeName
	 * @param memberName
	 * @param fields
	 * @return
	 */
	protected PropertyElement getMember(Index index, String typeName, String memberName)
	{
		PropertyElement result = this.getProperty(index, typeName, memberName);

		if (result == null)
		{
			result = this.getFunction(index, typeName, memberName);
		}

		return result;
	}

	/**
	 * getMembers
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	protected List<PropertyElement> getMembers(Index index, List<String> typeNames)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getProperties(index, typeNames));
		result.addAll(this.getFunctions(index, typeNames));

		return result;
	}

	/**
	 * getMembers
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	protected List<PropertyElement> getMembers(Index index, String typeName)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getProperties(index, typeName));
		result.addAll(this.getFunctions(index, typeName));

		return result;
	}

	/**
	 * getProjectGlobals
	 * 
	 * @param index
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getProjectGlobals(Index index)
	{
		return this.getMembers(index, JSTypeConstants.WINDOW_TYPE);
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	protected List<PropertyElement> getProperties(Index index, List<String> typeNames)
	{
		List<PropertyElement> result = null;

		try
		{
			result = this._reader.getProperties(index, typeNames);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	protected List<PropertyElement> getProperties(Index index, String typeName)
	{
		List<PropertyElement> result = null;

		try
		{
			result = this._reader.getProperties(index, typeName);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getProperty
	 * 
	 * @param index
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	protected PropertyElement getProperty(Index index, String typeName, String propertyName)
	{
		PropertyElement result = null;

		try
		{
			result = this._reader.getProperty(index, typeName, propertyName);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * getType
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public TypeElement getType(Index index, String typeName, boolean indexMembers)
	{
		TypeElement result = this._reader.getType(index, typeName, indexMembers);

		if (result == null)
		{
			result = this._reader.getType(getIndex(), typeName, indexMembers);
		}

		return result;
	}

	/**
	 * getTypeAncestorNames
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public List<String> getTypeAncestorNames(Index index, String typeName)
	{
		// Using linked hash set to preserve the order items were added to set
		Set<String> types = new LinkedHashSet<String>();

		// Using linked list since it provides a queue interface
		Queue<String> queue = new LinkedList<String>();

		// prime the queue
		queue.offer(typeName);

		while (queue.isEmpty() == false)
		{
			String name = queue.poll();
			TypeElement type = this.getType(index, name, false);

			if (type != null)
			{
				for (String parentType : type.getParentTypes())
				{
					if (types.contains(parentType) == false)
					{
						types.add(parentType);

						if (JSTypeConstants.OBJECT_TYPE.equals(parentType) == false)
						{
							queue.offer(parentType);
						}
					}
				}
			}
		}

		return new ArrayList<String>(types);
	}

	/**
	 * getTypeMember
	 * 
	 * @param index
	 * @param typeName
	 * @param memberName
	 * @param fields
	 * @return
	 */
	public PropertyElement getTypeMember(Index index, String typeName, String memberName)
	{
		PropertyElement result = this.getMember(index, typeName, memberName);

		if (result == null)
		{
			result = this.getMember(getIndex(), typeName, memberName);
		}

		return result;
	}

	/**
	 * getTypeMembers
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getTypeMembers(Index index, List<String> typeNames)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getMembers(getIndex(), typeNames));
		result.addAll(this.getMembers(index, typeNames));

		return result;
	}

	/**
	 * getTypeMembers
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getTypeMembers(Index index, String typeName)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getMembers(getIndex(), typeName));
		result.addAll(this.getMembers(index, typeName));

		return result;
	}
}

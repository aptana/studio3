/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.mortbay.util.ajax.JSON;

import com.aptana.core.util.StringUtil;
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
	 * getIndexAsJSON
	 * 
	 * @return
	 */
	public String getIndexAsJSON()
	{
		return this.getIndexAsJSON(getIndex());
	}

	/**
	 * getIndexAsJSON
	 * 
	 * @param index
	 * @return
	 */
	public String getIndexAsJSON(Index index)
	{
		String result = StringUtil.EMPTY;

		try
		{
			List<TypeElement> types = this._reader.getTypes(index, true);
			Map<String, Object> docs = new HashMap<String, Object>();

			// sort types by name
			Collections.sort(types, new Comparator<TypeElement>()
			{
				public int compare(TypeElement arg0, TypeElement arg1)
				{
					return arg0.getName().compareTo(arg1.getName());
				}
			});

			// include types as a separate property
			docs.put("types", types); //$NON-NLS-1$

			// convert to JSON
			result = JSON.toString(docs);
		}
		catch (IOException e)
		{
			JSPlugin.logError(e.getMessage(), e);
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
		List<PropertyElement> properties = this.getProperties(index, typeNames);
		List<FunctionElement> functions = this.getFunctions(index, typeNames);

		if (properties != null)
		{
			result.addAll(properties);
		}

		if (functions != null)
		{
			result.addAll(functions);
		}

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
		List<PropertyElement> properties = this.getProperties(index, typeName);
		List<FunctionElement> functions = this.getFunctions(index, typeName);

		if (properties != null)
		{
			result.addAll(properties);
		}

		if (functions != null)
		{
			result.addAll(functions);
		}

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
		List<PropertyElement> globalMembers = this.getMembers(getIndex(), typeNames);
		List<PropertyElement> projectMembers = this.getMembers(index, typeNames);

		if (globalMembers != null)
		{
			result.addAll(globalMembers);
		}

		if (projectMembers != null)
		{
			result.addAll(projectMembers);
		}

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
		List<PropertyElement> globalMembers = this.getMembers(getIndex(), typeName);
		List<PropertyElement> projectMembers = this.getMembers(index, typeName);

		if (globalMembers != null)
		{
			result.addAll(globalMembers);
		}

		if (projectMembers != null)
		{
			result.addAll(projectMembers);
		}

		return result;
	}

	/**
	 * getTypeProperties
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public List<PropertyElement> getTypeProperties(Index index, String typeName)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		List<PropertyElement> globalProperties = this.getProperties(getIndex(), typeName);
		List<PropertyElement> projectProperties = this.getProperties(index, typeName);

		if (globalProperties != null)
		{
			result.addAll(globalProperties);
		}

		if (projectProperties != null)
		{
			result.addAll(projectProperties);
		}

		return result;
	}
}

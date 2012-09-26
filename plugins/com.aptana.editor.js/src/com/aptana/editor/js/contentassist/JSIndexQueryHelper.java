/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

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

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.IJSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.jetty.util.epl.ajax.JSON;

public class JSIndexQueryHelper
{
	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		IndexManager manager = getIndexManager();
		return manager == null ? null : manager.getIndex(URI.create(IJSIndexConstants.METADATA_INDEX_LOCATION));
	}

	protected static IndexManager getIndexManager()
	{
		IndexPlugin plugin = IndexPlugin.getDefault();
		return plugin == null ? null : plugin.getIndexManager();
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
	 * getFunctions
	 * 
	 * @param index
	 * @param typeName
	 * @param methodName
	 * @param fields
	 * @return
	 */
	protected List<FunctionElement> getFunctions(Index index, String typeName, String methodName)
	{
		return this._reader.getFunctions(index, typeName, methodName);
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
		return this._reader.getFunctions(index, typeNames);
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
		return this._reader.getFunctions(index, typeName);
	}

	/**
	 * getGlobals
	 * 
	 * @param index
	 * @param name
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getGlobals(Index index, String name)
	{
		List<PropertyElement> indexGlobals = this.getMembers(index, JSTypeConstants.WINDOW_TYPE, name);
		List<PropertyElement> builtinGlobals = this.getMembers(getIndex(), JSTypeConstants.WINDOW_TYPE, name);

		return new ArrayList<PropertyElement>(CollectionsUtil.union(indexGlobals, builtinGlobals));
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
	protected List<PropertyElement> getMembers(Index index, String typeName, String memberName)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		List<FunctionElement> functions = this.getFunctions(index, typeName, memberName);
		List<PropertyElement> properties = this.getProperties(index, typeName, memberName);

		if (functions != null)
		{
			result.addAll(functions);
		}

		if (properties != null)
		{
			result.addAll(properties);
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
		List<FunctionElement> functions = this.getFunctions(index, typeNames);
		List<PropertyElement> properties = this.getProperties(index, typeNames);

		if (functions != null)
		{
			result.addAll(functions);
		}

		if (properties != null)
		{
			result.addAll(properties);
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
		List<FunctionElement> functions = this.getFunctions(index, typeName);
		List<PropertyElement> properties = this.getProperties(index, typeName);

		if (functions != null)
		{
			result.addAll(functions);
		}

		if (properties != null)
		{
			result.addAll(properties);
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
		return this._reader.getProperties(index, typeNames);
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
		return this._reader.getProperties(index, typeName);
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
	public List<PropertyElement> getProperties(Index index, String typeName, String propertyName)
	{
		return this._reader.getProperties(index, typeName, propertyName);
	}

	/**
	 * getType
	 * 
	 * @param index
	 * @param typeName
	 * @param includeMembers
	 * @return
	 */
	public List<TypeElement> getTypes(Index index, String typeName, boolean includeMembers)
	{
		List<TypeElement> result = new ArrayList<TypeElement>();
		List<TypeElement> indexTypes = this._reader.getType(index, typeName, includeMembers);
		List<TypeElement> builtinTypes = this._reader.getType(getIndex(), typeName, includeMembers);

		if (!CollectionsUtil.isEmpty(indexTypes))
		{
			result.addAll(indexTypes);
		}

		if (!CollectionsUtil.isEmpty(builtinTypes))
		{
			result.addAll(builtinTypes);
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

		while (!queue.isEmpty())
		{
			String name = queue.poll();
			List<TypeElement> typeList = this.getTypes(index, name, false);

			if (typeList != null)
			{
				for (TypeElement type : typeList)
				{
					for (String parentType : type.getParentTypes())
					{
						if (!types.contains(parentType))
						{
							types.add(parentType);

							if (!JSTypeConstants.OBJECT_TYPE.equals(parentType))
							{
								queue.offer(parentType);
							}
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
	public List<PropertyElement> getTypeMembers(Index index, String typeName, String memberName)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();
		List<PropertyElement> indexMembers = this.getMembers(index, typeName, memberName);
		List<PropertyElement> builtinMembers = this.getMembers(getIndex(), typeName, memberName);

		if (indexMembers != null)
		{
			result.addAll(indexMembers);
		}

		if (builtinMembers != null)
		{
			result.addAll(builtinMembers);
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
		List<PropertyElement> projectMembers = this.getMembers(index, typeNames);
		List<PropertyElement> globalMembers = this.getMembers(getIndex(), typeNames);

		if (projectMembers != null)
		{
			result.addAll(projectMembers);
		}

		if (globalMembers != null)
		{
			result.addAll(globalMembers);
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
		List<PropertyElement> projectMembers = this.getMembers(index, typeName);
		List<PropertyElement> globalMembers = this.getMembers(getIndex(), typeName);

		if (projectMembers != null)
		{
			result.addAll(projectMembers);
		}

		if (globalMembers != null)
		{
			result.addAll(globalMembers);
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
		List<PropertyElement> projectProperties = this.getProperties(index, typeName);
		List<PropertyElement> globalProperties = this.getProperties(getIndex(), typeName);

		if (projectProperties != null)
		{
			result.addAll(projectProperties);
		}

		if (globalProperties != null)
		{
			result.addAll(globalProperties);
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<TypeElement> getTypes()
	{
		return getTypes(getIndex());
	}

	/**
	 * getTypes
	 * 
	 * @param index
	 * @return
	 */
	public List<TypeElement> getTypes(Index index)
	{
		List<TypeElement> result = Collections.emptyList();

		if (index != null)
		{
			result = this._reader.getTypes(index, true);
		}

		return result;
	}
}

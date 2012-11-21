/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.index;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexReader;

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
	 * @param fileName
	 * @param project
	 * @param fields
	 * @return
	 */
	public Collection<PropertyElement> getCoreGlobals(IProject project, String fileName)
	{
		return getGlobals(getIndex(), project, fileName);
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
	public List<FunctionElement> getFunctions(Index index, String typeName, String methodName)
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
	 * Attempts to get a specific member off the global type. Attempts to determine the correct global type to query
	 * based on the project and filename passed in (Global or Window)
	 * 
	 * @param index
	 *            the index to query
	 * @param project
	 *            The project we're currently working with
	 * @param fileName
	 *            The name of the file we're working on
	 * @param memberName
	 *            The name of the member of global we're trying to query for.
	 * @return
	 */
	public Collection<PropertyElement> getGlobals(Index index, IProject project, String fileName, String memberName)
	{
		// Need to search Global or Window!
		String globalTypeName = JSTypeUtil.getGlobalType(project, fileName);
		Collection<PropertyElement> indexGlobals = getMembers(index, globalTypeName, memberName);
		Collection<PropertyElement> builtinGlobals = getMembers(getIndex(), globalTypeName, memberName);
		return CollectionsUtil.union(indexGlobals, builtinGlobals);
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
	protected Collection<PropertyElement> getMembers(Index index, String typeName, String memberName)
	{
		// FIXME Search both categories at once?
		return CollectionsUtil.union(getFunctions(index, typeName, memberName),
				getProperties(index, typeName, memberName));
	}

	/**
	 * getMembers
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	protected Collection<PropertyElement> getMembers(Index index, List<String> typeNames)
	{
		// FIXME Search both categories at once?
		return CollectionsUtil.union(getFunctions(index, typeNames), getProperties(index, typeNames));
	}

	/**
	 * getMembers
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	protected Collection<PropertyElement> getMembers(Index index, String typeName)
	{
		// FIXME Search both categories at once?
		return CollectionsUtil.union(getFunctions(index, typeName), getProperties(index, typeName));
	}

	/**
	 * Gets all the members defined on Window or Global in the given Index.
	 * 
	 * @param index
	 * @param fileName
	 * @param project
	 * @return
	 */
	public Collection<PropertyElement> getGlobals(Index index, IProject project, String fileName)
	{
		String globalType = JSTypeUtil.getGlobalType(project, fileName);
		if (JSTypeConstants.WINDOW_TYPE.equals(globalType))
		{
			return this.getMembers(index,
					CollectionsUtil.newList(JSTypeConstants.WINDOW_TYPE, JSTypeConstants.GLOBAL_TYPE));
		}
		return this.getMembers(index, globalType);
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
	public Collection<TypeElement> getTypes(Index index, String typeName, boolean includeMembers)
	{
		return CollectionsUtil.union(_reader.getType(index, typeName, includeMembers),
				_reader.getType(getIndex(), typeName, includeMembers));
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
			Collection<TypeElement> typeList = this.getTypes(index, name, false);

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
	public Collection<PropertyElement> getTypeMembers(Index index, String typeName, String memberName)
	{
		return CollectionsUtil.union(getMembers(index, typeName, memberName),
				getMembers(getIndex(), typeName, memberName));
	}

	/**
	 * getTypeMembers
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	public Collection<PropertyElement> getTypeMembers(Index index, List<String> typeNames)
	{
		return CollectionsUtil.union(getMembers(index, typeNames), getMembers(getIndex(), typeNames));
	}

	/**
	 * getTypeMembers
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public Collection<PropertyElement> getTypeMembers(Index index, String typeName)
	{
		return CollectionsUtil.union(getMembers(index, typeName), getMembers(getIndex(), typeName));
	}

	/**
	 * getTypeProperties
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public Collection<PropertyElement> getTypeProperties(Index index, String typeName)
	{
		return CollectionsUtil.union(getProperties(index, typeName), getProperties(getIndex(), typeName));
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
		if (index != null)
		{
			return _reader.getTypes(index, true);
		}

		return Collections.emptyList();
	}

	public List<EventElement> getEvents(Index index, String owningType, String eventName)
	{
		return this._reader.getEvents(index, owningType, eventName);
	}
}

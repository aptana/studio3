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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.IFilter;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexReader;

/**
 * This class is intended to silently query the types/functions/properties/events for a given project. It uses the
 * project's build paths to know what indices to look through and their ordering.
 * 
 * @author cwilliams
 */
public class JSIndexQueryHelper
{

	private static final String DOT_EXPORTS = ".exports"; //$NON-NLS-1$

	public static Index getJSCoreIndex()
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
	 * The in-order list of indices to query.
	 */
	private List<Index> indices;

	/**
	 * The project we're operating on. This may be null.
	 */
	private IProject project;

	/**
	 * When we are operating on a given index. This should be called when we're running queries for an external lib,
	 * contained in the index. This way it only queries that index and the JS Core.
	 * 
	 * @param index
	 */
	public JSIndexQueryHelper(Index index)
	{
		this(index, getJSCoreIndex());
	}

	/**
	 * This is when we're operating within a project. Here we query the project index, any of the project's build paths,
	 * plus the JS Core.
	 * 
	 * @param project
	 */
	// FIXME What if the project is null, shouldn't we pass in the possible index?
	public JSIndexQueryHelper(IProject project)
	{
		this(getIndices(project));
		this.project = project;
	}

	private JSIndexQueryHelper(Index... indices)
	{
		this.indices = CollectionsUtil.filter(Arrays.asList(indices), new IFilter<Index>()
		{
			public boolean include(Index item)
			{
				return item != null;
			}
		});
		this._reader = new JSIndexReader();
	}

	private static Index[] getIndices(IProject project)
	{
		// TODO Eventually the JS Core index(indices, JS core and DOM should be separated) should already be in the
		// build paths we get below!
		ArrayList<Index> indices = new ArrayList<Index>();

		if (project != null)
		{
			// Grab the project build paths and set up the indices
			Set<IBuildPathEntry> entries = getBuildPathManager().getBuildPaths(project);
			for (IBuildPathEntry entry : entries)
			{
				Index index = getIndexManager().getIndex(entry.getPath());
				indices.add(index);
			}
			Index index = getIndexManager().getIndex(project.getLocationURI());
			indices.add(index);
		}
		indices.add(getJSCoreIndex());
		indices.trimToSize();
		return indices.toArray(new Index[indices.size()]);
	}

	protected static BuildPathManager getBuildPathManager()
	{
		return BuildPathManager.getInstance();
	}

	/**
	 * Attempts to get a specific member off the global type. Attempts to determine the correct global type to query
	 * based on the project and filename passed in (Global or Window)
	 * 
	 * @param project
	 *            The project we're currently working with
	 * @param fileName
	 *            The name of the file we're working on
	 * @param memberName
	 *            The name of the member of global we're trying to query for.
	 * @return
	 */
	public Collection<PropertyElement> getGlobals(String fileName, String memberName)
	{
		// Need to search Global or Window!
		String globalTypeName = JSTypeUtil.getGlobalType(project, fileName);
		List<String> types = CollectionsUtil.newList(JSTypeConstants.GLOBAL_TYPE);
		if (JSTypeConstants.WINDOW_TYPE.equals(globalTypeName))
		{
			types.add(0, JSTypeConstants.WINDOW_TYPE);
		}

		ArrayList<PropertyElement> properties = new ArrayList<PropertyElement>();
		for (String type : types)
		{
			// TODO Search all types at once
			for (Index index : indices)
			{
				// FIXME Search both categories at once?
				properties.addAll(_reader.getFunctions(index, type, memberName));
				properties.addAll(_reader.getProperties(index, type, memberName));
			}
		}

		return properties;
	}

	/**
	 * Gets all the members defined on Window or Global in the given Index.
	 * 
	 * @param index
	 * @param fileName
	 * @param project
	 * @return
	 */
	public Collection<PropertyElement> getGlobals(String fileName)
	{
		String globalType = JSTypeUtil.getGlobalType(project, fileName);
		List<String> types = CollectionsUtil.newList(JSTypeConstants.GLOBAL_TYPE);
		if (JSTypeConstants.WINDOW_TYPE.equals(globalType))
		{
			types.add(0, JSTypeConstants.WINDOW_TYPE);
		}
		return getTypeMembers(types);
	}

	/**
	 * Loads a given type from the index. Combines entries in the given index and the JS Core index.
	 * 
	 * @param index
	 * @param typeName
	 * @param includeMembers
	 * @return
	 */
	public Collection<TypeElement> getTypes(String typeName, boolean includeMembers)
	{
		ArrayList<TypeElement> types = new ArrayList<TypeElement>();
		for (Index index : indices)
		{
			types.addAll(_reader.getType(index, typeName, includeMembers));
		}
		types.trimToSize();
		return types;
	}

	/**
	 * getTypeAncestorNames
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public List<String> getTypeAncestorNames(String typeName)
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
			Collection<TypeElement> typeList = getTypes(name, false);

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
	 * Gets all the functions and properties with the given name for the given type.
	 * 
	 * @param typeName
	 * @param memberName
	 * @return
	 */
	public Collection<PropertyElement> getTypeMembers(String typeName, String memberName)
	{
		ArrayList<PropertyElement> properties = new ArrayList<PropertyElement>();
		for (Index index : indices)
		{
			properties.addAll(_reader.getFunctions(index, typeName, memberName));
			properties.addAll(_reader.getProperties(index, typeName, memberName));
		}
		return properties;
	}

	/**
	 * Gets all the functions and properties for the given types.
	 * 
	 * @param typeNames
	 * @return
	 */
	public Collection<PropertyElement> getTypeMembers(List<String> typeNames)
	{
		if (CollectionsUtil.isEmpty(typeNames))
		{
			return Collections.emptyList();
		}
		ArrayList<PropertyElement> properties = new ArrayList<PropertyElement>();
		// FIXME Can we search both functions and properties at the same time?
		// FIXME What about "sub-types" that aren't hung explicitly off owning type? i.e. "Global.console"
		for (Index index : indices)
		{
			properties.addAll(_reader.getFunctions(index, typeNames));
			properties.addAll(_reader.getProperties(index, typeNames));
		}
		properties.trimToSize();
		return properties;
	}

	/**
	 * Returns the properties on a given type.
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public Collection<PropertyElement> getTypeProperties(String typeName)
	{
		if (StringUtil.isEmpty(typeName))
		{
			return Collections.emptyList();
		}
		ArrayList<PropertyElement> properties = new ArrayList<PropertyElement>();
		for (Index index : indices)
		{
			properties.addAll(_reader.getProperties(index, typeName));
		}
		properties.trimToSize();
		return properties;
	}

	/**
	 * Gets all events defined for the given type name in the given index.
	 * 
	 * @param index
	 * @param owningType
	 * @param eventName
	 * @return
	 */
	public List<EventElement> getEvents(String owningType, String eventName)
	{
		ArrayList<EventElement> events = new ArrayList<EventElement>();
		for (Index index : indices)
		{
			events.addAll(_reader.getEvents(index, owningType, eventName));
		}
		events.trimToSize();
		return events;
	}

	/**
	 * Determines the name of the type holding the object exported for the module defined in the given filepath.
	 * 
	 * @param absolutePath
	 * @return
	 */
	public String getModuleType(IPath absolutePath)
	{
		if (absolutePath == null || absolutePath.isEmpty())
		{
			return null;
		}

		// TODO Do smart lookup of the index that should contain the file?
		for (Index index : indices)
		{
			// Look up our mapping from generated type names to documents
			List<QueryResult> results = index.query(new String[] { IJSIndexConstants.MODULE_DEFINITION }, "*", //$NON-NLS-1$
					SearchPattern.PATTERN_MATCH);
			final String fileURI = absolutePath.toFile().toURI().toString();
			// Find the module declared in the file we resolved to...
			QueryResult match = CollectionsUtil.find(results, new IFilter<QueryResult>()
			{
				public boolean include(QueryResult item)
				{
					return item.getDocuments().contains(fileURI);
				}
			});
			if (match != null)
			{
				// Now use the stored generated type name...
				return match.getWord() + DOT_EXPORTS;
			}
		}
		return null;
	}

	public List<EventElement> getEvents(List<String> owningTypes)
	{
		ArrayList<EventElement> events = new ArrayList<EventElement>();
		for (Index index : indices)
		{
			events.addAll(_reader.getEvents(index, owningTypes));
		}
		events.trimToSize();
		return events;
	}

	public List<PropertyElement> getProperties(List<String> allTypes)
	{
		ArrayList<PropertyElement> properties = new ArrayList<PropertyElement>();
		for (Index index : indices)
		{
			properties.addAll(_reader.getProperties(index, allTypes));
		}
		properties.trimToSize();
		return properties;
	}

	public List<FunctionElement> getFunctions(String typeName, String propertyName)
	{
		ArrayList<FunctionElement> functions = new ArrayList<FunctionElement>();
		for (Index index : indices)
		{
			functions.addAll(_reader.getFunctions(index, typeName, propertyName));
		}
		functions.trimToSize();
		return functions;
	}

	public List<PropertyElement> getProperties(String typeName, String propertyName)
	{
		ArrayList<PropertyElement> properties = new ArrayList<PropertyElement>();
		for (Index index : indices)
		{
			properties.addAll(_reader.getProperties(index, typeName, propertyName));
		}
		properties.trimToSize();
		return properties;
	}

	/**
	 * See {@link SearchPattern} for matchFlags.
	 * 
	 * @param pattern
	 *            The pattern used to search the indices.
	 * @param matchFlags
	 *            The flags used to determine type of search (case sensitivity; if we're doing pattern, regexp, prefix
	 *            or exact matching)
	 * @return
	 */
	public Collection<String> getTypeNames(String pattern, int matchFlags)
	{
		ArrayList<String> properties = new ArrayList<String>();
		for (Index index : indices)
		{
			properties.addAll(_reader.getTypeNames(index, pattern, matchFlags));
		}
		properties.trimToSize();
		return properties;
	}

	/**
	 * Searches for a given method off a base type and it's ancestors in the type hierarchy. Returns the first match
	 * found.
	 * 
	 * @param typeName
	 *            The base type we're searching. We search this type and then if we fail to find a match, we search up
	 *            it's hierarchy in-order.
	 * @param methodName
	 *            The name of the method/function we're trying to find.
	 * @return null if no such method found, otherwise first instance we find.
	 */
	public FunctionElement findFunctionInHierarchy(String typeName, String methodName)
	{
		List<String> types = getTypeAncestorNames(typeName);
		types.add(0, typeName);
		for (String type : types)
		{
			// TODO Can we search against all the types simultaneously, then order results by hierarchy and return
			// first match? This would drop number of queries from N to 1...
			Collection<FunctionElement> functions = getFunctions(type, methodName);
			if (!CollectionsUtil.isEmpty(functions))
			{
				return functions.iterator().next();
			}
		}
		return null;
	}

	/**
	 * @param generatedModuleId
	 * @return an IPath. NEVER NULL! If we can't look up the module by it's id, then we return an empty IPath.
	 */
	public IPath getModulePath(String generatedModuleId)
	{
		if (generatedModuleId == null || generatedModuleId.isEmpty())
		{
			return Path.EMPTY;
		}
		if (generatedModuleId.endsWith(DOT_EXPORTS))
		{
			generatedModuleId = generatedModuleId.substring(0, generatedModuleId.length() - DOT_EXPORTS.length());
		}

		for (Index index : indices)
		{
			// Look up our mapping from generated type names to documents
			List<QueryResult> results = index.query(new String[] { IJSIndexConstants.MODULE_DEFINITION },
					generatedModuleId, SearchPattern.EXACT_MATCH | SearchPattern.CASE_SENSITIVE);
			if (results != null && !results.isEmpty())
			{
				QueryResult match = results.get(0);
				Set<String> docs = match.getDocuments();
				if (docs != null && !docs.isEmpty())
				{
					String uri = docs.iterator().next();
					String root = index.getRoot().toString();
					if (uri.startsWith(root))
					{
						uri = uri.substring(root.length());
					}
					// Remove the .js extension
					return Path.fromOSString(uri).removeFileExtension();
				}

			}
		}
		return Path.EMPTY;
	}

}

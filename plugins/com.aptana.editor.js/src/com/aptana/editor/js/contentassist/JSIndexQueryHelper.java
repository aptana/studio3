package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexQueryHelper
{
	private static final EnumSet<ContentSelector> PARENT_TYPES = EnumSet.of(ContentSelector.PARENT_TYPES);

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return IndexManager.getInstance().getIndex(URI.create(JSIndexConstants.METADATA));
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
	public List<PropertyElement> getCoreGlobals(EnumSet<ContentSelector> fields)
	{
		return this.getMembers(getIndex(), JSTypeConstants.WINDOW_TYPE, fields);
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
	protected FunctionElement getFunction(Index index, String typeName, String methodName, EnumSet<ContentSelector> fields)
	{
		FunctionElement result = null;

		try
		{
			result = this._reader.getFunction(index, typeName, methodName, fields);
		}
		catch (IOException e)
		{
			Activator.logError(e.getMessage(), e);
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
	protected List<FunctionElement> getFunctions(Index index, List<String> typeNames, EnumSet<ContentSelector> fields)
	{
		List<FunctionElement> result = null;

		try
		{
			result = this._reader.getFunctions(index, typeNames, fields);
		}
		catch (IOException e)
		{
			Activator.logError(e.getMessage(), e);
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
	protected List<FunctionElement> getFunctions(Index index, String typeName, EnumSet<ContentSelector> fields)
	{
		List<FunctionElement> result = null;

		try
		{
			result = this._reader.getFunctions(index, typeName, fields);
		}
		catch (IOException e)
		{
			Activator.logError(e.getMessage(), e);
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
	public PropertyElement getGlobal(Index index, String name, EnumSet<ContentSelector> fields)
	{
		PropertyElement result = this.getMember(index, JSTypeConstants.WINDOW_TYPE, name, fields);

		if (result == null)
		{
			result = this.getMember(getIndex(), JSTypeConstants.WINDOW_TYPE, name, fields);
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
	protected PropertyElement getMember(Index index, String typeName, String memberName, EnumSet<ContentSelector> fields)
	{
		PropertyElement result = this.getProperty(index, typeName, memberName, fields);

		if (result == null)
		{
			result = this.getFunction(index, typeName, memberName, fields);
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
	protected List<PropertyElement> getMembers(Index index, List<String> typeNames, EnumSet<ContentSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getProperties(index, typeNames, fields));
		result.addAll(this.getFunctions(index, typeNames, fields));

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
	protected List<PropertyElement> getMembers(Index index, String typeName, EnumSet<ContentSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getProperties(index, typeName, fields));
		result.addAll(this.getFunctions(index, typeName, fields));

		return result;
	}

	/**
	 * getProjectGlobals
	 * 
	 * @param index
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getProjectGlobals(Index index, EnumSet<ContentSelector> fields)
	{
		return this.getMembers(index, JSTypeConstants.WINDOW_TYPE, fields);
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param typeNames
	 * @param fields
	 * @return
	 */
	protected List<PropertyElement> getProperties(Index index, List<String> typeNames, EnumSet<ContentSelector> fields)
	{
		List<PropertyElement> result = null;

		try
		{
			result = this._reader.getProperties(index, typeNames, fields);
		}
		catch (IOException e)
		{
			Activator.logError(e.getMessage(), e);
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
	protected List<PropertyElement> getProperties(Index index, String typeName, EnumSet<ContentSelector> fields)
	{
		List<PropertyElement> result = null;

		try
		{
			result = this._reader.getProperties(index, typeName, fields);
		}
		catch (IOException e)
		{
			Activator.logError(e.getMessage(), e);
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
	protected PropertyElement getProperty(Index index, String typeName, String propertyName, EnumSet<ContentSelector> fields)
	{
		PropertyElement result = null;

		try
		{
			result = this._reader.getProperty(index, typeName, propertyName, fields);
		}
		catch (IOException e)
		{
			Activator.logError(e.getMessage(), e);
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
	public TypeElement getType(Index index, String typeName, EnumSet<ContentSelector> fields)
	{
		TypeElement result = this._reader.getType(index, typeName, fields);

		if (result == null)
		{
			result = this._reader.getType(getIndex(), typeName, fields);
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
		Queue<String> queue = new ArrayDeque<String>();

		// prime the queue
		queue.offer(typeName);

		while (queue.isEmpty() == false)
		{
			String name = queue.poll();
			TypeElement type = this.getType(index, name, PARENT_TYPES);

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
	public PropertyElement getTypeMember(Index index, String typeName, String memberName, EnumSet<ContentSelector> fields)
	{
		PropertyElement result = this.getMember(index, typeName, memberName, fields);

		if (result == null)
		{
			result = this.getMember(getIndex(), typeName, memberName, fields);
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
	public List<PropertyElement> getTypeMembers(Index index, List<String> typeNames, EnumSet<ContentSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getMembers(getIndex(), typeNames, fields));
		result.addAll(this.getMembers(index, typeNames, fields));

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
	public List<PropertyElement> getTypeMembers(Index index, String typeName, EnumSet<ContentSelector> fields)
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		result.addAll(this.getMembers(getIndex(), typeName, fields));
		result.addAll(this.getMembers(index, typeName, fields));

		return result;
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.index.core.CategoryInfo;
import com.aptana.index.core.Index;
import com.aptana.js.core.index.IJSIndexConstants;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.internal.core.index.JSIndexReader;

/**
 * ClassGroupElement
 */
public class ClassGroupElement extends BaseElement
{

	private Index index;
	private List<ClassElement> classes;
	private int typeCount = -1;
	private CategoryInfo typeInfo;
	private CategoryInfo propertyInfo;
	private CategoryInfo functionInfo;

	/**
	 * ClassGroupElement
	 * 
	 * @param name
	 * @param classes
	 */
	public ClassGroupElement(String name, Index index)
	{
		this.index = index;
		setName(name);
	}

	/**
	 * getClassCount
	 * 
	 * @return
	 */
	public int getClassCount()
	{
		if (typeCount == -1)
		{
			JSIndexReader reader = new JSIndexReader();
			List<String> typeNames = reader.getTypeNames(index);
			Set<String> mergedTypeNames = new HashSet<String>();

			for (String typeName : typeNames)
			{
				boolean isClassType = JSTypeUtil.isClassType(typeName);
				String baseName = isClassType ? JSTypeUtil.getClassType(typeName) : typeName;

				mergedTypeNames.add(baseName);
			}

			typeCount = mergedTypeNames.size();
		}

		return typeCount;
	}

	/**
	 * @return the classes
	 */
	public List<ClassElement> getClasses()
	{
		if (classes == null)
		{
			JSIndexReader reader = new JSIndexReader();
			List<TypeElement> types = reader.getTypes(index, true);

			classes = JSTypeUtil.typesToClasses(types);
		}

		return classes;
	}

	/**
	 * Return the grand total number of function entries in the index.
	 * 
	 * @return
	 */
	public CategoryInfo getFunctionInfo()
	{
		if (functionInfo == null)
		{
			JSIndexReader reader = new JSIndexReader();

			functionInfo = reader.getCategoryInfo(index, IJSIndexConstants.FUNCTION);
		}

		return functionInfo;
	}

	/**
	 * Return the grand total number of property entries in the index.
	 * 
	 * @return
	 */
	public CategoryInfo getPropertyInfo()
	{
		if (propertyInfo == null)
		{
			JSIndexReader reader = new JSIndexReader();

			propertyInfo = reader.getCategoryInfo(index, IJSIndexConstants.PROPERTY);
		}

		return propertyInfo;
	}

	/**
	 * Return the grand total number of type entries in the index. This includes Class<MyClass> and MyClass.
	 * 
	 * @return
	 */
	public CategoryInfo getTypeInfo()
	{
		if (typeInfo == null)
		{
			JSIndexReader reader = new JSIndexReader();

			typeInfo = reader.getCategoryInfo(index, IJSIndexConstants.TYPE);
		}

		return typeInfo;
	}

	/**
	 * Try to determine if this element has any content without actually loading classes into memory
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return getTypeInfo().count > 0;
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.aptana.core.util.ObjectUtil;

/**
 * ClassElement
 */
public class ClassElement extends TypeElement
{
	private List<TypeElement> classTypes;
	private List<TypeElement> instanceTypes;

	/**
	 * addClassType
	 * 
	 * @param type
	 */
	public void addClassType(TypeElement type)
	{
		if (type != null)
		{
			if (classTypes == null)
			{
				classTypes = new ArrayList<TypeElement>();
			}

			classTypes.add(type);
		}
	}

	/**
	 * addInstanceType
	 * 
	 * @param type
	 */
	public void addInstanceType(TypeElement type)
	{
		if (type != null)
		{
			if (instanceTypes == null)
			{
				instanceTypes = new ArrayList<TypeElement>();
			}

			instanceTypes.add(type);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (obj instanceof ClassElement)
		{
			ClassElement element = (ClassElement) obj;

			// NOTE: This model element is only being used in the Index View which currently exists for debugging
			// purposes only. TreeViews use "equals" to find nodes during selection, so we essentially only need to
			// compare label names
			result = ObjectUtil.areEqual(getName(), element.getName());
		}
		else
		{
			result = super.equals(obj);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#getDocuments()
	 */
	@Override
	public List<String> getDocuments()
	{
		List<String> result = new ArrayList<String>();

		if (classTypes != null)
		{
			for (TypeElement type : classTypes)
			{
				result.addAll(type.getDocuments());
			}
		}

		if (instanceTypes != null)
		{
			for (TypeElement type : instanceTypes)
			{
				result.addAll(type.getDocuments());
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.TypeElement#getEvents()
	 */
	@Override
	public List<EventElement> getEvents()
	{
		// Use linked hash set to preserve add order
		Set<EventElement> result = new LinkedHashSet<EventElement>();

		if (classTypes != null)
		{
			for (TypeElement classType : classTypes)
			{
				result.addAll(classType.getEvents());
			}
		}

		if (instanceTypes != null)
		{
			for (TypeElement instanceType : instanceTypes)
			{
				result.addAll(instanceType.getEvents());
			}
		}

		return new ArrayList<EventElement>(result);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.TypeElement#getParentTypes()
	 */
	@Override
	public List<String> getParentTypes()
	{
		// Use linked has set to preserve add order
		Set<String> result = new LinkedHashSet<String>();

		if (classTypes != null)
		{
			for (TypeElement classType : classTypes)
			{
				result.addAll(classType.getParentTypes());
			}
		}

		if (instanceTypes != null)
		{
			for (TypeElement instanceType : instanceTypes)
			{
				result.addAll(instanceType.getParentTypes());
			}
		}

		return new ArrayList<String>(result);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.TypeElement#getProperties()
	 */
	@Override
	public List<PropertyElement> getProperties()
	{
		// NOTE: use linked hash set to both preserve order and to remove duplicates
		Set<PropertyElement> result = new LinkedHashSet<PropertyElement>();

		if (classTypes != null)
		{
			for (TypeElement classType : classTypes)
			{
				result.addAll(classType.getProperties());
			}
		}

		if (instanceTypes != null)
		{
			for (TypeElement instanceType : instanceTypes)
			{
				result.addAll(instanceType.getProperties());
			}
		}

		return new ArrayList<PropertyElement>(result);
	}

	/**
	 * removeClassType
	 * 
	 * @param type
	 */
	public void removeClassType(TypeElement type)
	{
		if (classTypes != null)
		{
			classTypes.remove(type);
		}
	}

	/**
	 * removeInstanceType
	 * 
	 * @param type
	 */
	public void removeInstanceType(TypeElement type)
	{
		if (instanceTypes != null)
		{
			instanceTypes.remove(type);
		}
	}
}

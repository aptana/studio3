/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;

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
	 * @see com.aptana.editor.js.contentassist.model.TypeElement#getProperties()
	 */
	@Override
	public List<PropertyElement> getProperties()
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		if (classTypes != null)
		{
			for (TypeElement classType : classTypes)
			{
				for (PropertyElement property : classType.getProperties())
				{
					result.add(property);
				}
			}
		}

		if (instanceTypes != null)
		{
			for (TypeElement instanceType : instanceTypes)
			{
				for (PropertyElement property : instanceType.getProperties())
				{
					result.add(property);
				}
			}
		}

		return result;
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

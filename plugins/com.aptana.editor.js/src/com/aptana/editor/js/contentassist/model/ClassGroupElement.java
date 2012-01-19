/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * ClassGroupElement
 */
public class ClassGroupElement extends BaseElement<ClassGroupElement.Property>
{
	enum Property implements IPropertyInformation<ClassGroupElement>
	{
		NAME(Messages.ClassGroupElement_Name)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getName();
			}
		},
		TYPE_COUNT(Messages.ClassGroupElement_TypeCount)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getClasses().size();
			}
		};

		private String header;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}
	}

	List<ClassElement> classes;

	/**
	 * ClassGroupElement
	 * 
	 * @param name
	 * @param classes
	 */
	public ClassGroupElement(String name, List<ClassElement> classes)
	{
		this.classes = classes;
		setName(name);
	}

	/**
	 * @return the classes
	 */
	public List<ClassElement> getClasses()
	{
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#getPropertyInfoSet()
	 */
	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

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

import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.inferencing.JSTypeUtil;
import com.aptana.index.core.Index;

/**
 * JSElement
 */
public class JSElement extends BaseElement<JSElement.Property>
{
	enum Property implements IPropertyInformation<JSElement>
	{
		NAME(Messages.JSElement_Name)
		{
			public Object getPropertyValue(JSElement node)
			{
				return node.getName();
			}
		},
		INDEX(Messages.JSElement_IndexFile)
		{
			public Object getPropertyValue(JSElement node)
			{
				return node.getIndex().toString();
			}
		},
		CHILD_COUNT(Messages.JSElement_ChildCount)
		{
			public Object getPropertyValue(JSElement node)
			{
				// TODO: don't emit children that have zero classes?
				return 2;
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

	private Index index;

	/**
	 * An element used to group JS content in an Index
	 * 
	 * @param index
	 *            The index that contains JS content
	 */
	public JSElement(Index index)
	{
		this.index = index;
		setName(Messages.JSElement_NodeLabel);
	}

	/**
	 * Returns the element associated with this element
	 * 
	 * @return Returns an Index
	 */
	public Index getIndex()
	{
		return index;
	}

	/**
	 * Return a list of class elements from the workspace global area (JS metadata)
	 * 
	 * @return Returns a potentially empty, non-null list of ClassElements
	 */
	public List<ClassElement> getWorkspaceGlobalClasses()
	{
		JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
		List<TypeElement> types = queryHelper.getTypes();

		return JSTypeUtil.typesToClasses(types);
	}

	/**
	 * Return a list of class elements from the project area
	 * 
	 * @return Returns a potentially empty, non-null list of ClassElements
	 */
	public List<ClassElement> getProjectGlobalClasses()
	{
		JSIndexQueryHelper queryHelper = new JSIndexQueryHelper();
		List<TypeElement> types = queryHelper.getTypes(index);

		return JSTypeUtil.typesToClasses(types);
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

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.index.core.Index;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * ClassGroupElement
 */
public class ClassGroupElement extends BaseElement<ClassGroupElement.Property>
{
	enum Property implements IPropertyInformation<ClassGroupElement>
	{
		NAME(Messages.ClassGroupElement_NameLabel)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getName();
			}
		},
		COUNT(Messages.ClassGroupElement_CountLabel)
		{
			public Object getPropertyValue(ClassGroupElement node)
			{
				return node.getClasses().size();
			}
		};

		private String header;
		private String category;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		private Property(String header, String category)
		{
			this.category = category;
		}

		public String getCategory()
		{
			return category;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private Index index;
	private List<String> classes;

	public ClassGroupElement(Index index)
	{
		this.index = index;
		setName(Messages.ClassGroupElement_ClassGroupElementName);
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public List<String> getClasses()
	{
		if (classes == null)
		{
			CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
			Map<String, String> members = queryHelper.getClasses(index);

			if (!CollectionsUtil.isEmpty(members))
			{
				classes = new ArrayList<String>(members.keySet());
			}
			else
			{
				classes = Collections.emptyList();
			}
		}

		return classes;
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import com.aptana.core.util.StringUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;
import com.aptana.js.core.model.TypeElement;

public class TypeElementPropertySource extends
		BaseElementPropertySource<TypeElement, TypeElementPropertySource.Property>
{

	public TypeElementPropertySource(TypeElement adaptableObject)
	{
		super(adaptableObject);
	}

	enum Property implements IPropertyInformation<TypeElement>
	{
		NAME(Messages.TypeElement_Name)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return node.getName();
			}
		},
		PARENT_TYPES(Messages.TypeElement_ParentTypes)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return StringUtil.join(",", node.getParentTypes()); //$NON-NLS-1$
			}
		},
		DESCRIPTION(Messages.TypeElement_Description)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return node.getDescription();
			}
		},
		DOCUMENTS(Messages.TypeElement_Documents)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return StringUtil.join(", ", node.getDocuments()); //$NON-NLS-1$
			}
		},
		EVENT_COUNT(Messages.TypeElement_EventCount)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return node.getEvents().size();
			}
		},
		PROPERTY_COUNT(Messages.TypeElement_PropertyCount)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return node.getProperties().size();
			}
		},
		DEPRECATED(Messages.TypeElement_Deprecated)
		{
			public Object getPropertyValue(TypeElement node)
			{
				return node.isDeprecated();
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

}

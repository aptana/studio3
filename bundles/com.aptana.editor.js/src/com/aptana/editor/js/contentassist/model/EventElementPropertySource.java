/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.EnumSet;
import java.util.Set;

import com.aptana.index.core.ui.views.IPropertyInformation;
import com.aptana.js.core.model.EventElement;

public class EventElementPropertySource extends
		BaseElementPropertySource<EventElement, EventElementPropertySource.Property>
{

	enum Property implements IPropertyInformation<EventElement>
	{
		NAME(Messages.EventElement_Name)
		{
			public Object getPropertyValue(EventElement node)
			{
				return node.getName();
			}
		},
		OWNING_TYPE(Messages.EventElement_OwningType)
		{
			public Object getPropertyValue(EventElement node)
			{
				return node.getOwningType();
			}
		},
		PROPERTY_COUNT(Messages.EventElement_PropertyCount)
		{
			public Object getPropertyValue(EventElement node)
			{
				return node.getProperties().size();
			}
		},
		DEPRECATED(Messages.TypeElement_Deprecated)
		{
			public Object getPropertyValue(EventElement node)
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

	public EventElementPropertySource(EventElement adaptableObject)
	{
		super(adaptableObject);
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

}

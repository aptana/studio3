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

import com.aptana.core.util.StringUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;

public class PropertyElementPropertySource extends
		BaseElementPropertySource<PropertyElement, PropertyElementPropertySource.Property>
{

	enum Property implements IPropertyInformation<PropertyElement>
	{
		NAME(Messages.PropertyElement_Name)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getName();
			}
		},
		DESCRIPTION(Messages.PropertyElement_Description)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getDescription();
			}
		},
		OWNING_TYPE(Messages.PropertyElement_OwningType)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getOwningType();
			}
		},
		CLASS_PROPERTY(Messages.PropertyElement_StaticProperty)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.isClassProperty();
			}
		},
		INSTANCE_PROPERTY(Messages.PropertyElement_InstanceProperty)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.isInstanceProperty();
			}
		},
		RETURN_TYPES(Messages.PropertyElement_ReturnTypes)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				if (node instanceof FunctionElement)
				{
					return StringUtil.join(", ", ((FunctionElement) node).getReturnTypeNames()); //$NON-NLS-1$
				}
				else
				{
					return StringUtil.EMPTY;
				}
			}
		},
		TYPES(Messages.PropertyElement_Types)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return StringUtil.join(", ", node.getTypeNames()); //$NON-NLS-1$
			}
		},
		DOCUMENTS(Messages.PropertyElement_Documents)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return StringUtil.join(", ", node.getDocuments()); //$NON-NLS-1$
			}
		},
		DEPRECATED(Messages.TypeElement_Deprecated)
		{
			public Object getPropertyValue(PropertyElement node)
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

	public PropertyElementPropertySource(PropertyElement adaptableObject)
	{
		super(adaptableObject);
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

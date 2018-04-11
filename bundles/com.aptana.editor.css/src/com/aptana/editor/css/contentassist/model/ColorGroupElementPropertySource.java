/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import java.util.EnumSet;
import java.util.Set;

import com.aptana.css.core.model.ClassGroupElement;
import com.aptana.css.core.model.ColorGroupElement;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class ColorGroupElementPropertySource extends
		BaseElementPropertySource<ColorGroupElement, ColorGroupElementPropertySource.Property>
{
	enum Property implements IPropertyInformation<ColorGroupElement>
	{
		NAME(Messages.ColorGroupElement_NameLabel)
		{
			public Object getPropertyValue(ColorGroupElement node)
			{
				return node.getName();
			}
		},
		COUNT(Messages.ColorGroupElement_CountLabel)
		{
			public Object getPropertyValue(ColorGroupElement node)
			{
				return node.getColors().size();
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

	public ColorGroupElementPropertySource(ClassGroupElement adaptableObject)
	{
		super(adaptableObject);
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

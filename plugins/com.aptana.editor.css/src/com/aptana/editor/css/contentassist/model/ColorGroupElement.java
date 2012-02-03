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
import java.util.Set;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.index.core.Index;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * ClassGroupElement
 */
public class ColorGroupElement extends BaseElement<ColorGroupElement.Property>
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

	private Index index;
	private List<String> members;

	public ColorGroupElement(Index index)
	{
		this.index = index;
		setName(Messages.ColorGroupElement_ColorElementName);
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public List<String> getColors()
	{
		if (members == null)
		{
			CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
			Set<String> colors = queryHelper.getColors(index);

			if (!CollectionsUtil.isEmpty(colors))
			{
				members = new ArrayList<String>(colors);
			}
			else
			{
				members = Collections.emptyList();
			}
		}

		return members;
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

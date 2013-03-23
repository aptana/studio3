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

import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.css.core.model.CSSElement;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class CSSElementPropertySource extends BaseElementPropertySource<CSSElement, CSSElementPropertySource.Property>
{
	enum Property implements IPropertyInformation<CSSElement>
	{
		NAME(Messages.CSSElement_NameLabel)
		{
			public Object getPropertyValue(CSSElement node)
			{
				return node.getName();
			}
		},
		INDEX(Messages.CSSElement_IndexLabel)
		{
			public Object getPropertyValue(CSSElement node)
			{
				return node.getIndex().toString();
			}
		},
		INDEX_FILE(Messages.CSSElement_IndexFileLabel)
		{
			public Object getPropertyValue(CSSElement node)
			{
				return node.getIndex().getIndexFile().getAbsolutePath();
			}
		},
		INDEX_FILE_SIZE(Messages.CSSElement_IndexFileSizeLabel)
		{
			public Object getPropertyValue(CSSElement node)
			{
				return node.getIndex().getIndexFile().length();
			}
		},
		CHILD_COUNT(Messages.CSSElement_ChildCountLabel)
		{
			public Object getPropertyValue(CSSElement node)
			{
				// TODO: don't emit children with no content?
				return 3;
			}
		},
		VERSION(Messages.CSSElement_VersionLabel)
		{
			public Object getPropertyValue(CSSElement node)
			{
				return ICSSIndexConstants.INDEX_VERSION;
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

	public CSSElementPropertySource(CSSElement adaptableObject)
	{
		super(adaptableObject);
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

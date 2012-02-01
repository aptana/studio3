/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.EnumSet;
import java.util.Set;

import com.aptana.editor.html.contentassist.index.IHTMLIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * HTMLElement
 */
public class HTMLElement extends BaseElement<HTMLElement.Property>
{
	enum Property implements IPropertyInformation<HTMLElement>
	{
		NAME(Messages.HTMLElement_NameLabel)
		{
			public Object getPropertyValue(HTMLElement node)
			{
				return node.getName();
			}
		},
		INDEX(Messages.HTMLElement_IndexLabel)
		{
			public Object getPropertyValue(HTMLElement node)
			{
				return node.getIndex().toString();
			}
		},
		INDEX_FILE(Messages.HTMLElement_IndexFileLabel)
		{
			public Object getPropertyValue(HTMLElement node)
			{
				return node.getIndex().getIndexFile().getAbsolutePath();
			}
		},
		INDEX_FILE_SIZE(Messages.HTMLElement_IndexFileSizeLabel)
		{
			public Object getPropertyValue(HTMLElement node)
			{
				return node.getIndex().getIndexFile().length();
			}
		},
		CHILD_COUNT(Messages.HTMLElement_ChildCountLabel)
		{
			public Object getPropertyValue(HTMLElement node)
			{
				// TODO: don't emit children with no content?
				return 2;
			}
		},
		VERSION(Messages.HTMLElement_VersionLabel)
		{
			public Object getPropertyValue(HTMLElement node)
			{
				return IHTMLIndexConstants.INDEX_VERSION;
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

	/**
	 * HTMLElement
	 * 
	 * @param index
	 */
	public HTMLElement(Index index)
	{
		this.index = index;
		setName(Messages.HTMLElement_HTMLElementName);
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public Index getIndex()
	{
		return index;
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}
}

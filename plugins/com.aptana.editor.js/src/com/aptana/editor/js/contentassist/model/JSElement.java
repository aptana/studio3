/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.EnumSet;
import java.util.Set;

import com.aptana.editor.js.contentassist.index.IJSIndexConstants;
import com.aptana.index.core.Index;
import com.aptana.index.core.ui.views.IPropertyInformation;

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
		INDEX(Messages.JSElement_IndexLabel)
		{
			public Object getPropertyValue(JSElement node)
			{
				return node.getIndex().toString();
			}
		},
		INDEX_FILE(Messages.JSElement_IndexFile)
		{
			public Object getPropertyValue(JSElement node)
			{
				return node.getIndex().getIndexFile().getAbsolutePath();
			}
		},
		INDEX_FILE_SIZE(Messages.JSElement_IndexFileSizeLabel)
		{
			public Object getPropertyValue(JSElement node)
			{
				return node.getIndex().getIndexFile().length();
			}
		},
		CHILD_COUNT(Messages.JSElement_ChildCount)
		{
			public Object getPropertyValue(JSElement node)
			{
				// TODO: don't emit children that have zero classes?
				return 2;
			}
		},
		VERSION(Messages.JSElement_Version)
		{
			public Object getPropertyValue(JSElement node)
			{
				return IJSIndexConstants.INDEX_VERSION;
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

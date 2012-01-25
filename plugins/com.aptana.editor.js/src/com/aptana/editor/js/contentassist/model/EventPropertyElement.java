/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

/**
 * EventProperty
 */
public class EventPropertyElement extends BaseElement<EventPropertyElement.Property>
{
	enum Property implements IPropertyInformation<EventPropertyElement>
	{
		NAME(Messages.EventPropertyElement_Name)
		{
			public Object getPropertyValue(EventPropertyElement node)
			{
				return node.getName();
			}
		},
		TYPE(Messages.EventPropertyElement_Type)
		{
			public Object getPropertyValue(EventPropertyElement node)
			{
				return node.getType();
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

	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$

	private String _type;

	/**
	 * EventProperty
	 */
	public EventPropertyElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
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

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this._type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(TYPE_PROPERTY, this.getType());
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();
		printer.print(this.getName());
		printer.print(" : "); //$NON-NLS-1$
		printer.print(this.getType());
	}
}

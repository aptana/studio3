/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;

/**
 * EventElement
 */
public class EventElement extends BaseElement<EventElement.Property>
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

	private static final String OWNING_TYPE_PROPERTY = "owningType"; //$NON-NLS-1$
	private static final String PROPERTIES_PROPERTY = "properties"; //$NON-NLS-1$

	private String _owningType;
	private List<EventPropertyElement> _properties;

	/**
	 * EventElement
	 */
	public EventElement()
	{
	}

	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(EventPropertyElement property)
	{
		if (property != null)
		{
			if (this._properties == null)
			{
				this._properties = new ArrayList<EventPropertyElement>();
			}

			// TODO: handle possible dupes?

			this._properties.add(property);
		}
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

		if (object.containsKey(PROPERTIES_PROPERTY))
		{
			List<EventPropertyElement> properties = IndexUtil.createList(object.get(PROPERTIES_PROPERTY),
					EventPropertyElement.class);

			for (EventPropertyElement property : properties)
			{
				this.addProperty(property);
			}
		}
	}

	/**
	 * getOwningType
	 * 
	 * @return
	 */
	public String getOwningType()
	{
		return StringUtil.getStringValue(this._owningType);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<EventPropertyElement> getProperties()
	{
		return CollectionsUtil.getListValue(this._properties);
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
	 * setOwningType
	 * 
	 * @param type
	 */
	public void setOwningType(String type)
	{
		this._owningType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(OWNING_TYPE_PROPERTY, this.getOwningType());
		out.add(PROPERTIES_PROPERTY, this.getProperties());
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();
		printer.print("event "); //$NON-NLS-1$

		printer.print(this.getName());
		printer.println(" {"); //$NON-NLS-1$
		printer.increaseIndent();

		for (EventPropertyElement property : this.getProperties())
		{
			property.toSource(printer);
			printer.println(';');
		}

		printer.decreaseIndent();
		printer.printWithIndent('}');
	}
}

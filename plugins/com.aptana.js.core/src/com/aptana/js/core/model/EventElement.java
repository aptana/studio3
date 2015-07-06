/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

/**
 * EventElement
 */
public class EventElement extends BaseElement
{
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

			if (CollectionsUtil.isEmpty(properties))
			{
				return;
			}
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
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
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
	@Override
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

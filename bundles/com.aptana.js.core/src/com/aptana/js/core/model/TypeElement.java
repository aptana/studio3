/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aptana.core.IFilter;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.jetty.util.epl.ajax.JSON.Output;

public class TypeElement extends BaseElement
{
	private static final String FUNCTIONS_PROPERTY = "functions"; //$NON-NLS-1$
	private static final String PROPERTIES_PROPERTY = "properties"; //$NON-NLS-1$
	private static final String EVENTS_PROPERTY = "events"; //$NON-NLS-1$
	private static final String EXAMPLES_PROPERTY = "examples"; //$NON-NLS-1$
	private static final String REMARKS_PROPERTY = "remarks"; //$NON-NLS-1$
	private static final String IS_INTERNAL_PROPERTY = "internal"; //$NON-NLS-1$

	private List<String> _parentTypes;
	private List<PropertyElement> _properties;
	private List<EventElement> _events;
	private List<String> _examples;
	private List<String> _remarks;
	private boolean _serializeProperties;
	private boolean _isInternal;

	/**
	 * TypeElement
	 */
	public TypeElement()
	{
	}

	/**
	 * addEvent
	 * 
	 * @param event
	 */
	public void addEvent(EventElement event)
	{
		if (event != null)
		{
			if (this._events == null)
			{
				this._events = new ArrayList<EventElement>();
			}

			this._events.add(event);

			event.setOwningType(this.getName());
		}
	}

	/**
	 * addExample
	 * 
	 * @param example
	 */
	public void addExample(String example)
	{
		if (example != null && example.length() > 0)
		{
			if (this._examples == null)
			{
				this._examples = new ArrayList<String>();
			}

			this._examples.add(example);
		}
	}

	/**
	 * addParentType
	 * 
	 * @param type
	 */
	public void addParentType(String type)
	{
		if (type != null && type.length() > 0)
		{
			if (this._parentTypes == null)
			{
				this._parentTypes = new ArrayList<String>();
			}

			if (!this._parentTypes.contains(type))
			{
				// Don't allow adding self as parent!
				if (!type.equals(getName()))
				{
					this._parentTypes.add(type);
				}
			}
		}
	}

	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(PropertyElement property)
	{
		if (property != null)
		{
			if (this._properties == null)
			{
				this._properties = new ArrayList<PropertyElement>();
			}

			this._properties.add(property);

			property.setOwningType(this.getName());
		}
	}

	/**
	 * addRemark
	 * 
	 * @param remark
	 */
	public void addRemark(String remark)
	{
		if (remark != null && remark.length() > 0)
		{
			if (this._remarks == null)
			{
				this._remarks = new ArrayList<String>();
			}

			this._remarks.add(remark);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		// NOTE: parent types are added to this type element when reading types from the JS index

		if (object.containsKey(PROPERTIES_PROPERTY))
		{
			List<PropertyElement> properties = IndexUtil.createList(object.get(PROPERTIES_PROPERTY),
					PropertyElement.class);
			if (!CollectionsUtil.isEmpty(properties))
			{
				for (PropertyElement property : properties)
				{
					this.addProperty(property);
				}
			}
		}

		if (object.containsKey(FUNCTIONS_PROPERTY))
		{
			List<FunctionElement> functions = IndexUtil.createList(object.get(FUNCTIONS_PROPERTY),
					FunctionElement.class);
			if (!CollectionsUtil.isEmpty(functions))
			{
				for (FunctionElement function : functions)
				{
					this.addProperty(function);
				}
			}
		}

		if (object.containsKey(EVENTS_PROPERTY))
		{
			List<EventElement> events = IndexUtil.createList(object.get(EVENTS_PROPERTY), EventElement.class);
			if (!CollectionsUtil.isEmpty(events))
			{
				for (EventElement event : events)
				{
					this.addEvent(event);
				}
			}
		}

		if (object.containsKey(EXAMPLES_PROPERTY))
		{
			List<String> examples = IndexUtil.createList(object.get(EXAMPLES_PROPERTY));
			if (!CollectionsUtil.isEmpty(examples))
			{
				for (String example : examples)
				{
					this.addExample(example);
				}
			}
		}

		if (object.containsKey(REMARKS_PROPERTY))
		{
			List<String> remarks = IndexUtil.createList(object.get(REMARKS_PROPERTY));
			if (!CollectionsUtil.isEmpty(remarks))
			{
				for (String remark : remarks)
				{
					this.addRemark(remark);
				}
			}
		}

		// JSCA holds "isInternal", but we serialize as "internal"
		if (object.containsKey("isInternal")) //$NON-NLS-1$
		{
			this.setIsInternal(Boolean.TRUE == object.get("isInternal")); //$NON-NLS-1$ // $codepro.audit.disable useEquals
		}
		else
		{
			this.setIsInternal(Boolean.TRUE == object.get(IS_INTERNAL_PROPERTY)); // $codepro.audit.disable useEquals
		}
	}

	/**
	 * getConstructors
	 * 
	 * @return
	 */
	public List<FunctionElement> getConstructors()
	{
		List<FunctionElement> result = new ArrayList<FunctionElement>();

		for (PropertyElement property : this.getProperties())
		{
			if (property instanceof FunctionElement)
			{
				FunctionElement function = (FunctionElement) property;

				if (function.isConstructor())
				{
					result.add(function);
				}
			}
		}

		return result;
	}

	/**
	 * getEvents
	 * 
	 * @return
	 */
	public List<EventElement> getEvents()
	{
		return CollectionsUtil.getListValue(this._events);
	}

	public EventElement getEvent(final String eventName)
	{
		return CollectionsUtil.find(getEvents(), new IFilter<EventElement>()
		{
			public boolean include(EventElement item)
			{
				return item != null && ObjectUtil.areEqual(eventName, item.getName());
			}
		});
	}

	/**
	 * getExamples
	 * 
	 * @return
	 */
	public List<String> getExamples()
	{
		return CollectionsUtil.getListValue(this._examples);
	}

	/**
	 * getParentTypes
	 * 
	 * @return
	 */
	public List<String> getParentTypes()
	{
		return CollectionsUtil.getListValue(this._parentTypes);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return CollectionsUtil.getListValue(this._properties);
	}

	/**
	 * getProperty
	 * 
	 * @param name
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		int index = this.getPropertyIndex(name);
		PropertyElement result = null;

		if (index != -1)
		{
			result = this._properties.get(index);
		}

		return result;
	}

	/**
	 * getPropertyIndex
	 * 
	 * @param name
	 * @return
	 */
	protected int getPropertyIndex(String name)
	{
		int result = -1;

		if (name != null && name.length() > 0 && this._properties != null)
		{
			for (int i = 0; i < this._properties.size(); i++)
			{
				PropertyElement property = this._properties.get(i);

				if (name.equals(property.getName()))
				{
					result = i;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getRemarks
	 * 
	 * @return
	 */
	public List<String> getRemarks()
	{
		return CollectionsUtil.getListValue(this._remarks);
	}

	/**
	 * getSerializeProperties
	 * 
	 * @return
	 */
	public boolean getSerializeProperties()
	{
		return this._serializeProperties;
	}

	/**
	 * hasParentTypes
	 * 
	 * @return
	 */
	public boolean hasParentTypes()
	{
		return this._parentTypes != null && !this._parentTypes.isEmpty();
	}

	/**
	 * hasProperties
	 * 
	 * @return
	 */
	public boolean hasProperties()
	{
		return this._properties != null && !this._properties.isEmpty();
	}

	/**
	 * isInternal
	 * 
	 * @return
	 */
	public boolean isInternal()
	{
		return _isInternal;
	}

	/**
	 * removeProperty
	 * 
	 * @param property
	 */
	public boolean removeProperty(PropertyElement property)
	{
		boolean result = false;

		if (this._properties != null)
		{
			result = this._properties.remove(property);
		}

		return result;
	}

	/**
	 * setIsInternal
	 * 
	 * @param isInternal
	 */
	public void setIsInternal(boolean isInternal)
	{
		this._isInternal = isInternal;
	}

	/**
	 * setSerializeProperties
	 * 
	 * @param value
	 */
	public void setSerializeProperties(boolean value)
	{
		this._serializeProperties = value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.BaseElement#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		if (this._serializeProperties)
		{
			List<PropertyElement> properties = new ArrayList<PropertyElement>(this.getProperties());
			List<FunctionElement> functions = new ArrayList<FunctionElement>();

			for (PropertyElement property : properties)
			{
				if (property instanceof FunctionElement)
				{
					functions.add((FunctionElement) property);
				}
			}

			properties.removeAll(functions);

			// NOTE: parent types are written to the index by JSIndexWriter, so we don't need to serialize that value
			out.add(PROPERTIES_PROPERTY, properties);
			out.add(FUNCTIONS_PROPERTY, functions);
			out.add(EVENTS_PROPERTY, this.getEvents());
			out.add(EXAMPLES_PROPERTY, this.getExamples());
			out.add(REMARKS_PROPERTY, this.getRemarks());
			out.add(IS_INTERNAL_PROPERTY, this.isInternal());
		}
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.print(this.getName());

		if (this.hasParentTypes())
		{
			printer.print(" : ").print(StringUtil.join(", ", this.getParentTypes())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		printer.println().print('{').increaseIndent().println();

		for (PropertyElement property : this.getProperties())
		{
			property.toSource(printer);
			printer.println(';');
		}

		for (EventElement event : this.getEvents())
		{
			event.toSource(printer);
			printer.println(';');
		}

		printer.decreaseIndent().println('}');
	}

	public String toString()
	{
		SourcePrinter printer = new SourcePrinter();
		toSource(printer);
		return printer.toString();
	}
}

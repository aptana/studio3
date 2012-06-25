/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class ElementElement extends BaseElement<ElementElement.Property>
{
	enum Property implements IPropertyInformation<ElementElement>
	{
		NAME(Messages.ElementElement_NameLabel)
		{
			public Object getPropertyValue(ElementElement node)
			{
				return node.getName();
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

	private static final String USER_AGENTS_PROPERTY = "userAgents"; //$NON-NLS-1$
	private static final String SPECIFICATIONS_PROPERTY = "specifications"; //$NON-NLS-1$
	private static final String REFERENCES_PROPERTY = "references"; //$NON-NLS-1$
	private static final String EVENTS_PROPERTY = "events"; //$NON-NLS-1$
	private static final String ATTRIBUTES_PROPERTY = "attributes"; //$NON-NLS-1$
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$
	private static final String RELATED_CLASS_PROPERTY = "relatedClass"; //$NON-NLS-1$
	private static final String EXAMPLE_PROPERTY = "example"; //$NON-NLS-1$
	private static final String DEPRECATED_PROPERTY = "deprecated"; //$NON-NLS-1$
	private static final String DISPLAY_NAME_PROPERTY = "displayName"; //$NON-NLS-1$

	private String _displayName;
	private String _relatedClass;
	private List<String> _attributes;
	private List<SpecificationElement> _specifications;
	private List<UserAgentElement> _userAgents;
	private String _deprecated;
	private List<String> _events;
	private String _example;
	private List<String> _references;
	private String _remark;

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
	}

	/**
	 * addAttribute
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(String attribute)
	{
		if (attribute != null && attribute.length() > 0)
		{
			if (this._attributes == null)
			{
				this._attributes = new ArrayList<String>();
			}

			this._attributes.add(attribute);
		}
	}

	/**
	 * addEvent
	 * 
	 * @param event
	 *            the event to add
	 */
	public void addEvent(String event)
	{
		if (event != null && event.length() > 0)
		{
			if (this._events == null)
			{
				this._events = new ArrayList<String>();
			}

			this._events.add(event);
		}
	}

	/**
	 * @param reference
	 *            the reference to add
	 */
	public void addReference(String reference)
	{
		if (reference != null && reference.length() > 0)
		{
			if (this._references == null)
			{
				this._references = new ArrayList<String>();
			}

			this._references.add(reference);
		}
	}

	/**
	 * addSpecification
	 * 
	 * @param specification
	 *            the specification to add
	 */
	public void addSpecification(SpecificationElement specification)
	{
		if (specification != null)
		{
			if (this._specifications == null)
			{
				this._specifications = new ArrayList<SpecificationElement>();
			}

			this._specifications.add(specification);
		}
	}

	/**
	 * addUserAgent
	 * 
	 * @param userAgent
	 *            the userAgents to add
	 */
	public void addUserAgent(UserAgentElement userAgent)
	{
		if (userAgent != null)
		{
			if (this._userAgents == null)
			{
				this._userAgents = new ArrayList<UserAgentElement>();
			}

			this._userAgents.add(userAgent);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setDisplayName(StringUtil.getStringValue(object.get(DISPLAY_NAME_PROPERTY)));
		this.setDeprecated(StringUtil.getStringValue(object.get(DEPRECATED_PROPERTY)));
		this.setExample(StringUtil.getStringValue(object.get(EXAMPLE_PROPERTY)));
		this.setRelatedClass(StringUtil.getStringValue(object.get(RELATED_CLASS_PROPERTY)));
		this.setRemark(StringUtil.getStringValue(object.get(REMARK_PROPERTY)));

		this._attributes = IndexUtil.createList(object.get(ATTRIBUTES_PROPERTY));
		this._events = IndexUtil.createList(object.get(EVENTS_PROPERTY));
		this._references = IndexUtil.createList(object.get(REFERENCES_PROPERTY));
		this._specifications = IndexUtil.createList(object.get(SPECIFICATIONS_PROPERTY), SpecificationElement.class);
		this._userAgents = IndexUtil.createList(object.get(USER_AGENTS_PROPERTY), UserAgentElement.class);
	}

	/**
	 * getAttributes
	 * 
	 * @return the attributes
	 */
	public List<String> getAttributes()
	{
		return CollectionsUtil.getListValue(this._attributes);
	}

	/**
	 * getDeprecated
	 * 
	 * @return the deprecated
	 */
	public String getDeprecated()
	{
		return StringUtil.getStringValue(this._deprecated);
	}

	/**
	 * getDisplayName
	 * 
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return StringUtil.getStringValue(this._displayName);
	}

	/**
	 * getEvents
	 * 
	 * @return the events
	 */
	public List<String> getEvents()
	{
		return CollectionsUtil.getListValue(this._events);
	}

	/**
	 * getExample
	 * 
	 * @return the example
	 */
	public String getExample()
	{
		return StringUtil.getStringValue(this._example);
	}

	/**
	 * getReferences
	 * 
	 * @return the references
	 */
	public List<String> getReferences()
	{
		return CollectionsUtil.getListValue(this._references);
	}

	/**
	 * getRelatedClass
	 * 
	 * @return the relatedClass
	 */
	public String getRelatedClass()
	{
		return StringUtil.getStringValue(this._relatedClass);
	}

	/**
	 * getRemark
	 * 
	 * @return the remark
	 */
	public String getRemark()
	{
		return StringUtil.getStringValue(this._remark);
	}

	/**
	 * getSpecifications
	 * 
	 * @return the specifications
	 */
	public List<SpecificationElement> getSpecifications()
	{
		return CollectionsUtil.getListValue(this._specifications);
	}

	/**
	 * getUserAgentNames
	 * 
	 * @return
	 */
	public List<String> getUserAgentNames()
	{
		return CollectionsUtil.map(getUserAgents(), new IMap<UserAgentElement, String>()
		{
			public String map(UserAgentElement userAgent)
			{
				return userAgent.getPlatform();
			}
		});
	}

	/**
	 * @return the userAgents
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return CollectionsUtil.getListValue(this._userAgents);
	}

	/**
	 * setDeprecated
	 * 
	 * @param deprecated
	 *            the deprecated to set
	 */
	public void setDeprecated(String deprecated)
	{
		this._deprecated = deprecated;
	}

	/**
	 * setDisplayName
	 * 
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
	}

	/**
	 * setExample
	 * 
	 * @param example
	 *            the example to set
	 */
	public void setExample(String example)
	{
		this._example = example;
	}

	/**
	 * setRelatedClass
	 * 
	 * @param relatedClass
	 *            the relatedClass to set
	 */
	public void setRelatedClass(String relatedClass)
	{
		this._relatedClass = relatedClass;
	}

	/**
	 * setRemark
	 * 
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark)
	{
		this._remark = remark;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(DISPLAY_NAME_PROPERTY, this.getDisplayName());
		out.add(DEPRECATED_PROPERTY, this.getDeprecated());
		out.add(EXAMPLE_PROPERTY, this.getExample());
		out.add(RELATED_CLASS_PROPERTY, this.getRelatedClass());
		out.add(REMARK_PROPERTY, this.getRemark());

		out.add(ATTRIBUTES_PROPERTY, this.getAttributes());
		out.add(EVENTS_PROPERTY, this.getEvents());
		out.add(REFERENCES_PROPERTY, this.getReferences());
		out.add(SPECIFICATIONS_PROPERTY, this.getSpecifications());
		out.add(USER_AGENTS_PROPERTY, this.getUserAgents());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ElementElement))
		{
			return false;
		}
		ElementElement other = (ElementElement) obj;
		return ObjectUtil.areEqual(getName(), other.getName());
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}
}

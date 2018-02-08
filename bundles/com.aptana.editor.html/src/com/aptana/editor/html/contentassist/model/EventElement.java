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

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class EventElement extends BaseElement<EventElement.Property>
{
	enum Property implements IPropertyInformation<EventElement>
	{
		NAME(Messages.EventElement_NameLabel)
		{
			public Object getPropertyValue(EventElement node)
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
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$
	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$

	private String _type;
	private List<SpecificationElement> _specifications;
	private List<UserAgentElement> _userAgents;
	private String _remark;

	/**
	 * ElementElement
	 */
	public EventElement()
	{
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

		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
		this.setRemark(StringUtil.getStringValue(object.get(REMARK_PROPERTY)));

		this._specifications = IndexUtil.createList(object.get(SPECIFICATIONS_PROPERTY), SpecificationElement.class);
		this._userAgents = IndexUtil.createList(object.get(USER_AGENTS_PROPERTY), UserAgentElement.class);
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
	 * getType
	 * 
	 * @return the type
	 */
	public String getType()
	{
		return StringUtil.getStringValue(this._type);
	}

	/**
	 * @return the userAgents
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return CollectionsUtil.getListValue(this._userAgents);
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

	/**
	 * setType
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this._type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(TYPE_PROPERTY, this.getType());
		out.add(REMARK_PROPERTY, this.getRemark());

		out.add(SPECIFICATIONS_PROPERTY, this.getSpecifications());
		out.add(USER_AGENTS_PROPERTY, this.getUserAgents());
	}
}

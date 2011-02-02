/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexDocument;
import com.aptana.index.core.IndexUtil;

public abstract class BaseElement implements ICSSMetadataElement, Convertible, IndexDocument
{
	private static final String USER_AGENTS_PROPERTY = "userAgents"; //$NON-NLS-1$
	private static final String EXAMPLE_PROPERTY = "example"; //$NON-NLS-1$
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$

	private String _name;
	private List<UserAgentElement> _userAgents;
	private String _description;
	private String _example;
	private List<String> _documents;

	/**
	 * addDocument
	 * 
	 * @param document
	 */
	public void addDocument(String document)
	{
		if (document != null && document.length() > 0)
		{
			if (this._documents == null)
			{
				this._documents = new ArrayList<String>();
			}

			this._documents.add(document);
		}
	}

	/**
	 * addUserAgent
	 * 
	 * @param userAgent
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
	 * @see org.mortbay.util.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setName(StringUtil.getStringValue(object.get(NAME_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
		this.setExample(StringUtil.getStringValue(object.get(EXAMPLE_PROPERTY)));

		this._userAgents = IndexUtil.createList(object.get(USER_AGENTS_PROPERTY), UserAgentElement.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.ICSSMetadataElement#getDescription()
	 */
	public String getDescription()
	{
		return StringUtil.getStringValue(this._description);
	}

	/**
	 * getDocuments
	 * 
	 * @return
	 */
	public List<String> getDocuments()
	{
		return CollectionsUtil.getListValue(this._documents);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.ICSSMetadataElement#getExample()
	 */
	public String getExample()
	{
		return StringUtil.getStringValue(this._example);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.ICSSMetadataElement#getName()
	 */
	public String getName()
	{
		return StringUtil.getStringValue(this._name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.ICSSMetadataElement#getUserAgentNames()
	 */
	public List<String> getUserAgentNames()
	{
		List<String> result = new ArrayList<String>();

		for (UserAgentElement ua : this.getUserAgents())
		{
			result.add(ua.getPlatform());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.ICSSMetadataElement#getUserAgents()
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return CollectionsUtil.getListValue(this._userAgents);
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setExample
	 * 
	 * @param example
	 */
	public void setExample(String example)
	{
		this._example = example;
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(NAME_PROPERTY, this.getName());
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
		out.add(EXAMPLE_PROPERTY, this.getExample());
		out.add(USER_AGENTS_PROPERTY, this.getUserAgents());
	}
}
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexDocument;
import com.aptana.index.core.IndexUtil;

public class BaseElement implements Convertible, IndexDocument
{
	private static final String USER_AGENTS_PROPERTY = "userAgents"; //$NON-NLS-1$
	private static final String SINCE_PROPERTY = "since"; //$NON-NLS-1$
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$

	private String _name;
	private String _description;
	private List<UserAgentElement> _userAgents;
	private List<SinceElement> _sinceList;
	private List<String> _documents;

	/**
	 * BaseElement
	 */
	public BaseElement()
	{
	}

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
	 * addSince
	 * 
	 * @param since
	 */
	public void addSince(SinceElement since)
	{
		if (since != null)
		{
			if (this._sinceList == null)
			{
				this._sinceList = new ArrayList<SinceElement>();
			}

			this._sinceList.add(since);
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

		this._sinceList = IndexUtil.createList(object.get(SINCE_PROPERTY), SinceElement.class);
		this._userAgents = IndexUtil.createList(object.get(USER_AGENTS_PROPERTY), UserAgentElement.class);
	}

	/**
	 * getDescription
	 * 
	 * @return
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

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return StringUtil.getStringValue(this._name);
	}

	/**
	 * getSinceList
	 * 
	 * @return
	 */
	public List<SinceElement> getSinceList()
	{
		return CollectionsUtil.getListValue(this._sinceList);
	}

	/**
	 * getUserAgentNames
	 * 
	 * @return
	 */
	public List<String> getUserAgentNames()
	{
		List<String> result = new ArrayList<String>();

		for (UserAgentElement userAgent : this.getUserAgents())
		{
			result.add(userAgent.getPlatform());
		}

		return result;
	}

	/**
	 * getUserAgents
	 * 
	 * @return
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
		out.add(SINCE_PROPERTY, this.getSinceList());
		out.add(USER_AGENTS_PROPERTY, this.getUserAgents());
	}
}

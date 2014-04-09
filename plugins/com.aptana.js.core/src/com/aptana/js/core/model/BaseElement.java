/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.CorePlugin;
import com.aptana.core.IUserAgent;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexDocument;
import com.aptana.index.core.IndexUtil;
import com.aptana.jetty.util.epl.ajax.JSON.Convertible;
import com.aptana.jetty.util.epl.ajax.JSON.Output;
import com.aptana.js.core.JSCorePlugin;

public abstract class BaseElement implements Convertible, IndexDocument
{
	private static final String USER_AGENTS_PROPERTY = "userAgents"; //$NON-NLS-1$
	private static final String SINCE_PROPERTY = "since"; //$NON-NLS-1$
	private static final String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
	private static final String NAME_PROPERTY = "name"; //$NON-NLS-1$
	private static final String DEPRECATED_PROPERTY = "deprecated"; //$NON-NLS-1$

	// A special instance used to indicate that this element should associate all user agents with it
	private static final Set<UserAgentElement> ALL_USER_AGENTS = Collections.emptySet();

	private String _name;
	private String _description;
	private Set<UserAgentElement> _userAgents;
	private List<SinceElement> _sinceList;
	private List<String> _documents;
	private boolean _deprecated;

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
			if (this._userAgents == ALL_USER_AGENTS)
			{
				// grab the expanded set of all user agents
				Set<UserAgentElement> userAgents = new HashSet<UserAgentElement>(this.getUserAgents());

				// if the specified user agent exists in the expanded list, then don't do anything. Otherwise, we need
				// to generate the union of the expanded list and the specified user agent
				if (!userAgents.contains(userAgent))
				{
					this._userAgents = userAgents;
					this._userAgents.add(userAgent);
				}
			}
			else if (this._userAgents == null)
			{
				this._userAgents = new HashSet<UserAgentElement>();
				this._userAgents.add(userAgent);
			}
			else
			{
				this._userAgents.add(userAgent);
			}
		}
	}

	/**
	 * createUserAgentSet
	 * 
	 * @param object
	 * @return
	 */
	private Set<UserAgentElement> createUserAgentSet(Object object)
	{
		Set<UserAgentElement> result = null;

		if (object != null && object.getClass().isArray())
		{
			Object[] objects = (Object[]) object;

			if (objects.length > 0)
			{
				result = new HashSet<UserAgentElement>();

				for (Object value : objects)
				{
					if (value instanceof Map)
					{
						UserAgentElement userAgent = UserAgentElement.createUserAgentElement((Map<?, ?>) value);

						result.add(userAgent);
					}
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		this.setName(StringUtil.getStringValue(object.get(NAME_PROPERTY)));
		this.setDescription(StringUtil.getStringValue(object.get(DESCRIPTION_PROPERTY)));
		this._sinceList = IndexUtil.createList(object.get(SINCE_PROPERTY), SinceElement.class);
		this.setIsDeprecated(Boolean.TRUE == object.get(DEPRECATED_PROPERTY)); // $codepro.audit.disable useEquals

		Object userAgentsProperty = object.get(USER_AGENTS_PROPERTY);

		if (userAgentsProperty == null)
		{
			this._userAgents = ALL_USER_AGENTS;
		}
		else
		{
			this._userAgents = createUserAgentSet(userAgentsProperty);
		}
	}
	
	/**
	 * isDeprecated
	 * 
	 * @return
	 */
	public boolean isDeprecated()
	{
		return this._deprecated;
	}

	/**
	 * setIsDeprecated
	 * 
	 * @param value
	 */
	public void setIsDeprecated(boolean value)
	{
		this._deprecated = value;
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
		Set<UserAgentElement> userAgents;

		if (_userAgents == ALL_USER_AGENTS)
		{
			userAgents = new HashSet<UserAgentElement>();

			for (IUserAgent userAgent : CorePlugin.getDefault().getUserAgentManager().getAllUserAgents())
			{
				userAgents.add(UserAgentElement.createUserAgentElement(userAgent.getID()));
			}
		}
		else
		{
			userAgents = _userAgents;
		}

		return new ArrayList<UserAgentElement>(CollectionsUtil.getSetValue(userAgents));
	}

	/**
	 * A predicate used to determine if this element has been tagged to use all user agents. Note that this will return
	 * true only if setHasAllUserAgents has been called previously. If user agents have been added to this element and
	 * they so happen to be equivalent to a set of all user agents, this method will still return false.
	 * 
	 * @return
	 */
	public boolean hasAllUserAgents()
	{
		return _userAgents == ALL_USER_AGENTS;
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
	 * setHasAllUserAgents
	 */
	public void setHasAllUserAgents()
	{
		if (_userAgents != ALL_USER_AGENTS)
		{
			if (!CollectionsUtil.isEmpty(_userAgents))
			{
				Set<String> userAgentPlatforms = new HashSet<String>();

				// get current list of associated user agents
				for (UserAgentElement ua : getUserAgents())
				{
					userAgentPlatforms.add(ua.getPlatform());
				}

				// tag element as using all user agents
				_userAgents = ALL_USER_AGENTS;

				// remove new list of associate user agents
				for (UserAgentElement ua : getUserAgents())
				{
					userAgentPlatforms.remove(ua.getPlatform());
				}

				if (!userAgentPlatforms.isEmpty())
				{
					// @formatter:off
					String message = MessageFormat.format(
						"Setting element to use all user agents deletes the following associated user agents: {0}\nElement : {1}", //$NON-NLS-1$
						StringUtil.join(", ", userAgentPlatforms), //$NON-NLS-1$
						toSource()
					);
					// @formatter:on

					IdeLog.logWarning(JSCorePlugin.getDefault(), message);
				}
			}

			this._userAgents = ALL_USER_AGENTS;
		}
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
	 * @see com.aptana.jetty.util.epl.ajax.JSON.Convertible#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		out.add(NAME_PROPERTY, this.getName());
		// TODO To shrink string size, don't write out empty descriptions?
		out.add(DESCRIPTION_PROPERTY, this.getDescription());
		out.add(SINCE_PROPERTY, this.getSinceList());
		out.add(DEPRECATED_PROPERTY, this.isDeprecated());

		if (hasAllUserAgents())
		{
			// NOTE: use 'null' to indicate that all user agents should be associated with this element
			out.add(USER_AGENTS_PROPERTY, null);
		}
		else
		{
			out.add(USER_AGENTS_PROPERTY, this.getUserAgents());
		}
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter printer = new SourcePrinter();

		this.toSource(printer);

		return printer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		// Subclasses need to override this method
	}
}

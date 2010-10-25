package com.aptana.editor.css.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCSSMetadataElement implements ICSSMetadataElement
{

	private String _name;
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();
	private String _description;
	private String _example;

	public AbstractCSSMetadataElement()
	{
		super();
	}

	/**
	 * addUserAgent
	 * 
	 * @param userAgent
	 */
	public void addUserAgent(UserAgentElement userAgent)
	{
		this._userAgents.add(userAgent);
	}

	public String getDescription()
	{
		return this._description;
	}

	public String getExample()
	{
		return this._example;
	}

	public String getName()
	{
		return this._name;
	}

	public List<UserAgentElement> getUserAgents()
	{
		return this._userAgents;
	}

	public String[] getUserAgentNames()
	{
		String[] result = new String[this._userAgents.size()];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = this._userAgents.get(i).getPlatform();
		}

		return result;
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

}
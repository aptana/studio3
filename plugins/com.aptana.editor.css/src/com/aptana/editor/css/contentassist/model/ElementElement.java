package com.aptana.editor.css.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class ElementElement
{
	private String _displayName;
	private String _name;
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();
	private String _description;
	private String _example;
	private List<String> _properties = new LinkedList<String>();
	private String _remark;

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
	}

	/**
	 * addProperty
	 * 
	 * @param name
	 */
	public void addProperty(String name)
	{
		this._properties.add(name);
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

	/**
	 * getDescription;
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getFullName
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return this._displayName;
	}

	/**
	 * getExample
	 * 
	 * @return
	 */
	public String getExample()
	{
		return this._example;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<String> getProperties()
	{
		return this._properties;
	}

	/**
	 * getRemark
	 * 
	 * @return
	 */
	public String getRemark()
	{
		return this._remark;
	}

	/**
	 * getUserAgents
	 * 
	 * @return
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return this._userAgents;
	}
	
	/**
	 * getUserAgentNames
	 * 
	 * @return
	 */
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
	 * setFullName
	 * 
	 * @param name
	 */
	public void setDisplayName(String name)
	{
		this._displayName = name;
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

	/**
	 * setRemark
	 * 
	 * @param remark
	 */
	public void setRemark(String remark)
	{
		this._remark = remark;
	}
}

package com.aptana.editor.css.model;

import java.util.LinkedList;
import java.util.List;

public class ElementElement
{
	private String _name;
	private String _displayName;
	private String _description;
	private String _example;
	private String _remark;
	private List<String> _properties = new LinkedList<String>();
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();

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
	public String[] getProperties()
	{
		return this._properties.toArray(new String[this._properties.size()]);
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
	public UserAgentElement[] getUserAgents()
	{
		return this._userAgents.toArray(new UserAgentElement[this._userAgents.size()]);
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

package com.aptana.editor.js.model;

import java.util.LinkedList;
import java.util.List;

public class BaseElement
{
	private String _name;
	private String _description;
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();
	private List<SinceElement> _sinceList = new LinkedList<SinceElement>();
	
	/**
	 * BaseElement
	 */
	public BaseElement()
	{
	}
	
	/**
	 * addSince
	 * 
	 * @param since
	 */
	public void addSince(SinceElement since)
	{
		this._sinceList.add(since);
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
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this._description;
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
	 * getSinceList
	 * 
	 * @return
	 */
	public SinceElement[] getSinceList()
	{
		return this._sinceList.toArray(new SinceElement[this._sinceList.size()]);
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
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}
}

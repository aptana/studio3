package com.aptana.editor.js.contentassist.model;

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
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}
}

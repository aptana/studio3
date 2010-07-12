package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;

public class BaseElement
{
	private String _name;
	private String _description;
	private List<UserAgentElement> _userAgents = new ArrayList<UserAgentElement>();
	private List<SinceElement> _sinceList = new ArrayList<SinceElement>();
	private List<String> _documents = new ArrayList<String>();
	
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
	 * getDocuments
	 * 
	 * @return
	 */
	public List<String> getDocuments()
	{
		return this._documents;
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
	public List<SinceElement> getSinceList()
	{
		return this._sinceList;
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

package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseElement
{
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
		List<String> result = this._documents;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
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
		List<SinceElement> result = this._sinceList;
		
		if (result == null)
		{
			result = Collections.emptyList();
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
		List<UserAgentElement> result = this._userAgents;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * getUserAgentNames
	 * 
	 * @return
	 */
	public List<String> getUserAgentNames()
	{
		List<String> result;
		
		if (this._userAgents != null)
		{
			result = new ArrayList<String>(this._userAgents.size());
			
			for (UserAgentElement userAgent : this._userAgents)
			{
				result.add(userAgent.getPlatform());
			}
		}
		else
		{
			result = Collections.emptyList();
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
		if (description != null)
		{
			this._description = description;
		}
	}
	
	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		if (name != null)
		{
			this._name = name;
		}
	}
}

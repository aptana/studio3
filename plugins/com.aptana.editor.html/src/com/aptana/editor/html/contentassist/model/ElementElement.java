package com.aptana.editor.html.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class ElementElement
{
	private String _name;
	private String _relatedClass;
	private String _displayName;
	private List<SpecificationElement> _specifications = new LinkedList<SpecificationElement>();
	private String _deprecated;
	private String _description;
	private String _example;
	private String _remark;
	private List<String> _references = new LinkedList<String>();
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();
	private List<String> _attributes = new LinkedList<String>(); // TODO:

	/**
	 * ElementElement
	 */
	public ElementElement()
	{
	}

	/**
	 * addAttribute
	 * 
	 * @param attribute
	 *            the attribute to add
	 */
	public void addAttribute(String attribute)
	{
		this._attributes.add(attribute);
	}

	/**
	 * @param reference
	 *            the reference to add
	 */
	public void addReference(String reference)
	{
		this._references.add(reference);
	}

	/**
	 * addSpecification
	 * 
	 * @param specification
	 *            the specification to add
	 */
	public void addSpecification(SpecificationElement specification)
	{
		this._specifications.add(specification);
	}

	/**
	 * @param userAgent
	 *            the userAgents to add
	 */
	public void addUserAgent(UserAgentElement userAgent)
	{
		this._userAgents.add(userAgent);
	}

	/**
	 * getAttributes
	 * 
	 * @return the attributes
	 */
	public List<String> getAttributes()
	{
		return this._attributes;
	}

	/**
	 * getDeprecated
	 * 
	 * @return the deprecated
	 */
	public String getDeprecated()
	{
		return this._deprecated;
	}

	/**
	 * getDescription
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getDisplayName
	 * 
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return this._displayName;
	}

	/**
	 * getExample
	 * 
	 * @return the example
	 */
	public String getExample()
	{
		return this._example;
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getReferences
	 * 
	 * @return the references
	 */
	public List<String> getReferences()
	{
		return this._references;
	}

	/**
	 * getRelatedClass
	 * 
	 * @return the relatedClass
	 */
	public String getRelatedClass()
	{
		return this._relatedClass;
	}

	/**
	 * getRemark
	 * 
	 * @return the remark
	 */
	public String getRemark()
	{
		return this._remark;
	}

	/**
	 * getSpecifications
	 * 
	 * @return the specifications
	 */
	public List<SpecificationElement> getSpecifications()
	{
		return this._specifications;
	}

	/**
	 * @return the userAgents
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return this._userAgents;
	}

	/**
	 * setDeprecated
	 * 
	 * @param deprecated
	 *            the deprecated to set
	 */
	public void setDeprecated(String deprecated)
	{
		this._deprecated = deprecated;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setDisplayName
	 * 
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
	}

	/**
	 * setExample
	 * 
	 * @param example
	 *            the example to set
	 */
	public void setExample(String example)
	{
		this._example = example;
	}

	/**
	 * setName
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setRelatedClass
	 * 
	 * @param relatedClass
	 *            the relatedClass to set
	 */
	public void setRelatedClass(String relatedClass)
	{
		this._relatedClass = relatedClass;
	}

	/**
	 * setRemark
	 * 
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark)
	{
		this._remark = remark;
	}
}

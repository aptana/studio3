package com.aptana.editor.html.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class AttributeElement
{
	private String _name;
	private String _type;
	private List<SpecificationElement> _specifications = new LinkedList<SpecificationElement>();
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();
	private String _deprecated;
	private String _description;
	private String _hint;
	private List<String> _references = new LinkedList<String>();
	private String _remark;
	private List<ValueElement> _values = new LinkedList<ValueElement>();

	/**
	 * AttributeElement
	 */
	public AttributeElement()
	{
	}

	/**
	 * addReference
	 * 
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
	 * addUserAgent
	 * 
	 * @param userAgent
	 *            the userAgent to add
	 */
	public void addUserAgent(UserAgentElement userAgent)
	{
		this._userAgents.add(userAgent);
	}

	/**
	 * addValue
	 * 
	 * @param values
	 *            the value to add
	 */
	public void addValue(ValueElement value)
	{
		this._values.add(value);
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
	 * getHint
	 * 
	 * @return the hint
	 */
	public String getHint()
	{
		return this._hint;
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
	 * @return the references
	 */
	public List<String> getReferences()
	{
		return this._references;
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
	 * getType
	 * 
	 * @return the type
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * getUserAgents
	 * 
	 * @return the userAgents
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return this._userAgents;
	}

	/**
	 * getValues
	 * 
	 * @return the values
	 */
	public List<ValueElement> getValues()
	{
		return this._values;
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
	 * setHint
	 * 
	 * @param hint
	 *            the hint to set
	 */
	public void setHint(String hint)
	{
		this._hint = hint;
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
	 * setRemark
	 * 
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark)
	{
		this._remark = remark;
	}

	/**
	 * setType
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this._type = type;
	}
}

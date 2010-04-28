package com.aptana.editor.css.contentassist.model;

import java.util.LinkedList;
import java.util.List;


public class PropertyElement
{
	private String _name;
	private String _type;
	private List<SpecificationElement> _specifications = new LinkedList<SpecificationElement>();
	private String _description;
	private String _example;
	private String _remark;
	private List<UserAgentElement> _userAgents = new LinkedList<UserAgentElement>();
	private List<ValueElement> _values = new LinkedList<ValueElement>();
	private String _hint;

	/**
	 * PropertyElement
	 */
	public PropertyElement()
	{
	}

	/**
	 * addSpecification
	 * 
	 * @param specification
	 */
	public void addSpecification(SpecificationElement specification)
	{
		this._specifications.add(specification);
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
	 * addValue
	 * 
	 * @param value
	 */
	public void addValue(ValueElement value)
	{
		this._values.add(value);
	}

	/**
	 * getDescription;
	 */
	public String getDescription()
	{
		return this._description;
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
	 * getHint
	 * 
	 * @return
	 */
	public String getHint()
	{
		return this._hint;
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
	 * getRemark
	 * 
	 * @return
	 */
	public String getRemark()
	{
		return this._remark;
	}

	/**
	 * getSpecifications
	 * 
	 * @return
	 */
	public SpecificationElement[] getSpecifications()
	{
		return this._specifications.toArray(new SpecificationElement[this._specifications.size()]);
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public String getType()
	{
		return this._type;
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
	 * getValues
	 * 
	 * @return
	 */
	public ValueElement[] getValues()
	{
		return this._values.toArray(new ValueElement[this._values.size()]);
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
	 * setHint
	 * 
	 * @param hint
	 */
	public void setHint(String hint)
	{
		this._hint = hint;
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

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this._type = type;
	}
}

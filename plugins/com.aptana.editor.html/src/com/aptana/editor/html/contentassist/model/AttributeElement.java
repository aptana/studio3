/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;

public class AttributeElement extends BaseElement
{
	private static final String USER_AGENTS_PROPERTY = "userAgents"; //$NON-NLS-1$
	private static final String SPECIFICATIONS_PROPERTY = "specifications"; //$NON-NLS-1$
	private static final String REFERENCES_PROPERTY = "references"; //$NON-NLS-1$
	private static final String VALUES_PROPERTY = "values"; //$NON-NLS-1$
	private static final String DEPRECATED_PROPERTY = "deprecated"; //$NON-NLS-1$
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$
	private static final String HINT_PROPERTY = "hint"; //$NON-NLS-1$
	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$
	private static final String ELEMENT_PROPERTY = "element"; //$NON-NLS-1$

	private String _type;
	private String _element;
	private List<SpecificationElement> _specifications;
	private List<UserAgentElement> _userAgents;
	private String _deprecated;
	private String _hint;
	private List<String> _references;
	private String _remark;
	private List<ValueElement> _values;

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
		if (StringUtil.isEmpty(reference))
		{
			if (this._references == null)
			{
				this._references = new ArrayList<String>();
			}

			this._references.add(reference);
		}
	}

	/**
	 * addSpecification
	 * 
	 * @param specification
	 *            the specification to add
	 */
	public void addSpecification(SpecificationElement specification)
	{
		if (specification != null)
		{
			if (this._specifications == null)
			{
				this._specifications = new ArrayList<SpecificationElement>();
			}

			this._specifications.add(specification);
		}
	}

	/**
	 * addUserAgent
	 * 
	 * @param userAgent
	 *            the userAgent to add
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
	 * addValue
	 * 
	 * @param values
	 *            the value to add
	 */
	public void addValue(ValueElement value)
	{
		if (value != null)
		{
			if (this._values == null)
			{
				this._values = new ArrayList<ValueElement>();
			}

			this._values.add(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setElement(StringUtil.getStringValue(object.get(ELEMENT_PROPERTY)));
		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
		this.setHint(StringUtil.getStringValue(object.get(HINT_PROPERTY)));
		this.setRemark(StringUtil.getStringValue(object.get(REMARK_PROPERTY)));
		this.setDeprecated(StringUtil.getStringValue(object.get(DEPRECATED_PROPERTY)));

		this._values = IndexUtil.createList(object.get(VALUES_PROPERTY), ValueElement.class);
		this._references = IndexUtil.createList(object.get(REFERENCES_PROPERTY));
		this._specifications = IndexUtil.createList(object.get(SPECIFICATIONS_PROPERTY), SpecificationElement.class);
		this._userAgents = IndexUtil.createList(object.get(USER_AGENTS_PROPERTY), UserAgentElement.class);
	}

	/**
	 * getDeprecated
	 * 
	 * @return the deprecated
	 */
	public String getDeprecated()
	{
		return StringUtil.getStringValue(this._deprecated);
	}

	/**
	 * getElement
	 * 
	 * @return
	 */
	public String getElement()
	{
		return StringUtil.getStringValue(this._element);
	}

	/**
	 * getHint
	 * 
	 * @return the hint
	 */
	public String getHint()
	{
		return StringUtil.getStringValue(this._hint);
	}

	/**
	 * @return the references
	 */
	public List<String> getReferences()
	{
		return CollectionsUtil.getListValue(this._references);
	}

	/**
	 * getRemark
	 * 
	 * @return the remark
	 */
	public String getRemark()
	{
		return StringUtil.getStringValue(this._remark);
	}

	/**
	 * getSpecifications
	 * 
	 * @return the specifications
	 */
	public List<SpecificationElement> getSpecifications()
	{
		return CollectionsUtil.getListValue(this._specifications);
	}

	/**
	 * getType
	 * 
	 * @return the type
	 */
	public String getType()
	{
		return StringUtil.getStringValue(this._type);
	}

	/**
	 * getUserAgents
	 * 
	 * @return the userAgents
	 */
	public List<UserAgentElement> getUserAgents()
	{
		return CollectionsUtil.getListValue(this._userAgents);
	}

	/**
	 * getValues
	 * 
	 * @return the values
	 */
	public List<ValueElement> getValues()
	{
		return CollectionsUtil.getListValue(this._values);
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
	 * setElement
	 * 
	 * @param element
	 */
	public void setElement(String element)
	{
		this._element = element;
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

	/*
	 * (non-Javadoc)
	 * @see org.mortbay.util.ajax.JSON.Convertible#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(ELEMENT_PROPERTY, this.getElement());
		out.add(TYPE_PROPERTY, this.getType());
		out.add(HINT_PROPERTY, this.getHint());
		out.add(REMARK_PROPERTY, this.getRemark());
		out.add(DEPRECATED_PROPERTY, this.getDeprecated());

		out.add(VALUES_PROPERTY, this.getValues());
		out.add(REFERENCES_PROPERTY, this.getReferences());
		out.add(SPECIFICATIONS_PROPERTY, this.getSpecifications());
		out.add(USER_AGENTS_PROPERTY, this.getUserAgents());
	}
}

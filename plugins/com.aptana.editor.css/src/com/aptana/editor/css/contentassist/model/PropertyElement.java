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
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.StringUtil;

public class PropertyElement extends AbstractCSSMetadataElement
{
	private static final String SPECIFICATIONS_PROPERTY = "specifications"; //$NON-NLS-1$
	private static final String VALUES_PROPERTY = "values"; //$NON-NLS-1$
	private static final String ALLOW_MULTIPLE_VALUES_PROPERTY = "allowMultipleValues"; //$NON-NLS-1$
	private static final String HINT_PROPERTY = "hint"; //$NON-NLS-1$
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$

	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$
	private boolean _allowMultipleValues;
	private String _type;
	private List<SpecificationElement> _specifications = new ArrayList<SpecificationElement>();
	private String _hint;
	private String _remark;
	private List<ValueElement> _values = new ArrayList<ValueElement>();

	/**
	 * PropertyElement
	 */
	public PropertyElement()
	{
		super();
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
	 * addValue
	 * 
	 * @param value
	 */
	public void addValue(ValueElement value)
	{
		this._values.add(value);
	}

	/**
	 * allowMultipleValues
	 * 
	 * @return
	 */
	public boolean allowMultipleValues()
	{
		return this._allowMultipleValues;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.contentassist.model.AbstractCSSMetadataElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setType(object.get(TYPE_PROPERTY).toString());
		this.setRemark(object.get(REMARK_PROPERTY).toString());
		this.setHint(object.get(HINT_PROPERTY).toString());
		this.setAllowMultipleValues(Boolean.TRUE == object.get(ALLOW_MULTIPLE_VALUES_PROPERTY));

		// values
		Object values = object.get(VALUES_PROPERTY);

		if (values != null && values.getClass().isArray())
		{
			for (Object value : (Object[]) values)
			{
				if (value instanceof Map)
				{
					ValueElement v = new ValueElement();

					v.fromJSON((Map) value);

					this.addValue(v);
				}
			}
		}

		// specifications
		Object specifications = object.get(SPECIFICATIONS_PROPERTY);

		if (specifications != null && specifications.getClass().isArray())
		{
			for (Object specification : (Object[]) specifications)
			{
				if (specification instanceof Map)
				{
					SpecificationElement s = new SpecificationElement();

					s.fromJSON((Map) specification);

					this.addSpecification(s);
				}
			}
		}
	}

	/**
	 * getHint
	 * 
	 * @return
	 */
	public String getHint()
	{
		return StringUtil.getStringValue(this._hint);
	}

	/**
	 * getRemark
	 * 
	 * @return
	 */
	public String getRemark()
	{
		return StringUtil.getStringValue(this._remark);
	}

	/**
	 * getSpecifications
	 * 
	 * @return
	 */
	public List<SpecificationElement> getSpecifications()
	{
		return this._specifications;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public String getType()
	{
		return StringUtil.getStringValue(this._type);
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public List<ValueElement> getValues()
	{
		return this._values;
	}

	/**
	 * setAllowMultipleValues
	 * 
	 * @param value
	 */
	public void setAllowMultipleValues(Boolean value)
	{
		this._allowMultipleValues = value;
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

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.css.contentassist.model.AbstractCSSMetadataElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(TYPE_PROPERTY, this.getType());
		out.add(REMARK_PROPERTY, this.getRemark());
		out.add(HINT_PROPERTY, this.getHint());
		out.add(ALLOW_MULTIPLE_VALUES_PROPERTY, this.allowMultipleValues());
		out.add(VALUES_PROPERTY, this.getValues());
		out.add(SPECIFICATIONS_PROPERTY, this.getSpecifications());
	}
}

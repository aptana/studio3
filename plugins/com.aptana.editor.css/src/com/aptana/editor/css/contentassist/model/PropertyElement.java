/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.jetty.util.epl.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class PropertyElement extends BaseElement<PropertyElement.Property>
{
	enum Property implements IPropertyInformation<PropertyElement>
	{
		NAME(Messages.PropertyElement_NameLabel)
		{
			public Object getPropertyValue(PropertyElement node)
			{
				return node.getName();
			}
		};

		private String header;
		private String category;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		private Property(String header, String category)
		{
			this.category = category;
		}

		public String getCategory()
		{
			return category;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final String SPECIFICATIONS_PROPERTY = "specifications"; //$NON-NLS-1$
	private static final String VALUES_PROPERTY = "values"; //$NON-NLS-1$
	private static final String ALLOW_MULTIPLE_VALUES_PROPERTY = "allowMultipleValues"; //$NON-NLS-1$
	private static final String HINT_PROPERTY = "hint"; //$NON-NLS-1$
	private static final String REMARK_PROPERTY = "remark"; //$NON-NLS-1$
	private static final String TYPE_PROPERTY = "type"; //$NON-NLS-1$

	private boolean _allowMultipleValues;
	private String _type;
	private List<SpecificationElement> _specifications;
	private String _hint;
	private String _remark;
	private List<ValueElement> _values;

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
	 * addValue
	 * 
	 * @param value
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

		this.setType(StringUtil.getStringValue(object.get(TYPE_PROPERTY)));
		this.setRemark(StringUtil.getStringValue(object.get(REMARK_PROPERTY)));
		this.setHint(StringUtil.getStringValue(object.get(HINT_PROPERTY)));
		this.setAllowMultipleValues(Boolean.TRUE == object.get(ALLOW_MULTIPLE_VALUES_PROPERTY));

		this._values = IndexUtil.createList(object.get(VALUES_PROPERTY), ValueElement.class);
		this._specifications = IndexUtil.createList(object.get(SPECIFICATIONS_PROPERTY), SpecificationElement.class);
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

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
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
		return CollectionsUtil.getListValue(this._specifications);
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
		return CollectionsUtil.getListValue(this._values);
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
	 * com.aptana.editor.css.contentassist.model.AbstractCSSMetadataElement#toJSON(com.aptana.jetty.util.epl.ajax.JSON.Output)
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

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PropertyElement))
		{
			return false;
		}
		PropertyElement other = (PropertyElement) obj;
		return ObjectUtil.areEqual(getName(), other.getName());
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}
}

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

import com.aptana.core.util.ObjectUtil;
import com.aptana.index.core.IndexUtil;
import com.aptana.index.core.ui.views.IPropertyInformation;

public class PseudoElementElement extends BaseElement<PseudoElementElement.Property>
{
	enum Property implements IPropertyInformation<PseudoElementElement>
	{
		NAME(Messages.PseudoElementElement_NameLabel)
		{
			public Object getPropertyValue(PseudoElementElement node)
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
	private static final String ALLOW_PSEUDO_CLASS_SYNTAX_PROPERTY = "allowPseudoClassSyntax"; //$NON-NLS-1$

	private boolean _allowPseudoClassSyntax;
	private List<SpecificationElement> _specifications;

	/**
	 * PseudoElementElement
	 */
	public PseudoElementElement()
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
	 * allowPseudoClassSyntax
	 * 
	 * @return
	 */
	public boolean allowPseudoClassSyntax()
	{
		return _allowPseudoClassSyntax;
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

		this.setAllowPseudoClassSyntax(Boolean.TRUE == object.get(ALLOW_PSEUDO_CLASS_SYNTAX_PROPERTY));

		this._specifications = IndexUtil.createList(object.get(SPECIFICATIONS_PROPERTY), SpecificationElement.class);
	}

	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
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
	 * setAllowPseudoClassSyntax
	 * 
	 * @param allow
	 */
	public void setAllowPseudoClassSyntax(Boolean allow)
	{
		this._allowPseudoClassSyntax = allow;
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

		out.add(ALLOW_PSEUDO_CLASS_SYNTAX_PROPERTY, this.allowPseudoClassSyntax());
		out.add(SPECIFICATIONS_PROPERTY, this.getSpecifications());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PseudoElementElement))
		{
			return false;
		}
		PseudoElementElement other = (PseudoElementElement) obj;
		return ObjectUtil.areEqual(getName(), other.getName());
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}
}

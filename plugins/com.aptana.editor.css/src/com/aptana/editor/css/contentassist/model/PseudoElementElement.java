/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.index.core.IndexUtil;

public class PseudoElementElement extends BaseElement
{
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
	 * com.aptana.editor.css.contentassist.model.AbstractCSSMetadataElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(ALLOW_PSEUDO_CLASS_SYNTAX_PROPERTY, this.allowPseudoClassSyntax());
		out.add(SPECIFICATIONS_PROPERTY, this.getSpecifications());
	}
}

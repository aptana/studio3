package com.aptana.editor.css.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class PseudoElementElement extends AbstractCSSMetadataElement
{
	private boolean _allowPseudoClassSyntax;
	private List<SpecificationElement> _specifications = new LinkedList<SpecificationElement>();

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
		this._specifications.add(specification);
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

	public void setAllowPseudoClassSyntax(Boolean allow)
	{
		this._allowPseudoClassSyntax = allow;
	}

	public boolean allowPseudoClassSyntax()
	{
		return _allowPseudoClassSyntax;
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model.filters;

import com.aptana.scripting.model.AbstractBundleElement;
import com.aptana.scripting.model.AbstractElement;

public class ScopeFilter implements IModelFilter
{
	private String[] _scopes;
	
	/**
	 * ScopeFilter
	 * 
	 * @param scope
	 */
	public ScopeFilter(String scope)
	{
		this._scopes = new String[] { scope };
	}
	
	/**
	 * ScopeFilter
	 * 
	 * @param scopes
	 */
	public ScopeFilter(String[] scopes)
	{
		this._scopes = scopes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractElement)
	 */
	public boolean include(AbstractElement element)
	{
		boolean result = false;
		
		if (element instanceof AbstractBundleElement)
		{
			AbstractBundleElement abe = (AbstractBundleElement) element;
			
			result = abe.matches(this._scopes);
		}
		
		return result;
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

public class NameSelector implements ISelectorNode
{
	private String _name;
	private int matchLength = 0;
	
	/**
	 * NameSelector
	 * 
	 * @param name
	 */
	public NameSelector(String name)
	{
		this._name = name;
	}

	public int matchFragments()
	{
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		boolean result = false;
		
		if (context != null && this._name != null && this._name.length() > 0)
		{
			String step = context.getCurrentStep();
			
			if (step != null && step.startsWith(this._name))
			{
				// step matches as a prefix, now make sure we matched the whole step
				// or up to a period
				int nameLength = this._name.length();
				int scopeLength = step.length();
				
				if (scopeLength == nameLength || step.charAt(nameLength) == '.')
				{
					result = true;
					matchLength = nameLength;
					context.advance();
				}
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this._name;
	}

	public int matchLength()
	{
		return matchLength;
	}
}

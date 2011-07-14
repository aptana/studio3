/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.Stack;
import java.util.regex.Pattern;

public class MatchContext
{
	private static final Pattern spaces = Pattern.compile("\\s+"); //$NON-NLS-1$
	private String[] _steps;
	private int _currentIndex;
	private Stack<Integer> _savedPositions;
	
	/**
	 * MatchContext
	 * 
	 * @param scope
	 */
	MatchContext(String scope)
	{
		this._steps = (scope != null) ? spaces.split(scope) : new String[0];
		this._currentIndex = 0;
		this._savedPositions = new Stack<Integer>();
	}
	
	/**
	 * advance
	 */
	public void advance()
	{
		this._currentIndex++;
	}
	
	/**
	 * getCurrentStep
	 * 
	 * @return
	 */
	public String getCurrentStep()
	{
		String result = null;
		
		if (0 <= this._currentIndex && this._currentIndex < this._steps.length)
		{
			result = this._steps[this._currentIndex];
		}
		
		return result;
	}
	
	/**
	 * getLength
	 * 
	 * @return
	 */
	public int getLength()
	{
		return this._steps.length;
	}
	
	/**
	 * popCurrentStep
	 */
	public void popCurrentStep()
	{
		this.popCurrentStep(true);
	}
	
	/**
	 * popCurrentStep
	 * 
	 * @param restore
	 */
	public void popCurrentStep(boolean restore)
	{
		if (this._savedPositions.size() > 0)
		{
			int value = this._savedPositions.pop();
			
			if (restore)
			{
				this._currentIndex = value;
			}
		}
	}
	
	/**
	 * pushCurrentStep
	 */
	public void pushCurrentStep()
	{
		this._savedPositions.push(this._currentIndex);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return this.getCurrentStep();
	}
}

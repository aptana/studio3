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
		this._currentIndex = this._steps.length - 1;
		this._savedPositions = new Stack<Integer>();
	}

	/**
	 * Advance to the next step within the match context. This method does no bounds checking.
	 */
	public void advance()
	{
		this._currentIndex++;
	}

	/**
	 * Back up to the previous step within the match context. This method does no bounds checking.
	 */
	public void backup()
	{
		this._currentIndex--;
	}

	/**
	 * Determine if the match context has more steps from the current location.
	 * 
	 * @return
	 */
	public boolean canAdvance()
	{
		return _currentIndex < _steps.length - 1;
	}

	/**
	 * Return the currently active step within this match context. Returns null if the current step is undefined
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
	 * Return the number of steps within this context
	 * 
	 * @return
	 */
	public int getLength()
	{
		return this._steps.length;
	}

	/**
	 * Remove the last stored position and make that the current position within this match context.
	 */
	public void popCurrentStep()
	{
		this.popCurrentStep(true);
	}

	/**
	 * Remove the last stored position. If restore is true then make the restored position the current position within
	 * this match context.
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
	 * Save the currently active position for later retrieval. This uses a stack, so pushCurrentSteps should have
	 * matching popCurrentStep calls.
	 */
	public void pushCurrentStep()
	{
		this._savedPositions.push(this._currentIndex);
	}

	public String toString()
	{
		return this.getCurrentStep();
	}
}

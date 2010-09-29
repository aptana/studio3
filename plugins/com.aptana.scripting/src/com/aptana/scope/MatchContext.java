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

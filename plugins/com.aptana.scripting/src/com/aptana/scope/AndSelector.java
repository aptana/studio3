/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

public class AndSelector extends BinarySelector
{
	private int matchFragments = 0;
	private int matchLength;

	/**
	 * AndSelector
	 * 
	 * @param left
	 * @param right
	 */
	public AndSelector(ISelectorNode left, ISelectorNode right)
	{
		super(left, right);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		matchFragments = 0;
		matchLength = 0;
		boolean result = false;

		if (context != null && this._left != null && this._right != null)
		{
			context.pushCurrentStep();

			result = this._left.matches(context) && this._right.matches(context);
			if (result)
			{
				matchFragments = this._left.matchFragments() + this._right.matchFragments();
				matchLength = this._left.matchLength() + this._right.matchLength();
			}

			context.popCurrentStep(!result);
		}

		return result;
	}

	public int matchLength()
	{
		return matchLength;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.BinarySelector#getOperator()
	 */
	protected String getOperator()
	{
		return ""; //$NON-NLS-1$
	}

	public int matchFragments()
	{
		return matchFragments;
	}
}

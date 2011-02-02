/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

public class OrSelector extends BinarySelector
{
	private int matchFragments = 0;
	private int matchLength;

	/**
	 * OrSelector
	 * 
	 * @param left
	 * @param right
	 */
	public OrSelector(ISelectorNode left, ISelectorNode right)
	{
		super(left, right);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		boolean result = false;
		matchFragments = 0;
		matchLength = 0;

		if (context != null)
		{
			context.pushCurrentStep();

			if (this._left != null)
			{
				result = this._left.matches(context);
				if (result)
				{
					matchFragments = this._left.matchFragments();
					matchLength = this._left.matchLength();
				}

				if (result == false && this._right != null)
				{
					result = this._right.matches(context);
					if (result)
					{
						matchFragments = this._right.matchFragments();
						matchLength = this._right.matchLength();
					}
				}
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
		return ","; //$NON-NLS-1$
	}

	public int matchFragments()
	{
		return matchFragments;
	}
}

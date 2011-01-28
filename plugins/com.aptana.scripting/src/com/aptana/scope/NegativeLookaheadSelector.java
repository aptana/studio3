/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

public class NegativeLookaheadSelector extends BinarySelector
{
	private int matchLength;
	private int matchFragments;

	/**
	 * NegativeLookaheadSelector
	 * 
	 * @param left
	 * @param right
	 */
	public NegativeLookaheadSelector(ISelectorNode left, ISelectorNode right)
	{
		super(left, right);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		matchLength = 0;
		matchFragments = 0;
		boolean result = true;

		if (context != null && this._left != null && this._right != null)
		{
			context.pushCurrentStep();

			result = this._left.matches(context);

			if (result)
			{
				context.pushCurrentStep();

				result = (this._right.matches(context) == false);

				context.popCurrentStep();
			}
			
			if (result)
			{
				matchLength = this._left.matchLength();
				matchFragments = this._left.matchFragments();
			}

			context.popCurrentStep(!result);
		}

		return result;
	}

	public int matchFragments()
	{
		return matchFragments;
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
		return " -"; //$NON-NLS-1$
	}
}

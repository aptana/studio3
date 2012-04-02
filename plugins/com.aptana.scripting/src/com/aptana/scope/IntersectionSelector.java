/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.ArrayList;

public class IntersectionSelector extends BinarySelector
{
	/**
	 * IntersectionSelector
	 * 
	 * @param left
	 * @param right
	 */
	public IntersectionSelector(ISelectorNode left, ISelectorNode right)
	{
		super(left, right);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.BinarySelector#getOperator()
	 */
	protected String getOperator()
	{
		return " &"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
		matchResults = null;
		boolean result = false;

		if (context != null && this._left != null && this._right != null)
		{
			context.pushCurrentStep();

			if (this._left.matches(context))
			{
				matchResults = this._left.getMatchResults();

				if (matchResults.isEmpty())
				{
					matchResults = new ArrayList<Integer>();
				}

				if (this._right.matches(context))
				{
					// matched at current step, append match results
					matchResults.addAll(this._right.getMatchResults());
					result = true;
				}
				else
				{
					matchResults = null;
				}
			}

			context.popCurrentStep(!result);
		}

		return result;
	}
}

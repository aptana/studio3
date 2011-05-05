/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import java.util.Collections;
import java.util.List;

public class NegativeLookaheadSelector extends BinarySelector
{

	private List<Integer> matchResults;

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
		matchResults = null;
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
				matchResults = this._left.matchResults();
			}

			context.popCurrentStep(!result);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.BinarySelector#getOperator()
	 */
	protected String getOperator()
	{
		return " -"; //$NON-NLS-1$
	}

	public List<Integer> matchResults()
	{
		if (matchResults == null)
		{
			return Collections.emptyList();
		}
		return matchResults;
	}
}

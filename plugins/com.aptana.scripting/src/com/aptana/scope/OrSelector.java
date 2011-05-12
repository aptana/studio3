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

public class OrSelector extends BinarySelector
{
	private List<Integer> matchResults;

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
		matchResults = null;
		boolean result = false;

		if (context != null)
		{
			context.pushCurrentStep();

			if (this._left != null)
			{
				result = this._left.matches(context);
				if (result)
				{
					matchResults = this._left.matchResults();
				}

				if (result == false && this._right != null)
				{
					result = this._right.matches(context);
					if (result)
					{
						matchResults = this._right.matchResults();
					}
				}
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
		return ","; //$NON-NLS-1$
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

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
	private String operator;

	/**
	 * OrSelector
	 * 
	 * @param left
	 * @param right
	 */
	public OrSelector(ISelectorNode left, ISelectorNode right)
	{
		this(left, right, ","); //$NON-NLS-1$
	}

	/**
	 * OrSelector
	 * 
	 * @param left
	 * @param right
	 * @param operator
	 */
	public OrSelector(ISelectorNode left, ISelectorNode right, String operator)
	{
		super(left, right);

		this.operator = operator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scope.BinarySelector#getOperator()
	 */
	protected String getOperator()
	{
		if ("|".equals(operator)) //$NON-NLS-1$
		{
			return " |"; //$NON-NLS-1$
		}

		return ","; //$NON-NLS-1$
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
					matchResults = this._left.getMatchResults();
				}

				if (result == false && this._right != null)
				{
					result = this._right.matches(context);

					if (result)
					{
						matchResults = this._right.getMatchResults();
					}
				}
			}

			context.popCurrentStep(!result);
		}

		return result;
	}
}

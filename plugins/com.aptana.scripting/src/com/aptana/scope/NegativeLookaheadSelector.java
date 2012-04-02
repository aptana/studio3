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
	 * @see com.aptana.scope.BinarySelector#getOperator()
	 */
	protected String getOperator()
	{
		return " -"; //$NON-NLS-1$
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
			// save current context position in case of failure
			context.pushCurrentStep();

			// we have to match the left-hand side
			result = this._left.matches(context);

			// if we've matched so far, we have to make sure nothing to the right of the current position matches the
			// rhs, our lookahead
			if (result)
			{
				// save current position since lookaheads ultimately do not advance context position whether
				context.pushCurrentStep();

				// assume failure
				result = false;

				while (true)
				{
					// as long as our rhs doesn't match anything from the current position to the end of the scope, we
					// have a successful match for this node
					if (!this._right.matches(context))
					{
						// try the next context position; otherwise, we're done
						if (context.canAdvance())
						{
							context.advance();
						}
						else
						{
							result = true;
							break;
						}
					}
					else
					{
						// oops, we got a match, so this selector fails
						matchResults = null;
						break;
					}
				}

				// back up to where we were before we started testing the rhs
				context.popCurrentStep();
			}

			if (result)
			{
				matchResults = this._left.getMatchResults();
			}

			// restore original context position if matching failed
			context.popCurrentStep(!result);
		}

		return result;
	}
}

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
	 * @see com.aptana.scope.ISelectorNode#matches(com.aptana.scope.MatchContext)
	 */
	public boolean matches(MatchContext context)
	{
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
		return " -";
	}
}

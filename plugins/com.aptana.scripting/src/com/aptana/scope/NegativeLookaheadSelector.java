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
	
	@Override
	public int matchFragments()
	{
		return matchFragments;
	}
	
	@Override
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
		return " -";
	}
}

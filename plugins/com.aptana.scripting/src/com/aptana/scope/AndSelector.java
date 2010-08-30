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
		return "";
	}

	@Override
	public int matchFragments()
	{
		return matchFragments;
	}
}

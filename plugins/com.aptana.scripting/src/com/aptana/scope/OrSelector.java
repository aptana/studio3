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
		return ",";
	}

	@Override
	public int matchFragments()
	{
		return matchFragments;
	}
}

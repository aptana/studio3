package com.aptana.scope;

public class AndSelector extends BinarySelector
{
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
		boolean result = false;
		
		if (context != null && this._left != null && this._right != null)
		{
			context.pushCurrentStep();
			
			result = this._left.matches(context) && this._right.matches(context);
			
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
		return "";
	}
}

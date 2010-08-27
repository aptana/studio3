package com.aptana.scope;

public class OrSelector extends BinarySelector
{
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
		
		if (context != null)
		{
			context.pushCurrentStep();
			
			if (this._left != null)
			{
				result = this._left.matches(context);
				
				if (result == false && this._right != null)
				{
					result = this._right.matches(context);
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
		return ",";
	}
}

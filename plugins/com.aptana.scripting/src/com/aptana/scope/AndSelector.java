package com.aptana.scope;

public class AndSelector implements ISelectorNode
{
	ISelectorNode _left;
	ISelectorNode _right;

	/**
	 * AndSelector
	 * 
	 * @param left
	 * @param right
	 */
	public AndSelector(ISelectorNode left, ISelectorNode right)
	{
		this._left = left;
		this._right = right;
	}

	/**
	 * getLeftChild
	 * 
	 * @return
	 */
	public ISelectorNode getLeftChild()
	{
		return this._left;
	}
	
	/**
	 * getRightChild
	 * 
	 * @return
	 */
	public ISelectorNode getRightChild()
	{
		return this._right;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String left = (this._left == null) ? "null" : this._left.toString(); //$NON-NLS-1$
		String right = (this._right == null) ? "null" : this._right.toString(); //$NON-NLS-1$
		
		return left + " " + right; //$NON-NLS-1$
	}
}

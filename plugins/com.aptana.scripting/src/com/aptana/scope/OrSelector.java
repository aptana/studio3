package com.aptana.scope;

public class OrSelector implements ISelectorNode
{
	ISelectorNode _left;
	ISelectorNode _right;
	
	/**
	 * OrSelector
	 * 
	 * @param left
	 * @param right
	 */
	public OrSelector(ISelectorNode left, ISelectorNode right)
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
		
		context.pushCurrentStep();
		
		if (this._right != null)
		{
			result = this._right.matches(context);
			
			if (result == false && this._left != null)
			{
				result = this._left.matches(context);
			}
		}
		
		context.popCurrentStep(!result);
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this._left.toString() + ", " + this._right.toString(); //$NON-NLS-1$
	}
}

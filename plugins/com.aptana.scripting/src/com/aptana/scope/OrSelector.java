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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String left = (this._left == null) ? "null" : this._left.toString(); //$NON-NLS-1$
		String right = (this._right == null) ? "null" : this._right.toString(); //$NON-NLS-1$
		
		return left + ", " + right; //$NON-NLS-1$
	}
}

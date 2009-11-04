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
	 * @see com.aptana.scope.ISelectorNode#matches(java.lang.String)
	 */
	public boolean matches(String scope)
	{
		boolean result = false;
		
		if (this._left != null && this._right != null)
		{
			result = this._left.matches(scope) && this._right.matches(scope);
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
		return this._left.toString() + " " + this._right.toString();
	}
}

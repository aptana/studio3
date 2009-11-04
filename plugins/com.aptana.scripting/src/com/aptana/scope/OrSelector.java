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
	 * @see com.aptana.scope.ISelectorNode#matches(java.lang.String)
	 */
	public boolean matches(String scope)
	{
		return this._left.matches(scope) || this._right.matches(scope);
	}

	@Override
	public String toString()
	{
		return this._left.toString() + ", " + this._right.toString();
	}
}

package com.aptana.scope;

public abstract class BinarySelector implements ISelectorNode
{
	protected ISelectorNode _left;
	protected ISelectorNode _right;

	/**
	 * NegativeLookaheadSelector
	 * 
	 * @param left
	 * @param right
	 */
	public BinarySelector(ISelectorNode left, ISelectorNode right)
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

	/**
	 * getOperator
	 */
	protected abstract String getOperator();

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String left = (this._left == null) ? "null" : this._left.toString(); //$NON-NLS-1$
		String right = (this._right == null) ? "null" : this._right.toString(); //$NON-NLS-1$

		return left + this.getOperator() + " " + right; //$NON-NLS-1$
	}

}

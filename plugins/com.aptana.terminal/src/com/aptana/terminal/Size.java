package com.aptana.terminal;

public class Size
{
	public final double width;
	public final double height;
	
	/**
	 * Size
	 * 
	 * @param width
	 * @param height
	 */
	public Size(double width, double height)
	{
		this.width = width;
		this.height = height;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return Double.toString(this.width) + "," + Double.toString(this.height); //$NON-NLS-1$
	}
}

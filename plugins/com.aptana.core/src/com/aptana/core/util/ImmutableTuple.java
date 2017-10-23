package com.aptana.core.util;

import java.io.Serializable;

/**
 * Defines a tuple of some object, adding equals and hashCode operations All attributes in this class are final!
 * 
 * @author Fabio
 */
public final class ImmutableTuple<X, Y> implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final X first;
	public final Y second;

	public ImmutableTuple(X o1, Y o2)
	{
		this.first = o1;
		this.second = o2;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ImmutableTuple))
		{
			return false;
		}

		@SuppressWarnings("rawtypes")
		ImmutableTuple t2 = (ImmutableTuple) obj;
		if (first == t2.first && second == t2.second)
		{ // all the same
			return true;
		}

		if (first == null && t2.first != null)
		{
			return false;
		}
		if (second == null && t2.second != null)
		{
			return false;
		}
		if (first != null && t2.first == null)
		{
			return false;
		}
		if (second != null && t2.second == null)
		{
			return false;
		}

		if (!first.equals(t2.first))
		{
			return false;
		}
		if (!second.equals(t2.second))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		if (first != null && second != null)
		{
			return first.hashCode() * second.hashCode();
		}
		if (first != null)
		{
			return first.hashCode();
		}
		if (second != null)
		{
			return second.hashCode();
		}
		return 7;
	}

	@Override
	public String toString()
	{
		return StringUtil.concat(
				"Tuple [", first != null ? first.toString() : "null", " -- ", second != null ? second.toString() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						: "null", "]"); //$NON-NLS-1$//$NON-NLS-2$
	}
}

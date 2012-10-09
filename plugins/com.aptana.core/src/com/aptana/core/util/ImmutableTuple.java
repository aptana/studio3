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

	public final X o1;
	public final Y o2;

	public ImmutableTuple(X o1, Y o2)
	{
		this.o1 = o1;
		this.o2 = o2;
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
		if (o1 == t2.o1 && o2 == t2.o2)
		{ // all the same
			return true;
		}

		if (o1 == null && t2.o1 != null)
		{
			return false;
		}
		if (o2 == null && t2.o2 != null)
		{
			return false;
		}
		if (o1 != null && t2.o1 == null)
		{
			return false;
		}
		if (o2 != null && t2.o2 == null)
		{
			return false;
		}

		if (!o1.equals(t2.o1))
		{
			return false;
		}
		if (!o2.equals(t2.o2))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		if (o1 != null && o2 != null)
		{
			return o1.hashCode() * o2.hashCode();
		}
		if (o1 != null)
		{
			return o1.hashCode();
		}
		if (o2 != null)
		{
			return o2.hashCode();
		}
		return 7;
	}

	@Override
	public String toString()
	{
		return StringUtil.concat("Tuple [", o1 != null ? o1.toString() : "null", " -- ", o2 != null ? o2.toString() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				: "null", "]"); //$NON-NLS-1$//$NON-NLS-2$
	}
}

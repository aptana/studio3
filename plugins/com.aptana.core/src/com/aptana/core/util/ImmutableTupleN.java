/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

/**
 * Defines a tuple of objects, adding equals and hashCode operations. It's expected that any object added to this tuple
 * is immutable (properly implementing hashCode() and equals()).
 * 
 * @author Fabio
 */
public class ImmutableTupleN
{

	private final Object[] tuple;
	private int hash;

	public ImmutableTupleN(Object... tuple)
	{
		this.tuple = tuple;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ImmutableTupleN))
		{
			return false;
		}

		ImmutableTupleN t2 = (ImmutableTupleN) obj;
		if (t2.tuple.length != this.tuple.length)
		{
			return false;
		}
		for (int i = 0; i < tuple.length; i++)
		{
			Object o1 = tuple[i];
			Object o2 = t2.tuple[i];
			if (o1 != o2)
			{
				if (o1 == null || o2 == null)
				{
					return false;
				}
				if (!o1.equals(o2))
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		if (hash != 0)
		{
			return hash;
		}
		int ret = 1;
		int len = tuple.length;
		for (int i = 0; i < len; i++)
		{
			Object o = tuple[i];
			if (o == null)
			{
				ret += (i * 3);
			}
			else
			{
				int objHash = o.hashCode();
				ret += (i * 7 + (3 * objHash));
			}
		}
		hash = 7 + ret;
		return hash;
	}

	@Override
	public String toString()
	{
		return this.getClass().getName() + "[" + StringUtil.join(", ", tuple) + "]"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @return the size of this tuple.
	 */
	int size()
	{
		return this.tuple.length;
	}

	/**
	 * @param i
	 *            the position of the element we want to get at the tuple.
	 */
	public Object getAt(int i)
	{
		return this.tuple[i];
	}
}

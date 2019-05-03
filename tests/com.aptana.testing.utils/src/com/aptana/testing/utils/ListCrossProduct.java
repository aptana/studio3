/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.testing.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ListCrossProduct
 */
public class ListCrossProduct<T> implements Iterable<List<T>>
{
	private List<List<T>> lists = new ArrayList<List<T>>();

	class CrossProductIterator implements Iterator<List<T>>
	{
		private List<Integer> offsets;

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			boolean result = false;

			if (offsets == null)
			{
				result = true;
			}
			else
			{
				for (int offset : offsets)
				{
					if (offset > 0)
					{
						result = true;
						break;
					}
				}
			}

			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public List<T> next()
		{
			List<T> result = values();

			advance();

			return result;
		}

		public void advance()
		{
			for (int i = lists.size() - 1; i >= 0; i--)
			{
				// get next offset for list i
				int offset = offsets.get(i) + 1;

				if (offset > lists.get(i).size() - 1)
				{
					// we wrapped around, so reset offset and continue processing the carry
					offsets.set(i, 0);
				}
				else
				{
					// index is within range, save and stop processing
					offsets.set(i, offset);
					break;
				}
			}
		}

		public List<T> values()
		{
			if (offsets == null)
			{
				reset();
			}

			List<T> result = new ArrayList<T>();

			for (int i = 0; i < lists.size(); i++)
			{
				List<T> list = lists.get(i);
				int offset = offsets.get(i);

				result.add(list.get(offset));
			}

			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		public void reset()
		{
			offsets = new ArrayList<Integer>();

			for (int i = 0; i < lists.size(); i++)
			{
				offsets.add(0);
			}
		}
	}

	public void addList(List<T> list)
	{
		lists.add(list);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<List<T>> iterator()
	{
		return new CrossProductIterator();
	}
}

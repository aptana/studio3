/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.util.Collections;
import java.util.List;

/**
 * CategoryInfo
 */
public class CategoryInfo
{
	private final String name;
	public final int count;
	public final int minLength;
	public final int maxLength;
	public final long sum;
	public final int median;
	public final int average;

	CategoryInfo(String name, List<Integer> lengths)
	{
		this.name = name;

		// make sure we have some sort of list
		if (lengths == null)
		{
			lengths = Collections.emptyList();
		}

		// set size
		this.count = lengths.size();

		// find min, max, and sum of lengths
		int min = (count > 0) ? Integer.MAX_VALUE : 0;
		int max = (count > 0) ? Integer.MIN_VALUE : 0;
		long tmpSum = 0;

		for (int length : lengths)
		{
			min = Math.min(min, length);
			max = Math.max(max, length);
			tmpSum += length;
		}

		this.minLength = min;
		this.maxLength = max;
		this.sum = tmpSum;

		// find median
		if (count > 0)
		{
			Collections.sort(lengths);
			int mid = count / 2;
			int tmpMedian;

			if (count % 2 == 1 || lengths.size() == 1)
			{
				tmpMedian = lengths.get(mid);
			}
			else
			{
				tmpMedian = (lengths.get(mid - 1) + lengths.get(mid)) / 2;
			}

			this.median = tmpMedian;
		}
		else
		{
			this.median = 0;
		}

		// find average
		this.average = (count > 0) ? (int) (tmpSum / count) : 0;
	}
}

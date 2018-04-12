/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class ExcludeRegionList
{

	/**
	 * Defines the behavior that the writer should follow when hitting an excluded region.
	 */
	public static enum EXCLUDE_STRATEGY
	{
		SKIP, WRITE_AS_IS
	};

	private final List<IRegion> excludes = new ArrayList<IRegion>();
	private final Map<IRegion, EXCLUDE_STRATEGY> excludeActions = new HashMap<IRegion, ExcludeRegionList.EXCLUDE_STRATEGY>();

	public boolean isExcluded(int start, int end)
	{
		if (!excludes.isEmpty())
		{
			return findRegion(start, end) != null;
		}
		return false;
	}

	public EXCLUDE_STRATEGY getExcludeStrategy(int start, int end)
	{
		if (!excludes.isEmpty())
		{
			IRegion region = findRegion(start, end);
			if (region != null)
			{
				return excludeActions.get(region);
			}
		}
		return null;
	}

	private IRegion findRegion(int start, int end)
	{
		for (final IRegion region : excludes)
		{
			final int regionEnd = region.getOffset() + region.getLength();
			if (start <= regionEnd && region.getOffset() <= end)
			{
				return region;
			}
		}
		return null;
	}

	public IRegion[] selectValidRanges(int start, int end)
	{
		final List<Region> result = new ArrayList<Region>();
		for (final IRegion region : excludes)
		{
			final int regionEnd = region.getOffset() + region.getLength();
			if (start <= regionEnd && region.getOffset() <= end)
			{
				if (start < region.getOffset())
				{
					int validEnd = Math.min(end, region.getOffset());
					result.add(new Region(start, validEnd - start));
				}
				start = regionEnd;
				if (start > end)
				{
					break;
				}
			}
		}
		if (start < end)
		{
			result.add(new Region(start, end - start));
		}
		return result.toArray(new IRegion[result.size()]);
	}

	public List<IRegion> getExcludes()
	{
		return Collections.unmodifiableList(excludes);
	}

	public void excludeRegion(IRegion region, EXCLUDE_STRATEGY strategy)
	{
		int start = region.getOffset();
		int end = region.getOffset() + region.getLength();
		if (!excludes.isEmpty())
		{
			for (Iterator<IRegion> i = excludes.iterator(); i.hasNext();)
			{
				final IRegion r = i.next();
				final int rEnd = r.getOffset() + r.getLength();
				if (r.getOffset() <= end && start <= rEnd)
				{
					if (region.getOffset() >= r.getOffset() && region.getOffset() + region.getLength() <= rEnd)
					{
						// new region is inside one of the old regions
						return;
					}
					// calculate the surrounding bounds
					if (r.getOffset() < start)
					{
						start = r.getOffset();
					}
					if (rEnd > end)
					{
						end = rEnd;
					}
					i.remove();
				}
			}
		}
		// use input region or create the new one
		if (start == region.getOffset() && end == region.getOffset() + region.getLength())
		{
			excludes.add(region);
			excludeActions.put(region, strategy);
		}
		else
		{
			Region newRegion = new Region(start, end - start);
			excludes.add(newRegion);
			excludeActions.put(newRegion, strategy);
		}
		Collections.sort(excludes, REGION_COMPARATOR);
	}

	private static final Comparator<IRegion> REGION_COMPARATOR = new Comparator<IRegion>()
	{

		public int compare(IRegion o1, IRegion o2)
		{
			return o1.getOffset() - o2.getOffset();
		}

	};

}

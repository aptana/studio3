/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

/**
 * An implementation of a set non-overlapping segments of a line, as it referred in Computational Geometry.
 * The set holds segments and ensures that the set is consistent/non-overlapping at all times.
 * The operations included appending/removing a segment to/from the set.
 * 
 * @author Max Stepanov
 */
public final class Regions implements Iterable<IRegion> {

	private static final IRegion[] EMPTY = new IRegion[0];

	private static final Comparator<IRegion> COMPARATOR = new Comparator<IRegion>() {
		public int compare(IRegion o1, IRegion o2) {
			int diff = o1.getOffset() - o2.getOffset();
			if (diff == 0) {
				diff = o1.getLength() - o2.getLength();
			}
			return diff;
		}
	};
	private final SortedSet<IRegion> regions = new TreeSet<IRegion>(COMPARATOR);

	/**
	 * Default constructor
	 */
	public Regions() {
	}

	/**
	 * Construct from list of regions
	 */
	public Regions(IRegion... region) {
		append(region);
	}

	private void validate() {
		IRegion previous = null;
		for (Iterator<IRegion> i = iterator(); i.hasNext();) {
			IRegion current = i.next();
			if (current.getLength() == 0) {
				throw new IllegalStateException("Zero length region found"); //$NON-NLS-1$
			}
			if (previous != null && overlap(previous, current)) {
				throw new IllegalStateException("Overlapping regions found"); //$NON-NLS-1$
			}
			previous = current;
		}
	}

	public Iterator<IRegion> iterator() {
		return regions.iterator();
	}

	/**
	 * Returns true if the region set is empty
	 * @return
	 */
	public boolean isEmpty() {
		return regions.isEmpty();
	}

	/**
	 * Clears the region set
	 */
	public void clear() {
		regions.clear();
	}

	/**
	 * Append regions
	 * 
	 * @param region
	 */
	public void append(IRegion... region) {
		for (IRegion r : region) {
			Assert.isLegal(r.getLength() >= 0, "Negative region length"); //$NON-NLS-1$
			if (r.getLength() == 0) {
				continue;
			}
			boolean hasOverlaps;
			do {
				hasOverlaps = false;
				IRegion left = NavigableSetFloor(regions, r);
				IRegion right = NavigableSetCeiling(regions, r);
				if (overlap(left, r)) {
					regions.remove(left);
					r = merge(left, r);
					hasOverlaps = true;
				}
				if (overlap(r, right)) {
					regions.remove(right);
					r = merge(r, right);
					hasOverlaps = true;
				}
			} while (hasOverlaps);
			regions.add(r);
		}
		validate();
	}

	/**
	 * Exclude specified regions
	 */
	public void remove(IRegion... region) {
		for (IRegion r : region) {
			Assert.isLegal(r.getLength() >= 0, "Negative region length"); //$NON-NLS-1$
			IRegion from = new Region(r.getOffset(), Integer.MAX_VALUE);
			IRegion floor = NavigableSetFloor(regions, from);
			List<IRegion> list = new ArrayList<IRegion>(NavigableSetTailSet(regions, floor != null ? floor : from, true));
			for (IRegion current : list) {
				if (overlap(current, r)) {
					regions.remove(current);
					IRegion[] parts = substruct(current, r);
					if (parts.length > 0) {
						regions.addAll(Arrays.asList(parts));
					} else {
						break;
					}
				}

			}
		}
		validate();
	}

	/**
	 * Returns the region which is the overlap of provided region with current region set
	 * In case of multiple matches, the first overlap region is returned.
	 * @param region
	 * @return
	 */
	public IRegion overlap(IRegion region) {
		Assert.isLegal(region.getLength() >= 0, "Negative region length"); //$NON-NLS-1$
		IRegion from = new Region(region.getOffset(), Integer.MAX_VALUE);
		IRegion floor = NavigableSetFloor(regions, from);
		for (IRegion current : NavigableSetTailSet(regions, floor != null ? floor : from, true)) {
			IRegion overlap = intersection(current, region);
			if (overlap != null) {
				return overlap;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Regions ").append(regions).toString(); //$NON-NLS-1$
	}

	private static boolean overlap(IRegion o1, IRegion o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		int offset1 = o1.getOffset();
		int offset2 = o2.getOffset();
		if (offset1 == offset2) {
			return true;
		} else if (offset1 < offset2) {
			return offset1 + o1.getLength() >= offset2;
		} else {
			return offset2 + o2.getLength() >= offset1;
		}
	}

	private static IRegion merge(IRegion o1, IRegion o2) {
		int offset1 = o1.getOffset();
		int offset2 = o2.getOffset();
		int offset = Math.min(offset1, offset2);
		int length = Math.max(offset1 + o1.getLength(), offset2 + o2.getLength()) - offset;
		if (offset == offset1 && length == o1.getLength()) {
			return o1;
		}
		if (offset == offset2 && length == o2.getLength()) {
			return o2;
		}
		return new Region(offset, length);
	}

	private static IRegion[] substruct(IRegion target, IRegion part) {
		int leftLength = part.getOffset() - target.getOffset();
		int rightLength = target.getLength() - part.getLength() - leftLength;
		IRegion left = leftLength > 0 ? new Region(target.getOffset(), leftLength) : null;
		IRegion right = rightLength > 0 ? new Region(part.getOffset() + part.getLength(), rightLength) : null;
		if (left != null && right != null) {
			return new IRegion[] { left, right };
		} else if (left != null) {
			return new IRegion[] { left };
		} else if (right != null) {
			return new IRegion[] { right };
		} else {
			return EMPTY;
		}
	}

	private static IRegion intersection(IRegion o1, IRegion o2) {
		int offset1 = o1.getOffset();
		int offset2 = o2.getOffset();
		int offset = Math.max(offset1, offset2);
		int length = Math.min(offset1 + o1.getLength(), offset2 + o2.getLength()) - offset;
		if (length <= 0) {
			return null;
		}
		if (offset == offset1 && length == o1.getLength()) {
			return o1;
		}
		if (offset == offset2 && length == o2.getLength()) {
			return o2;
		}
		return new Region(offset, length);
	}

	/** Workaround for NavigatableSet interface which is @since java 1.6
	 */

	private static <T> T NavigableSetFloor(SortedSet<T> sortedSet, T e) {
		if (sortedSet.contains(e)) {
			return e;
		}
		SortedSet<T> headSet = sortedSet.headSet(e);
		if (headSet.isEmpty()) {
			return null;
		}
		return headSet.last();
	}

	private static <T> T NavigableSetCeiling(SortedSet<T> sortedSet, T e) {
		SortedSet<T> tailSet = sortedSet.tailSet(e);
		if (tailSet.isEmpty()) {
			return null;
		}
		return tailSet.first();
	}

	private static <T> SortedSet<T> NavigableSetTailSet(SortedSet<T> sortedSet, T fromElement, boolean inclusive) {
		Assert.isTrue(inclusive);
		return sortedSet.tailSet(fromElement);
	}

}

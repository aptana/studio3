/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility functions for set-like operations on collections
 */
public class CollectionsUtil
{
	/**
	 * Given a list of elements of type <T>, remove the duplicates from the list in place
	 * 
	 * @param <T>
	 * @param list
	 */
	public static <T> void removeDuplicates(final List<T> list)
	{
		final LinkedHashSet<T> set;

		set = new LinkedHashSet<T>(list);
		list.clear();
		list.addAll(set);
	}

	/**
	 * Given two collections of elements of type <T>, return a collection with the items which
	 * only appear in one collection or the other
	 * 
	 * @param <T> Type
	 * @param collection1 Collection #1
	 * @param collection2 Collection #2
	 * @return Collection with items unique to each list
	 */
	public static <T> Collection<T> getNonOverlapping(Collection<T> collection1, Collection<T> collection2)
	{
		Collection<T> result = union(collection1, collection2);
		result.removeAll(intersect(collection1, collection2));
		return result;
	}

	/**
	 * Given two collections of elements of type <T>, return a collection with the items which
	 * only appear in both lists
	 * 
	 * @param <T> Type
	 * @param collection1 Collection #1
	 * @param collection2 Collection #2
	 * @return Collection with items common to both lists
	 */
	public static <T> Collection<T> intersect(Collection<T> collection1, Collection<T> collection2)
	{
		Set<T> intersection = new HashSet<T>(collection1);
		intersection.retainAll(new HashSet<T>(collection2));
		return intersection;
	}

	/**
	 * Given two collections of elements of type <T>, return a collection containing the
	 * items from both lists
	 * 
	 * @param <T> Type
	 * @param collection1 Collection #1
	 * @param collection2 Collection #2
	 * @return Collection with items from both lists
	 */
	public static <T> Collection<T> union(Collection<T> collection1, Collection<T> collection2)
	{
		Set<T> union = new HashSet<T>(collection1);
		union.addAll(new HashSet<T>(collection2));
		return new ArrayList<T>(union);
	}

	/**
	 * This is a convenience method that returns true if the specified collection is null or empty
	 * 
	 * @param <T>
	 * @param collection
	 * @return
	 */
	public static <T> boolean isEmpty(Collection<T> collection)
	{
		return collection == null || collection.isEmpty();
	}

	/**
	 * This is a convenience method that essentially checks for a null list and returns Collections.emptyList in that
	 * case. If the list is non-null, then this is an identity function.
	 * 
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> List<T> getListValue(List<T> list)
	{
		List<T> result = list;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}
}

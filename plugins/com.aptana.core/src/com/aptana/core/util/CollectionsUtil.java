/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.ArrayList;
import java.util.Arrays;
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
	 * This is a convenience method that essentially checks for a null list and returns Collections.emptyList in that
	 * case. If the list is non-null, then this is an identity function.
	 * 
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> List<T> getListValue(List<T> list)
	{
		if (list == null)
		{
			return Collections.emptyList();
		}
		return list;
	}

	/**
	 * Given two collections of elements of type <T>, return a collection with the items which only appear in one
	 * collection or the other
	 * 
	 * @param <T>
	 *            Type
	 * @param collection1
	 *            Collection #1
	 * @param collection2
	 *            Collection #2
	 * @return Collection with items unique to each list
	 */
	public static <T> Collection<T> getNonOverlapping(Collection<T> collection1, Collection<T> collection2)
	{
		Collection<T> result = union(collection1, collection2);
		result.removeAll(intersect(collection1, collection2));
		return result;
	}

	/**
	 * Given two collections of elements of type <T>, return a collection with the items which only appear in both lists
	 * 
	 * @param <T>
	 *            Type
	 * @param collection1
	 *            Collection #1
	 * @param collection2
	 *            Collection #2
	 * @return Collection with items common to both lists
	 */
	public static <T> Collection<T> intersect(Collection<T> collection1, Collection<T> collection2)
	{
		Set<T> intersection = new HashSet<T>(collection1);
		intersection.retainAll(new HashSet<T>(collection2));
		return intersection;
	}

	/**
	 * This is a convenience method that returns true if the specified collection is null or empty
	 * 
	 * @param <T>
	 *            Any type of object
	 * @param collection
	 * @return
	 */
	public static <T> boolean isEmpty(Collection<T> collection)
	{
		return collection == null || collection.isEmpty();
	}

	/**
	 * Convert a list of items into a List. An empty list is returned if items is null
	 * 
	 * @param <T>
	 *            Any type of object
	 * @param items
	 *            A variable length list of items of type T
	 * @return Returns a new ArrayList<T> or an empty list
	 */
	public static final <T> List<T> newList(T... items)
	{
		if (items != null)
		{
			return new ArrayList<T>(Arrays.asList(items));
		}

		return Collections.emptyList();
	}

	/**
	 * Convert a list of items into a Set. An empty set is returned if items is null
	 * 
	 * @param <T>
	 *            Any type of object
	 * @param items
	 *            A variable length list of items of type T
	 * @return Returns a new HashSet<T> or an empty set
	 */
	public static final <T> Set<T> newSet(T... items)
	{
		if (items != null)
		{
			return new HashSet<T>(Arrays.asList(items));
		}

		return Collections.emptySet();
	}

	/**
	 * Given a list of elements of type <T>, remove the duplicates from the list in place
	 * 
	 * @param <T>
	 * @param list
	 */
	public static <T> void removeDuplicates(List<T> list)
	{
		// uses LinkedHashSet to keep the order
		Set<T> set = new LinkedHashSet<T>(list);
		list.clear();
		list.addAll(set);
	}

	/**
	 * Given two collections of elements of type <T>, return a collection containing the items from both lists
	 * 
	 * @param <T>
	 *            Type
	 * @param collection1
	 *            Collection #1
	 * @param collection2
	 *            Collection #2
	 * @return Collection with items from both lists
	 */
	public static <T> Collection<T> union(Collection<T> collection1, Collection<T> collection2)
	{
		Set<T> union = new HashSet<T>(collection1);
		union.addAll(new HashSet<T>(collection2));
		return new ArrayList<T>(union);
	}

	private CollectionsUtil()
	{
	}
}

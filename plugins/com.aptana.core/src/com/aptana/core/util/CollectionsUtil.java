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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.aptana.core.IFilter;

/**
 * Utility functions for set-like operations on collections
 */
public class CollectionsUtil
{
	/**
	 * Filter a source collection into a destination collection using a filter predicate. If the source or destination
	 * are null, then this is a no-op. If the filter is null, then all source items are added to the destination
	 * collection. Note that the destination collection has no requirements other than it must be a collection of
	 * matching type as the source. This allows the destination to be used, for example, as an accumulator. Note that
	 * this method is not thread safe, so users of this method will need to maintain type safety against the collections
	 * 
	 * @param source
	 *            A collection to filter
	 * @param destination
	 *            A collection to which unfiltered items in source are added
	 * @param filter
	 *            A filter that determines which items to add to the destination collection
	 */
	public static <T> void filter(Collection<T> source, Collection<T> destination, IFilter<? super T> filter)
	{
		if (source != null && destination != null)
		{
			if (filter != null)
			{
				for (T item : source)
				{
					if (filter.include(item))
					{
						destination.add(item);
					}
				}
			}
			else
			{
				destination.addAll(source);
			}
		}
	}

	/**
	 * Generate a new list containing items from the specified collection that the filter determines should be included.
	 * If the specified filter is null, then all items are added to the result list. If the specified collection is null
	 * then an empty list is returned. Note that this method is not thread safe, so users of this method will need to
	 * maintain type safety against the collection
	 * 
	 * @param collection
	 *            A collection to filter
	 * @param filter
	 *            A filter that determines which items to keep in the collection
	 * @return Returns a List<T> containing all non-filtered items from the collection
	 */
	public static <T> List<T> filter(Collection<T> collection, IFilter<T> filter)
	{
		ArrayList<T> result = new ArrayList<T>();

		filter(collection, result, filter);

		return result;
	}

	/**
	 * Filter a collection in place using a filter predicate. If the source or the filter are null, then this is a
	 * no-op. collection. Note that this method is not thread safe, so users of this method will need to maintain type
	 * safety against the collection. Also, not all collections support {@link Iterator#remove()} so it is possible that
	 * a UnsupportedOperationException can be thrown depending on the type of the collection
	 * 
	 * @param collection
	 *            A collection to filter
	 * @param filter
	 *            A filter that determines which items to keep in the source collection
	 */
	public static <T> void filterInPlace(Collection<T> collection, IFilter<? super T> filter)
	{
		if (collection != null && filter != null)
		{
			Iterator<T> iterator = collection.iterator();

			while (iterator.hasNext())
			{
				T item = iterator.next();

				if (!filter.include(item))
				{
					iterator.remove();
				}
			}
		}
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
	 * Convert a list of items into a Set while preserving the order. An empty set is returned if items is null.
	 * 
	 * @param <T>
	 *            Any type of object
	 * @param items
	 *            A variable length list of items of type T
	 * @return Returns a new LinkedHashSet<T> or an empty set
	 */
	public static final <T> Set<T> newInOrderSet(T... items)
	{
		if (items != null)
		{
			return new LinkedHashSet<T>(Arrays.asList(items));
		}
		return Collections.emptySet();
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

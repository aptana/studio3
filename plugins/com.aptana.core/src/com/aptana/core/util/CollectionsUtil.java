/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;

/**
 * Utility functions for set-like operations on collections
 */
public class CollectionsUtil
{
	/**
	 * Add a varargs list of items to the specified list. If the list or items array are null, then no action is
	 * performed. Note that the destination list has no requirements other than it must be a List of the source item's
	 * type. This allows the destination to be used, for example, as an accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * list.
	 * 
	 * @param list
	 *            A list to which items will be added
	 * @param items
	 *            A list of items to add
	 */
	public static final <T, U extends T> List<T> addToList(List<T> list, U... items)
	{
		if (list != null && items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				list.add(items[i]);
			}
			if (list instanceof ArrayList)
			{
				((ArrayList<T>) list).trimToSize();
			}
		}

		return list;
	}

	/**
	 * Add a varargs list of items to the specified list. If the list or items array are null, then no action is
	 * performed. Note that the destination list has no requirements other than it must be a List of the source item's
	 * type. This allows the destination to be used, for example, as an accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * list.
	 * 
	 * @param list
	 *            A list to which items will be added
	 * @param items
	 *            A list of items to add
	 */
	public static final <T, U extends T> List<T> addToList(List<T> list, List<U> items)
	{
		if (list != null && items != null)
		{
			list.addAll(items);
		}

		return list;
	}

	/**
	 * Converts a list to a new copy of array based on the start index and end index.
	 * 
	 * @param list
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	private static final <T> T[] toArray(List<T> list, int startIndex, int endIndex)
	{
		if (isEmpty(list))
		{
			return (T[]) list.toArray();
		}
		List<T> subList = list.subList(startIndex, endIndex);
		return (T[]) subList.toArray((T[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), subList.size()));
	}

	public static final <T> T[] toArray(List<T> list)
	{
		return toArray(list, 0, list.size());
	}

	/**
	 * Add a varargs list of items into a set. If the set or items are null then no action is performed. Note that the
	 * destination set has no requirements other than it must be a Set of the source item's type. This allows the
	 * destination to be used, for example, as an accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the set.
	 * 
	 * @param set
	 *            A set to which items will be added
	 * @param items
	 *            A list of items to add
	 */
	public static final <T, U extends T> Set<T> addToSet(Set<T> set, U... items)
	{
		if (set != null && items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				set.add(items[i]);
			}
		}

		return set;
	}

	/**
	 * Filter a source collection into a destination collection using a filter predicate. If the source or destination
	 * are null, then this is a no-op. If the filter is null, then all source items are added to the destination
	 * collection. Note that the destination collection has no requirements other than it must be a collection of
	 * matching type as the source. This allows the destination to be used, for example, as an accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * collections.
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
			if (destination instanceof ArrayList)
			{
				((ArrayList<T>) destination).trimToSize();
			}
		}
	}

	/**
	 * Searches the collection for the first element that matches the filter.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * collection
	 * 
	 * @param collection
	 *            A collection to search
	 * @param filter
	 *            A filter that determines which item to return
	 * @return Returns the first <T> that matches the filter
	 */
	public static <T> T find(Collection<T> collection, IFilter<T> filter)
	{
		if (collection != null && filter != null)
		{
			for (T item : collection)
			{
				if (filter.include(item))
				{
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Generate a new list containing items from the specified collection that the filter determines should be included.
	 * If the specified filter is null, then all items are added to the result list. If the specified collection is null
	 * then an empty list is returned.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * collection
	 * 
	 * @param collection
	 *            A collection to filter
	 * @param filter
	 *            A filter that determines which items to keep in the collection
	 * @return Returns a List<T> containing all non-filtered items from the collection
	 */
	public static <T> List<T> filter(Collection<T> collection, IFilter<T> filter)
	{
		ArrayList<T> result = new ArrayList<T>(collection == null ? 0 : collection.size());

		filter(collection, result, filter);

		return result;
	}

	/**
	 * Filter a collection in place using a filter predicate. If the source or the filter are null, then this is a
	 * no-op. collection.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * list.<br>
	 * <br>
	 * Note that not all collections support {@link Iterator#remove()} so it is possible that a
	 * {@link UnsupportedOperationException} can be thrown depending on the collection type
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
	 * This is a convenience method that essentially checks for a null set and returns Collections.emptySet in that
	 * case. If the set is non-null, then this is an identity function.
	 * 
	 * @param <T>
	 * @param set
	 * @return
	 */
	public static <T> Set<T> getSetValue(Set<T> set)
	{
		if (set == null)
		{
			return Collections.emptySet();
		}

		return set;
	}

	/**
	 * This is a convenience method to return the first element from a list. If the list is empty, then null is
	 * returned.
	 * 
	 * @param list
	 * @return
	 */
	public static <T> T getFirstElement(List<T> list)
	{
		if (!isEmpty(list))
		{
			return list.get(0);
		}

		return null;
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

		if (!isEmpty(result))
		{
			result.removeAll(intersect(collection1, collection2));
			if (result instanceof ArrayList)
			{
				((ArrayList<T>) result).trimToSize();
			}
		}

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
		if (isEmpty(collection1) || isEmpty(collection2))
		{
			return Collections.emptyList();
		}

		Set<T> intersection = new HashSet<T>(collection1);

		intersection.retainAll(collection2);

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
	 * This is a convenience method that returns true if the specified map is null or empty
	 * 
	 * @param <T>
	 *            any type of key
	 * @param <U>
	 *            any type of value
	 * @param map
	 * @return
	 */
	public static <T, U> boolean isEmpty(Map<T, U> map)
	{
		return map == null || map.isEmpty();
	}

	/**
	 * Transform the items of a collection to a new type and add to a specified collection. If source, destination, or
	 * mapper are null then no action is performed. Note that the destination collection has no requirements other than
	 * it must be a collection of map's destination type. This allows the destination to be used, for example, as an
	 * accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * collections.
	 * 
	 * @param source
	 *            The collection containing items to be transformed
	 * @param destination
	 *            A collection to which transformed items will be added
	 * @param mapper
	 *            The map that transforms items from their source type to their destination type
	 */
	public static <T, U> void map(Collection<T> source, Collection<U> destination, IMap<? super T, U> mapper)
	{
		if (source != null && destination != null && mapper != null)
		{
			for (T item : source)
			{
				U value = mapper.map(item);
				if (value != null)
				{
					destination.add(value);
				}
			}
			if (destination instanceof ArrayList)
			{
				((ArrayList<U>) destination).trimToSize();
			}
		}
	}

	/**
	 * Transform the items of a collection to a new type and add to a new list. If collection or mapper are null then no
	 * action is performed<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * collection.
	 * 
	 * @param collection
	 *            The collection containing items to be transformed
	 * @param mapper
	 *            The map that transforms items from their source type to their destination type
	 * @return
	 */
	public static <T, U> List<U> map(Collection<T> collection, IMap<? super T, U> mapper)
	{
		if (isEmpty(collection))
		{
			return Collections.emptyList();
		}

		List<U> result = new ArrayList<U>(collection.size());
		map(collection, result, mapper);
		return result;
	}

	/**
	 * Transform the items of an Iterator to a new type and add to a new list. If the iterator or mapper are null then
	 * no action is performed<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the
	 * items backing the iterator
	 * 
	 * @param iterator
	 *            The iterator containing items to be transformed
	 * @param mapper
	 *            The map to transforms items from their source type to their destination type
	 * @return
	 */
	public static <T, U> List<U> map(Iterator<T> iterator, IMap<? super T, U> mapper)
	{
		if (iterator == null || mapper == null)
		{
			return Collections.emptyList();
		}

		List<U> result = new ArrayList<U>();

		while (iterator.hasNext())
		{
			result.add(mapper.map(iterator.next()));
		}

		return result;
	}

	/**
	 * Convert a varargs list of items into a Set while preserving order. An empty set is returned if items is null.
	 * 
	 * @param <T>
	 *            Any type of object
	 * @param items
	 *            A variable length list of items of type T
	 * @return Returns a new LinkedHashSet<T> or an empty set
	 */
	public static final <T> Set<T> newInOrderSet(T... items)
	{
		return addToSet(new LinkedHashSet<T>(items != null ? items.length : 0), items);
	}

	/**
	 * Convert a vararg list of items into a List. An empty list is returned if items is null
	 * 
	 * @param <T>
	 *            Any type of object
	 * @param items
	 *            A variable length list of items of type T
	 * @return Returns a new ArrayList<T> or an empty list
	 */
	public static final <T> List<T> newList(T... items)
	{
		return addToList(new ArrayList<T>(items != null ? items.length : 0), items);
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
		return addToSet(new HashSet<T>(items != null ? items.length : 0), items);
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
	public static final <T> Map<T, T> newMap(T... items)
	{
		return addToMap(new HashMap<T, T>(items != null ? items.length / 2 : 0), items);
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
	public static final <T> Map<T, T> newInOrderMap(T... items)
	{
		return addToMap(new LinkedHashMap<T, T>(items != null ? items.length / 2 : 0), items);
	}

	/**
	 * Add a varargs list of items into a map. It is expected that items be in "key, value, key2, value2, etc.."
	 * ordering. If the map or items are null then no action is performed. Note that the destination map has no
	 * requirements other than it must be a Map of the source item's type. This allows the destination to be used, for
	 * example, as an accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the map.
	 * 
	 * @param keyType
	 *            The key type in the resulting map
	 * @param valueType
	 *            The value type in the resulting collection
	 * @param map
	 *            A map to which items will be added
	 * @param items
	 *            An interleaved list of keys and values
	 */
	static final <T, U> void addToMap(Class<T> keyType, Class<U> valueType, Map<T, U> map, Object... items)
	{
		if (keyType != null && valueType != null && map != null && !ArrayUtil.isEmpty(items))
		{
			if (items.length % 2 != 0)
			{
				throw new IllegalArgumentException("Length of list of items must be multiple of 2"); //$NON-NLS-1$
			}

			for (int i = 0; i < items.length; i += 2)
			{
				Object keyObject = items[i];
				T key;
				if (keyType.isAssignableFrom(keyObject.getClass()))
				{
					key = keyType.cast(keyObject);
				}
				else
				{
					// @formatter:off
					String message = MessageFormat.format(
						"Key {0} was not of the expected type: {1}", //$NON-NLS-1$
						i,
						keyType
					);
					// @formatter:on
					throw new IllegalArgumentException(message);
				}

				Object valueObject = items[i + 1];
				U value;
				if (valueType.isAssignableFrom(valueObject.getClass()))
				{
					value = valueType.cast(valueObject);

					map.put(key, value);
				}
				else
				{
					// @formatter:off
					String message = MessageFormat.format(
						"Value {0} was not of the expected type: {1}", //$NON-NLS-1$
						i + 1,
						valueType
					);
					// @formatter:on
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	/**
	 * Convert a list of items into a Map where the key and value types are specified. An empty map is returned if
	 * keyType, valueType, or items is null
	 * 
	 * @param keyType
	 *            The key type in the resulting map
	 * @param valueType
	 *            The value type in the resulting collection
	 * @param items
	 *            An interleaved list of keys and values
	 * @return Returns a new HashMap<T, U> or an empty map
	 */
	public static final <T, U> Map<T, U> newTypedMap(Class<T> keyType, Class<U> valueType, Object... items)
	{
		Map<T, U> result;

		if (keyType != null && valueType != null && !ArrayUtil.isEmpty(items))
		{
			result = new HashMap<T, U>();
			addToMap(keyType, valueType, result, items);
		}
		else
		{
			result = Collections.emptyMap();
		}

		return result;
	}

	/**
	 * Add a varargs list of items into a map. It is expected that items be in "key, value, key2, value2, etc.."
	 * ordering. If the map or items are null then no action is performed. Note that the destination map has no
	 * requirements other than it must be a Map of the source item's type. This allows the destination to be used, for
	 * example, as an accumulator.<br>
	 * <br>
	 * Note that this method is not thread safe. Users of this method will need to maintain type safety against the map.
	 * 
	 * @param map
	 *            A map to which items will be added
	 * @param items
	 *            A list of items to add
	 */
	static final <T, U extends T> Map<T, T> addToMap(Map<T, T> map, U... items)
	{
		if (map != null && items != null)
		{
			if (items.length % 2 != 0)
			{
				throw new IllegalArgumentException("Length of list of items must be multiple of 2"); //$NON-NLS-1$
			}
			for (int i = 0; i < items.length; i += 2)
			{
				map.put(items[i], items[i + 1]);
			}
		}

		return map;
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
		if (list instanceof ArrayList)
		{
			((ArrayList<T>) list).trimToSize();
		}
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
	public static <T> Collection<T> union(Collection<? extends T> collection1, Collection<? extends T> collection2)
	{
		if (isEmpty(collection1))
		{
			if (isEmpty(collection2))
			{
				// if both are empty, return empty list
				return Collections.emptyList();
			}
			// if just 1 is empty, return 2
			return new ArrayList<T>(collection2);
		}
		// at this point when know 1 is not empty
		if (isEmpty(collection2))
		{
			// so if 2 is, return 1.
			return new ArrayList<T>(collection1);
		}

		// we know both 1 and 2 aren't empty
		Set<T> union = new HashSet<T>(collection1.size() + collection2.size());

		union.addAll(collection1);
		union.addAll(collection2);

		return new ArrayList<T>(union);
	}

	private CollectionsUtil()
	{
	}

	/**
	 * Given a collection of items, we use an IMap to generate keys to associate, and then return the map from generated
	 * keys to the values used to generate them. Useful for generating maps from ids/names to a complex object for quick
	 * lookups as a cache 9whe what you have is just the set of objects).
	 * 
	 * @param values
	 * @param valueTokeyMapper
	 * @return
	 */
	public static <K, V> Map<K, V> mapFromValues(Collection<V> values, IMap<V, K> valueTokeyMapper)
	{
		if (isEmpty(values))
		{
			return Collections.emptyMap();
		}

		Map<K, V> map = new HashMap<K, V>(values.size());
		for (V value : values)
		{
			K key = valueTokeyMapper.map(value);
			if (key == null)
			{
				throw new IllegalStateException(MessageFormat.format(
						"Generated key for value {0} was null, which is not allowed.", value.toString())); //$NON-NLS-1$
			}
			map.put(key, value);
		}

		return map;
	}

	/**
	 * See http://ruby-doc.org/core-1.9.3/Enumerable.html#method-i-inject This is a method that operates over a
	 * collection, executing the block for each element and carrying along a "collector". This allows us to build up an
	 * object by operating on each element of a collection. Typically this would be used to generate sums of values, or
	 * concatenate a string, or run math operations.
	 * 
	 * @param collection
	 * @param collector
	 * @param block
	 * @return
	 */
	public static <K, V> K inject(Collection<V> collection, K collector, IInjectBlock<V, K> block)
	{
		if (isEmpty(collection))
		{
			return collector;
		}
		for (V item : collection)
		{
			collector = block.execute(collector, item);
		}
		return collector;
	}

	/**
	 * Partitions a collection by running the filtering operation over it. Any entries returning true will be put into
	 * the first element of the return list. Any entries returning false will be put into the second. <br>
	 * If the filter is empty, we return all results in the first value.<br>
	 * If the collection is empty or null, we return a tuple holding two empty lists.
	 * 
	 * @param collection
	 * @param filter
	 * @return
	 * @see http://ruby-doc.org/core-2.0/Enumerable.html#method-i-partition
	 */
	static <T> ImmutableTuple<List<T>, List<T>> partition(Collection<T> collection, IFilter<T> filter)
	{
		if (isEmpty(collection))
		{
			// return an empty tuple
			return new ImmutableTuple<List<T>, List<T>>(new ArrayList<T>(0), new ArrayList<T>(0));
		}

		if (filter == null)
		{
			return new ImmutableTuple<List<T>, List<T>>(new ArrayList<T>(collection), new ArrayList<T>(0));
		}

		// Assume even split for now
		int size = collection.size();
		ArrayList<T> trueResults = new ArrayList<T>(size / 2);
		ArrayList<T> falseResults = new ArrayList<T>(size / 2);

		for (T item : collection)
		{
			if (filter.include(item))
			{
				trueResults.add(item);
			}
			else
			{
				falseResults.add(item);
			}
		}
		// trim up the lists
		trueResults.trimToSize();
		falseResults.trimToSize();

		return new ImmutableTuple<List<T>, List<T>>(trueResults, falseResults);
	}

	public static int size(Collection<? extends Object> collection)
	{
		if (collection == null)
		{
			return 0;
		}
		return collection.size();
	}
}

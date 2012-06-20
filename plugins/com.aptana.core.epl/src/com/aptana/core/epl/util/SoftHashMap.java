/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A hash map with soft values (i.e.: values that are collected only when the vm gets out of space). Not synchronized
 * (must be done from the outside).
 * 
 * @author fabioz
 */
public final class SoftHashMap<Key, Val> extends AbstractMap<Key, Val>
{

	/**
	 * Create our own reference so that we can know the key that is garbage-collected.
	 */
	private final static class SoftValue<Key, Val> extends SoftReference<Val>
	{

		private final Key key;

		private SoftValue(Val value, Key key, ReferenceQueue<Val> queue)
		{
			super(value, queue);
			this.key = key;
		}
	}

	/**
	 * This is the internal map that contains our key->soft val references.
	 */
	private final Map<Key, SoftValue<Key, Val>> map = new HashMap<Key, SoftValue<Key, Val>>();

	/**
	 * Queue where we store the references. Used so that we clear the dead references from time to time.
	 */
	private ReferenceQueue<Val> queue = new ReferenceQueue<Val>();

	public SoftHashMap()
	{
	}

	public Val get(Object key)
	{
		Val res = null;
		SoftValue<Key, Val> sr = map.get(key);
		if (sr != null)
		{
			res = sr.get();
			if (res == null)
			{
				// Note that if the key is garbage-collected before the object it contains, it won't be even added
				// to the queue, but as we got it here, it means that it was probably already in the queue but
				// the queue still wasn't processed, meaning that processing the queue should do the trick of
				// removing it (and any other stale entries).
				removeStaleEntries();
			}
		}
		return res;
	}

	/**
	 * Remove the keys that were added to the queue (i.e.: the soft references that were garbage-collected should be
	 * removed).
	 */
	public void removeStaleEntries()
	{
		while (true)
		{
			@SuppressWarnings("unchecked")
			SoftValue<Key, Val> softValue = (SoftValue<Key, Val>) queue.poll();
			if (softValue != null)
			{
				SoftValue<Key, Val> curr = map.get(softValue.key);
				// Check if the map still has that reference... in theory, what could happen is that if a key
				// is added twice, the old one may still be in the queue (and thus, we'd be removing the new key)
				if (curr == softValue)
				{
					map.remove(softValue.key);
				}
			}
			else
			{
				return;
			}
		}
	}

	public Val put(Key key, Val value)
	{
		map.put(key, new SoftValue<Key, Val>(value, key, queue));
		return value;
	}

	/**
	 * Different from the map, it'll always return null here (never the object that was there as we don't want the
	 * overhead of getting it from the soft ref).
	 */
	public Val remove(Object key)
	{
		map.remove(key);
		return null;
	}

	public int size()
	{
		return map.size();
	}

	public void clear()
	{
		map.clear();
		// No need to poll the entries of the current queue, just create a new one and let the old be garbage-collected.
		queue = new ReferenceQueue<Val>();
	}

	// Not implemented...

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set entrySet()
	{
		throw new UnsupportedOperationException("Not implemented!"); //$NON-NLS-1$
	}

}
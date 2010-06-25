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

package com.aptana.filesystem.ftp.internal;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Max Stepanov
 *
 */
public class ExpiringMap<K, V> implements Map<K, V> {

	private LinkedHashMap<K, Item> map = new LinkedHashMap<K, Item>();
	private long maxObjectTTL;


	public ExpiringMap(long maxObjectTTL) {
		super();
		this.maxObjectTTL = maxObjectTTL;
	}
	
	private boolean hasExpired(Object key, Item item) {
		long now = System.currentTimeMillis();
		if (item.creationTime + maxObjectTTL < now) {
			map.remove(key);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		map.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key) {
		Item v = map.get(key);
		if (v != null && !hasExpired(key, v)) {
			return v.object;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<K> keySet() {
		return map.keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public V put(K key, V value) {
		Item v =  map.put(key, new Item(value));
		if (v != null) {
			return v.object;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends K, ? extends V> t) {
		Map<K, Item> all = new LinkedHashMap<K, Item>();
		for (Entry<? extends K, ? extends V> i : t.entrySet()) {
			all.put(i.getKey(), new Item(i.getValue()));
		}
		map.putAll(all);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public V remove(Object key) {
		Item v = map.remove(key);
		if (v != null && !hasExpired(key, v)) {
			return v.object;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	private class Item {
		
		protected V object;
		protected long creationTime;
		
		public Item(V object) {
			this.object = object;
			this.creationTime = System.currentTimeMillis();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ExpiringMap.Item) {
				return object == ((ExpiringMap.Item) obj).object
					|| (object != null && object.equals(((ExpiringMap.Item) obj).object));
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return object != null ? object.hashCode() : 0;
		}
	}
}

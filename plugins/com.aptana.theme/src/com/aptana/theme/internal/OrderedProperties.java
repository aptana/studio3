/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A special subclass of Properties intended to retain ordering of the properties.
 * 
 * @author cwilliams
 */
public class OrderedProperties extends Properties
{

	private final class MapEntry implements Map.Entry<Object, Object>
	{
		private final Object theKey;

		private MapEntry(Object theKey)
		{
			this.theKey = theKey;
		}

		public Object getKey()
		{
			return theKey;
		}

		public Object getValue()
		{
			return OrderedProperties.this.get(theKey);
		}

		public Object setValue(Object arg0)
		{
			return OrderedProperties.this.put(theKey, arg0);
		}
	}

	private static final long serialVersionUID = -3073105326009478275L;

	private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

	public synchronized Enumeration<Object> keys()
	{
		return Collections.<Object> enumeration(keys);
	}

	public synchronized Object put(Object key, Object value)
	{
		keys.add(key);
		return super.put(key, value);
	}

	public Set<Object> keySet()
	{
		return keys;
	}

	public Set<String> stringPropertyNames()
	{
		Set<String> set = new LinkedHashSet<String>();
		for (Object key : keySet())
		{
			set.add((String) key);
		}
		return set;
	}

	@Override
	public synchronized Object remove(Object key)
	{
		keys.remove(key);
		return super.remove(key);
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet()
	{
		// FIXME If we use entrySet() in a loop, this will recalc every iteration. Need to create it once and return the
		// instance!
		Set<java.util.Map.Entry<Object, Object>> entries = new LinkedHashSet<Map.Entry<Object, Object>>();
		for (Object key : keySet())
		{
			Map.Entry<Object, Object> entry = new MapEntry(key);
			entries.add(entry);
		}
		return entries;
	}
}
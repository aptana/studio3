package com.aptana.theme.internal;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

	public Enumeration<Object> keys()
	{
		return Collections.<Object> enumeration(keys);
	}

	public Object put(Object key, Object value)
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
		Set<java.util.Map.Entry<Object, Object>> entries = new LinkedHashSet<Map.Entry<Object, Object>>();
		for (Object key : keySet())
		{
			Map.Entry<Object, Object> entry = new MapEntry(key);
			entries.add(entry);
		}
		return entries;
	}
}
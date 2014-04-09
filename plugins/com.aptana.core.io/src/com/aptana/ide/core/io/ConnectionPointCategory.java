/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Max Stepanov
 */
/* package */class ConnectionPointCategory implements IConnectionPointCategory
{

	private final String id;
	private final String name;
	private final int order;
	private final boolean remote;
	private List<ConnectionPointType> types = new ArrayList<ConnectionPointType>();

	/**
	 * 
	 */
	protected ConnectionPointCategory(String id, String name, int order, boolean remote)
	{
		this.id = id;
		this.name = name;
		this.order = order;
		this.remote = remote;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if (order != ((ConnectionPointCategory) o).order)
		{
			return order - ((ConnectionPointCategory) o).order;
		}
		return name.compareTo(((ConnectionPointCategory) o).name);
	}

	/* package */void addType(ConnectionPointType type)
	{
		types.add(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.Identifiable#getId()
	 */
	public String getId()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointCategory#getName()
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPointCategory#getConnectionPoints()
	 */
	public IConnectionPoint[] getConnectionPoints()
	{
		List<IConnectionPoint> list = new ArrayList<IConnectionPoint>();
		for (ConnectionPointType type : types)
		{
			list.addAll(Arrays.asList(ConnectionPointManager.getInstance().getConnectionPointsForType(type.getType())));
		}
		return list.toArray(new IConnectionPoint[list.size()]);
	}

	public boolean isRemote()
	{
		return remote;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof ConnectionPointCategory))
		{
			return false;
		}
		ConnectionPointCategory other = (ConnectionPointCategory) obj;
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}
}

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
	public ConnectionPointCategory(String id, String name, int order)
	{
		this(id, name, order, false);
	}

	/**
	 * 
	 */
	public ConnectionPointCategory(String id, String name, int order, boolean remote)
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
}

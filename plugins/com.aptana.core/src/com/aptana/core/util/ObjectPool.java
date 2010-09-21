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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A basic object pool that checks expiration and validation on checkout of instances. Instances are not automatically
 * expired and cleaned via a thread, but instead are only checked on checkout. If validation can be costly this may slow
 * down checkout.
 * 
 * @author cwilliams
 * @param <T>
 */
public abstract class ObjectPool<T> implements IObjectPool<T>
{
	private static final int DEFAULT_EXPIRATION = 30000; // 30 seconds

	private long expirationTime;
	private Hashtable<T, Long> locked, unlocked;

	public ObjectPool(int expirationTime)
	{
		// TODO Allow way to force max pool size!
		this.expirationTime = expirationTime;
		locked = new Hashtable<T, Long>();
		unlocked = new Hashtable<T, Long>();
	}

	public ObjectPool()
	{
		this(DEFAULT_EXPIRATION);
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.util.IObjectPool#create()
	 */
	public abstract T create();

	/* (non-Javadoc)
	 * @see com.aptana.core.util.IObjectPool#validate(T)
	 */
	public abstract boolean validate(T o);

	/* (non-Javadoc)
	 * @see com.aptana.core.util.IObjectPool#expire(T)
	 */
	public abstract void expire(T o);

	/* (non-Javadoc)
	 * @see com.aptana.core.util.IObjectPool#checkOut()
	 */
	public synchronized T checkOut()
	{
		long now = System.currentTimeMillis();
		T t;
		if (unlocked.size() > 0)
		{
			Enumeration<T> e = unlocked.keys();
			while (e.hasMoreElements())
			{
				t = e.nextElement();
				// Allow for expiration time of -1, which means never expire!
				if (expirationTime != -1 && (now - unlocked.get(t)) > expirationTime)
				{
					// object has expired
					unlocked.remove(t);
					expire(t);
					t = null;
				}
				else
				{
					if (validate(t))
					{
						unlocked.remove(t);
						locked.put(t, now);
						return t;
					}
					// object failed validation
					unlocked.remove(t);
					expire(t);
					t = null;
				}
			}
		}
		// no objects available, create a new one
		t = create();
		if (t != null)
		{
			locked.put(t, now);
		}
		return t;
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.util.IObjectPool#checkIn(T)
	 */
	public synchronized void checkIn(T t)
	{
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.util.IObjectPool#cleanup()
	 */
	public synchronized void dispose()
	{
		for (T t : unlocked.keySet())
		{
			expire(t);
		}
		unlocked.clear();
		// TODO Also expire all the locked ones?
	}
}

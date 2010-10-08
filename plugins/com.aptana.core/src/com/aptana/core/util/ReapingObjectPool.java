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

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import com.aptana.core.CorePlugin;

class ConnectionReaper extends Thread
{

	private ReapingObjectPool<?> pool;
	private final long delay = 300000;

	boolean keepRunning = true;

	ConnectionReaper(ReapingObjectPool<?> pool)
	{
		this.pool = pool;
	}

	public void run()
	{
		while (keepRunning)
		{
			try
			{
				sleep(delay);
			}
			catch (InterruptedException e)
			{
			}
			pool.reap();
		}
		CorePlugin.trace("Reaping thread stopped"); //$NON-NLS-1$
	}
}

/**
 * An object pool that spawns off a reaper thread. This pool doesn't do any validation or expiration on checkout, it
 * solely manages a listing o locked and unlocked instances. The reaper manages testing expiration and validation of the
 * unlocked instances. This type of pool is handy when validation might be costly.
 * 
 * @author cwilliams
 * @param <T>
 */
public abstract class ReapingObjectPool<T> implements IObjectPool<T>
{

	private long expirationTime;
	private Hashtable<T, Long> locked, unlocked;
	private ConnectionReaper reaper;
	private int poolsize = 10;

	public ReapingObjectPool()
	{
		this(30000);
	}

	// TODO Enforce pool size!
	public ReapingObjectPool(int expirationTime)
	{
		this.expirationTime = expirationTime;
		this.locked = new Hashtable<T, Long>(poolsize);
		this.unlocked = new Hashtable<T, Long>(poolsize);
		if (expirationTime != -1)
		{
			// no need to reap if the instances can never expire.
			this.reaper = new ConnectionReaper(this);
			reaper.start();
		}
	}

	/**
	 * Expires all unlocked instances that have past expiration time and don't validate.
	 */
	public synchronized void reap()
	{
		long now = System.currentTimeMillis();
		Enumeration<T> e = unlocked.keys();
		while ((e != null) && (e.hasMoreElements()))
		{
			T t = e.nextElement();
			if ((expirationTime != -1 && (now - unlocked.get(t)) > expirationTime) && !validate(t))
			{
				unlocked.remove(t);
				expire(t);
				t = null;
			}
		}
	}

	/**
	 * Expires all unlocked instances, stops the reaper thread.
	 */
	public synchronized void dispose()
	{
		Enumeration<T> e = unlocked.keys();
		while ((e != null) && (e.hasMoreElements()))
		{
			T t = e.nextElement();
			unlocked.remove(t);
			expire(t);
		}
		if (locked != null && locked.size() > 0)
		{
			CorePlugin.logWarning(MessageFormat.format("Killed a connection pool that still has {0} locked items", locked.size())); //$NON-NLS-1$
		}
		try
		{
			// Kill the reaper
			this.reaper.keepRunning = false;
			this.reaper.interrupt();
		}
		catch (Exception e1)
		{
			// ignore
		}
	}

	public abstract void expire(T o);

	public abstract T create();

	public abstract boolean validate(T o);

	/**
	 * This simply looks for an "unlocked" instance and returns it. Otherwise it generates a new instance and returns
	 * that.
	 * 
	 * @return
	 */
	public synchronized T checkOut()
	{
		long now = System.currentTimeMillis();
		for (T c : unlocked.keySet())
		{
			unlocked.remove(c);
			locked.put(c, now);
			return c;
		}

		T c = create();
		locked.put(c, now);
		return c;
	}

	/**
	 * "Unlocks" this instance.
	 * 
	 * @param t
	 */
	public synchronized void checkIn(T t)
	{
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}
}
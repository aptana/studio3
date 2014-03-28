/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

class ConnectionReaper extends Thread
{

	private ReapingObjectPool<?> pool;
	private static final long delay = 300000;

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
		// Not logging the trace message until we have the verbosity to log at different levels
		// CorePlugin.trace("Reaping thread stopped"); //$NON-NLS-1$
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
	private ReapingObjectPool(int expirationTime)
	{
		this.expirationTime = expirationTime;
		this.locked = new Hashtable<T, Long>(poolsize);
		this.unlocked = new Hashtable<T, Long>(poolsize);
		if (expirationTime != -1)
		{
			// no need to reap if the instances can never expire.
			reaper = new ConnectionReaper(this);
		}
	}

	protected void start()
	{
		if (reaper != null)
		{
			reaper.start();
		}
	}

	/**
	 * Expires all unlocked instances that have past expiration time and don't validate.
	 */
	synchronized void reap()
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
			IdeLog.logWarning(CorePlugin.getDefault(),
					MessageFormat.format("Killed a connection pool that still has {0} locked items", locked.size())); //$NON-NLS-1$
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
		if (c == null)
		{
			return null;
		}
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
		if (t == null)
		{
			return;
		}
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}

	/**
	 * Returns the number of "available" items held in the pool (waiting to expire or get re-used).
	 * 
	 * @return
	 */
	protected int unlockedItems()
	{
		synchronized (unlocked)
		{
			return unlocked.size();
		}
	}
}
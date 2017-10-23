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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

public abstract class KeepAliveObjectPool<T> implements IObjectPool<T>
{

	private final List<T> locked;
	private final Map<T, Long> unlocked;
	private final ConnectionReaper reaper;
	private final int releaseTime;

	public KeepAliveObjectPool(int releaseTime)
	{
		this.releaseTime = releaseTime;
		locked = new ArrayList<T>();
		unlocked = new LinkedHashMap<T, Long>();

		reaper = new ConnectionReaper();
	}

	protected void start()
	{
		reaper.start();
	}

	public synchronized void checkIn(T t)
	{
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}

	public synchronized T checkOut()
	{
		// returns the first valid connection from the unused queue
		for (T c : unlocked.keySet())
		{
			if (validate(c))
			{
				unlocked.remove(c);
				locked.add(c);
				return c;
			}
		}

		// creates a new connection
		T c = create();
		locked.add(c);
		return c;
	}

	public synchronized void dispose()
	{
		for (T c : unlocked.keySet())
		{
			expire(c);
		}
		unlocked.clear();
		if (locked.size() > 0)
		{
			IdeLog.logWarning(CorePlugin.getDefault(),
					MessageFormat.format("Killed a connection pool that still has {0} locked items", locked.size())); //$NON-NLS-1$
			locked.clear();
		}
		reaper.exit();
	}

	protected synchronized void reap()
	{
		long now = System.currentTimeMillis();
		for (T c : unlocked.keySet())
		{
			if ((now - unlocked.get(c)) > timeToRelease())
			{
				// time to release the connection
				unlocked.remove(c);
				expire(c);
			}
			else
			{
				// keeps the connection alive unless it no longer validates
				if (!validate(c))
				{
					unlocked.remove(c);
					expire(c);
				}
			}
		}
	}

	private int timeToRelease()
	{
		int divider = ((unlocked.size() + locked.size()) ^ 2);
		return releaseTime / (divider > 0 ? divider : 1);
	}

	private class ConnectionReaper extends Thread
	{

		private static final long INTERVAL = 15000; // 15 seconds

		private boolean isRunning;

		public ConnectionReaper()
		{
			isRunning = true;
		}

		public void run()
		{
			while (isRunning)
			{
				try
				{
					sleep(INTERVAL);
				}
				catch (InterruptedException e)
				{
				}
				reap();
			}
		}

		public void exit()
		{
			isRunning = false;
		}
	}
}

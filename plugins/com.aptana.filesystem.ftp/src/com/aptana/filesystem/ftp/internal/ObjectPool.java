package com.aptana.filesystem.ftp.internal;

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class ObjectPool<T>
{
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
		this(30000); // 30 seconds
	}

	protected abstract T create();

	public abstract boolean validate(T o);

	public abstract void expire(T o);

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
				// TODO Allow for expiration time of -1, which means never expire!
				if ((now - unlocked.get(t)) > expirationTime)
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
						System.out.println("Returned existing " + t.getClass().getName());
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
		System.out.println("Created new " + t.getClass().getName());
		locked.put(t, now);
		return t;
	}

	public synchronized void checkIn(T t)
	{
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}
	
	public synchronized void cleanup()
	{
		for (T t : unlocked.keySet())
		{
			expire(t);
		}
		unlocked.clear();
		// TODO Also expire all the locked ones?		
	}
}

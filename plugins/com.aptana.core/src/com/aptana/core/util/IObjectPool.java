/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

public interface IObjectPool<T>
{

	/**
	 * Create a new instance of a managed object in the pool.
	 * 
	 * @return
	 */
	public T create();

	public boolean validate(T o);

	/**
	 * Expire the object. This means the object is "stale" in the pool, and the object should be disposed of.
	 * 
	 * @param o
	 */
	public void expire(T o);

	/**
	 * Ask for an instance of an item managed by the pool. When done, remember to {@link #checkIn(Object)}
	 * 
	 * @return
	 */
	public T checkOut();

	/**
	 * Return an object back to the pool.
	 * 
	 * @param t
	 */
	public void checkIn(T t);

	/**
	 * Cleans up the pool.
	 */
	public void dispose();

}
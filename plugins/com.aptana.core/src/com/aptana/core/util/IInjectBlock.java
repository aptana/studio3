/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

/**
 * A very generic interface used in the {@link CollectionsUtil#inject(java.util.Collection, Object, IInjectBlock)}
 * method. We create anonymous instances to act as lambda/blocks to be executed per-item in a collection.
 * 
 * @author cwilliams
 * @param <T>
 * @param <U>
 */
public interface IInjectBlock<T, U>
{
	/**
	 * @param collector
	 *            The object we collect results into.
	 * @param item
	 *            The item that is an element of the collection that we're currently operating on
	 * @return Returns the value we assign to the collector.
	 */
	U execute(U collector, T item);
}

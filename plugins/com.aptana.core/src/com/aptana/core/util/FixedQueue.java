/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Max Stepanov
 *
 */
public class FixedQueue<E> implements Queue<E> {

	private final int size;
	private final LinkedList<E> list = new LinkedList<E>();
	
	/**
	 * 
	 */
	public FixedQueue(int size) {
		this.size = size;
	}
	
	private void checkSize() {
		while (list.size() > size) {
			list.removeFirst();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return list.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return list.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator<E> iterator() {
		return list.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return list.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(E e) {
		try {
			return list.add(e);
		} finally {
			checkSize();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return list.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		try {
			return list.addAll(c);
		} finally {
			checkSize();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		list.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#offer(java.lang.Object)
	 */
	public boolean offer(E e) {
		try {
			return list.offer(e);
		} finally {
			checkSize();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#remove()
	 */
	public E remove() {
		return list.remove();
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#poll()
	 */
	public E poll() {
		return list.poll();
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#element()
	 */
	public E element() {
		return list.element();
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#peek()
	 */
	public E peek() {
		return list.peek();
	}

}

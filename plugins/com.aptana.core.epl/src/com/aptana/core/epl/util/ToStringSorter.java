/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.core.epl.util;

/**
 * The SortOperation takes a collection of objects and returns
 * a sorted collection of these objects. The sorting of these
 * objects is based on their toString(). They are sorted in
 * alphabetical order.
 */
public class ToStringSorter {
	Object[] sortedObjects;
	String[] sortedStrings;
/**
 *  Returns true if stringTwo is 'greater than' stringOne
 *  This is the 'ordering' method of the sort operation.
 */
public boolean compare(String stringOne, String stringTwo) {
	return stringOne.compareTo(stringTwo) < 0;
}
/**
 *  Sort the objects in sorted collection and return that collection.
 */
private void quickSort(int left, int right) {
	int originalLeft = left;
	int originalRight = right;
	int midIndex =  left + (right - left) / 2;
	String midToString = this.sortedStrings[midIndex];

	do {
		while (compare(this.sortedStrings[left], midToString))
			left++;
		while (compare(midToString, this.sortedStrings[right]))
			right--;
		if (left <= right) {
			Object tmp = this.sortedObjects[left];
			this.sortedObjects[left] = this.sortedObjects[right];
			this.sortedObjects[right] = tmp;
			String tmpToString = this.sortedStrings[left];
			this.sortedStrings[left] = this.sortedStrings[right];
			this.sortedStrings[right] = tmpToString;
			left++;
			right--;
		}
	} while (left <= right);

	if (originalLeft < right)
		quickSort(originalLeft, right);
	if (left < originalRight)
		quickSort(left, originalRight);
}
/**
 *  Return a new sorted collection from this unsorted collection.
 *  Sort using quick sort.
 */
public void sort(Object[] unSortedObjects, String[] unsortedStrings) {
	int size = unSortedObjects.length;
	this.sortedObjects = new Object[size];
	this.sortedStrings = new String[size];

	//copy the array so can return a new sorted collection
	System.arraycopy(unSortedObjects, 0, this.sortedObjects, 0, size);
	System.arraycopy(unsortedStrings, 0, this.sortedStrings, 0, size);
	if (size > 1)
		quickSort(0, size - 1);
}
}

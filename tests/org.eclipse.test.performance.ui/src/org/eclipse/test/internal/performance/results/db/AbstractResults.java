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
package org.eclipse.test.internal.performance.results.db;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.test.internal.performance.results.utils.Util;


/**
 * Abstract class to store performance results.
 *
 * Each results gives access to specific children depending on model.
 */
public abstract class AbstractResults implements Comparable {

	public static final double[] INVALID_RESULTS = new double[] {2};
	public static final double[] NO_BUILD_RESULTS = new double[0];
	public static final int BUILD_VALUE_INDEX = 0;
	public static final int BASELINE_VALUE_INDEX = 1;
	public static final int DELTA_VALUE_INDEX = 2;
	public static final int DELTA_ERROR_INDEX = 3;
	public static final int BUILD_ERROR_INDEX = 4;
	public static final int BASELINE_ERROR_INDEX = 5;
	public static final int NUMBERS_LENGTH = 6;

	AbstractResults parent;
	int id = -1;
	String name;
	List children;
	private static boolean NEW_LINE = true;
	PrintStream printStream = null;

AbstractResults(AbstractResults parent, String name) {
	this.parent = parent;
	this.children = new ArrayList();
	this.name = name;
}

AbstractResults(AbstractResults parent, int id) {
	this.parent = parent;
	this.children = new ArrayList();
	this.id = id;
}

/*
 * Add a child to current results, using specific sort
 * order if specified.
 */
void addChild(Comparable child, boolean sort) {
	if (sort) {
		int size = this.children.size();
		for (int i=0; i<size; i++) {
			Object results = this.children.get(i);
			if (child.compareTo(results) < 0) {
				this.children.add(i, child);
				return;
			}
		}
	}
	this.children.add(child);
}

/**
 * Compare the results to the given one using the name.
 *
 * @see java.lang.Comparable#compareTo(java.lang.Object)
 */
public int compareTo(Object obj) {
	if (obj instanceof AbstractResults) {
		AbstractResults res = (AbstractResults) obj;
		return getName().compareTo(res.getName());
	}
	return -1;
}

/**
 * Returns whether two results are equals using the name
 * to compare them.
 *
 * @param obj  The results to compare with
 * @return <code>true</code> if the name are equals,
 * 	<code>false</code> otherwise
 * @see java.lang.Comparable#compareTo(java.lang.Object)
 */
public boolean equals(Object obj) {
	if (obj instanceof AbstractResults) {
		return this.name.equals(((AbstractResults)obj).getName());
	}
	return super.equals(obj);
}

/**
 * Return an array built on the current results children list.
 *
 * @return An array of the children list
 */
public AbstractResults[] getChildren() {
	AbstractResults[] elements = new AbstractResults[size()];
	this.children.toArray(elements);
	return elements;
}

ComponentResults getComponentResults() {
	if (this.parent != null) {
		return this.parent.getComponentResults();
	}
	return null;
}

int getId() {
	return this.id;
}

/**
 * Returns the name of the results object.
 *
 * @return The name of the results
 */
public String getName() {
	return this.name;
}

/**
 * Returns the parent
 *
 * @return The parent
 */
public AbstractResults getParent() {
	return this.parent;
}

PerformanceResults getPerformance() {
	if (this.parent != null) {
		return this.parent.getPerformance();
	}
	return null;
}

String getPath() {
	String path = this.parent==null || this.parent.parent==null ? "" : this.parent.getPath() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	return path+this.name;
}

/**
 * Return the children list of the current results.
 *
 * @return An iterator on the children list
 */
public Iterator getResults() {
	return this.children.iterator();
}

AbstractResults getResults(String resultName) {
	int size = this.children.size();
	for (int i=0; i<size; i++) {
		AbstractResults searchedResults = (AbstractResults) this.children.get(i);
		if (searchedResults.getName().equals(resultName)) {
			return searchedResults;
		}
	}
	return null;
}

AbstractResults getResults(int searchedId) {
	int size = this.children.size();
	for (int i=0; i<size; i++) {
		AbstractResults searchedResults = (AbstractResults) this.children.get(i);
		if (searchedResults.id == searchedId) {
			return searchedResults;
		}
	}
	return null;
}

public int hashCode() {
	return this.name.hashCode();
}

void printTab() {
	if (this.parent != null) {
		if (this.printStream != null) this.printStream.print("\t"); //$NON-NLS-1$
		this.parent.printTab();
	}
}
void print(String text) {
	if (this.printStream != null) {
		if (NEW_LINE) printTab();
		this.printStream.print(text);
		NEW_LINE = false;
	}
}

void printGlobalTime(long start) {
	printGlobalTime(start, null);
}

void printGlobalTime(long start, String end) {
	long time = System.currentTimeMillis();
	String resultsName = getName();
	StringBuffer buffer;
	if (resultsName == null) {
		buffer = new StringBuffer(" => time spent was "); //$NON-NLS-1$
	} else {
		buffer = new StringBuffer(" => time spent in '"); //$NON-NLS-1$
		buffer.append(resultsName);
		buffer.append("' was "); //$NON-NLS-1$
	}
	buffer.append(Util.timeString(time-start));
	if (end != null) {
		buffer.append(". "); //$NON-NLS-1$
		buffer.append(end.trim());
	}
	println(buffer);
}

void println() {
	if (this.printStream != null) {
		this.printStream.println();
		NEW_LINE = true;
	}
}

void println(String text) {
	if (this.printStream != null) {
		if (NEW_LINE) printTab();
		this.printStream.println(text);
		NEW_LINE = true;
	}
}

void println(StringBuffer buffer) {
	println(buffer.toString());
}

public int size() {
	return this.children == null ? 0 : this.children.size();
}

public String toString() {
	return getPath();
}

}

/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.eval;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.test.internal.performance.data.Assert;
import org.eclipse.test.internal.performance.data.Dim;

/**
 * @since 3.1
 */
public abstract class AssertChecker {
	private Set fDimensions;
	
	public AssertChecker(Dim dimension) {
		this(new Dim[] {dimension});
	}
	
	public AssertChecker(Dim[] dimensions) {
		fDimensions= new HashSet();
		fDimensions.addAll(Arrays.asList(dimensions));
	}
	
	public Dim[] getDimensions() {
		return (Dim[]) fDimensions.toArray(new Dim[fDimensions.size()]);
	}

	protected Dim getDimension() {
		Assert.isTrue(fDimensions.size() == 1);
		return getDimensions()[0];
	}

	/**
	 * Evaluates the predicate.
	 * 
	 * @param reference statistics of dimensions of the reference metering session
	 * @param measured statistics of dimensions of the metering session to be tested
	 * @param message a message presented to the user upon failure
	 * @return <code>true</code> if the predicate passes, <code>false</code> if it fails
	 */
	public abstract boolean test(StatisticsSession reference, StatisticsSession measured, StringBuffer message);
}

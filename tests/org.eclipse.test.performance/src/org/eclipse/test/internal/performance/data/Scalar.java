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
package org.eclipse.test.internal.performance.data;


/**
 * @since 3.1
 */
public class Scalar {
	private Dim fDimension;
	private long fMagnitude;
	
	public Scalar(Dim dimension, long extent) {
		fDimension= dimension;
		fMagnitude= extent;
	}
	
	public Dim getDimension() {
		return fDimension;
	}
	
	public long getMagnitude() {
		return fMagnitude;
	}
	
	public String toString() {
		if (fDimension == null)
			return "Scalar [dimension= " + fDimension + ", magnitude= " + fMagnitude + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return "Scalar [" + fDimension.getName() + ": " + fDimension.getDisplayValue(this) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}

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

import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.Dim;

/**
 * @since 3.1
 */
public class RelativeBandChecker extends AssertChecker {

	private final double fLowerBand;
	private final double fUpperBand;

	public RelativeBandChecker(Dim dimension, double lowerBand, double upperBand) {
		super(dimension);
		fLowerBand= lowerBand;
		fUpperBand= upperBand;
	}

	public boolean test(StatisticsSession reference, StatisticsSession measured, StringBuffer message) {
		Dim dimension= getDimension();
		
		if (!measured.contains(dimension)) {
		    PerformanceTestPlugin.logWarning("collected data provides no dimension '"+dimension.getName()+'\''); //$NON-NLS-1$
			return true;
		}
		if (!reference.contains(dimension)) {
		    PerformanceTestPlugin.logWarning("reference data provides no dimension '"+dimension.getName()+'\''); //$NON-NLS-1$
			return true;
		}
		
		double actual= measured.getAverage(dimension);
		double test= reference.getAverage(dimension);
		
		if (test < 0.001 && test > -0.001) {
			// we don't fail for reference value of zero
		    PerformanceTestPlugin.logWarning("ref value for '"+dimension.getName()+"' is too small"); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		if (actual < 0) {
			// we don't fail for negative values
		    PerformanceTestPlugin.logWarning("actual value for '"+dimension.getName()+"' is negative"); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		if (actual > fUpperBand * test || actual < fLowerBand * test) {
			message.append('\n' + dimension.getName() + ": " + dimension.getDisplayValue(actual) + " is not within [" + Math.round(fLowerBand * 100)+ "%, " + Math.round(fUpperBand * 100) + "%] of " + dimension.getDisplayValue(test)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return false;
		}
		return true;
	}	
}

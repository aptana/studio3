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

import java.util.HashSet;
import junit.framework.Assert;

import org.eclipse.test.internal.performance.InternalPerformanceMeter;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Sample;
import org.eclipse.test.internal.performance.db.DB;
import org.eclipse.test.internal.performance.db.Variations;
import org.eclipse.test.performance.PerformanceMeter;

/**
 * The default implementation of an evaluator backed by a database.
 * @since 3.1
 */
public class Evaluator extends EmptyEvaluator {
	
	private AssertChecker[] fCheckers;


	public void setAssertCheckers(AssertChecker[] asserts) {
		fCheckers= asserts;
	}

	/*
	 * @see org.eclipse.test.internal.performance.eval.IEvaluator#evaluate(org.eclipse.jdt.ui.tests.performance.PerformanceMeter)
	 */
	public void evaluate(PerformanceMeter performanceMeter) throws RuntimeException {
	    
		if (fCheckers == null)
		    return;	// nothing to do
		
		// get reference build tag
		Variations refKeys= PerformanceTestPlugin.getAssertAgainst();
		if (refKeys == null)
		    return;	// nothing to do
		
	    if (!(performanceMeter instanceof InternalPerformanceMeter))
	        return;	// we cannot handle this.
	    
        InternalPerformanceMeter ipm= (InternalPerformanceMeter) performanceMeter;
	    Sample session= ipm.getSample();
		Assert.assertTrue("metering session is null", session != null); //$NON-NLS-1$
	    String scenarioName= session.getScenarioID();
		
		// determine all dimensions we need
		HashSet allDimensions= new HashSet();
		for (int i= 0; i < fCheckers.length; i++) {
			AssertChecker chk= fCheckers[i];
			Dim[] dims= chk.getDimensions();
			for (int j= 0; j < dims.length; j++)
				allDimensions.add(dims[j]);
		}
		
		// get data for this session
		DataPoint[] sessionDatapoints;
		Variations config= PerformanceTestPlugin.getVariations();
		if (config != null)
		    sessionDatapoints= DB.queryDataPoints(config, scenarioName, allDimensions);
		else
			sessionDatapoints= session.getDataPoints();
	    if (sessionDatapoints == null || sessionDatapoints.length == 0) {
	        PerformanceTestPlugin.logWarning("no session data named '" + config + "' found"); //$NON-NLS-1$ //$NON-NLS-2$
	        return;
	    }

		// get reference data
		DataPoint[] datapoints= DB.queryDataPoints(refKeys, scenarioName, allDimensions);
	    if (datapoints == null || datapoints.length == 0) {
	        PerformanceTestPlugin.logWarning("no reference data named '" + refKeys + "' found"); //$NON-NLS-1$ //$NON-NLS-2$
	        return;
	    }
		
	    // calculate the average
		StatisticsSession referenceStats= new StatisticsSession(datapoints);
		StatisticsSession measuredStats= new StatisticsSession(sessionDatapoints);

		StringBuffer failMesg= new StringBuffer("Performance criteria not met when compared to '" + refKeys + "':"); //$NON-NLS-1$ //$NON-NLS-2$ 
		boolean pass= true;
		for (int i= 0; i < fCheckers.length; i++) {
			AssertChecker chk= fCheckers[i];
			pass &= chk.test(referenceStats, measuredStats, failMesg);
		}
		
		if (!pass) {
		    if (config != null)
		        	DB.markAsFailed(config, session, failMesg.toString());
//		    else
//				Assert.assertTrue(failMesg.toString(), false);
		}
	}
}

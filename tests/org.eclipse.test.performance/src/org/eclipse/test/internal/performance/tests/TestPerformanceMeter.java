/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.test.internal.performance.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.test.internal.performance.InternalPerformanceMeter;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Sample;
import org.eclipse.test.internal.performance.data.Scalar;


/**
 * Mock performance meter that generates deterministic values for two dimensions.
 */
class TestPerformanceMeter extends InternalPerformanceMeter {
    	
	private long fStartTime;
	private List fDataPoints= new ArrayList();
	private Map fStart= new HashMap();
	private Map fStop= new HashMap();
	
	/**
	 * @param scenarioId the scenario id
	 */
	TestPerformanceMeter(String scenarioId) {
	    super(scenarioId);
		fStartTime= System.currentTimeMillis();
	}
		
    void addPair(Dim dimension, long start, long end) {
	    fStart.put(dimension, new Scalar(dimension, start));        
	    fStop.put(dimension, new Scalar(dimension, end));        
    }
    
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#dispose()
	 */
	public void dispose() {
	    fDataPoints= null;
	    super.dispose();
	}

	/*
	 * @see org.eclipse.test.internal.performance.InternalPerformanceMeter#getSample()
	 */
	public Sample getSample() {
	    if (fDataPoints != null)
	        return new Sample(getScenarioName(), fStartTime, new HashMap(), (DataPoint[]) fDataPoints.toArray(new DataPoint[fDataPoints.size()]));
	    return null;
	}
	
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#start()
	 */
	public void start() {
	    fDataPoints.add(new DataPoint(BEFORE, fStart));
	}
		
    /*
	 * @see org.eclipse.test.performance.PerformanceMeter#stop()
	 */
	public void stop() {
	    fDataPoints.add(new DataPoint(AFTER, fStop));
	}
}

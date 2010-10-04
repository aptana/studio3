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

package org.eclipse.test.internal.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Sample;


/**
 * Performance meter that makes its measurements with OS functionality.
 */
public class OSPerformanceMeter extends InternalPerformanceMeter {

	private PerformanceMonitor fPerformanceMonitor;
	private long fStartTime;
	private List fDataPoints= new ArrayList();    
	
	/**
	 * @param scenarioId the scenario id
	 */
	public OSPerformanceMeter(String scenarioId) {
	    super(scenarioId);
		fPerformanceMonitor= PerformanceMonitor.getPerformanceMonitor();
		fStartTime= System.currentTimeMillis();
	}
	
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#dispose()
	 */
	public void dispose() {
	    fPerformanceMonitor= null;
	    fDataPoints= null;
	    super.dispose();
	}

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#start()
	 */
	public void start() {
		snapshot(BEFORE);
	}
	
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#stop()
	 */
	public void stop() {
		snapshot(AFTER);
	}
	
	/*
	 * @see org.eclipse.test.internal.performance.InternalPerformanceMeter#getSample()
	 */
	public Sample getSample() {
	    if (fDataPoints != null) {
	        HashMap runProperties= new HashMap();
	        collectRunInfo(runProperties);
	        return new Sample(getScenarioName(), fStartTime, runProperties, (DataPoint[]) fDataPoints.toArray(new DataPoint[fDataPoints.size()]));
	    }
	    return null;
	}
	
	//---- private stuff ------
	
    private void snapshot(int step) {
	    HashMap map= new HashMap();
	    fPerformanceMonitor.collectOperatingSystemCounters(map);
	    fDataPoints.add(new DataPoint(step, map));
    }

	/**
	 * Write out the run element if it hasn't been written out yet.
	 * @param runProperties
	 */
	private void collectRunInfo(HashMap runProperties) {
        fPerformanceMonitor.collectGlobalPerformanceInfo(runProperties);
	}
}

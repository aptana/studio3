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
import java.util.Map;

import org.eclipse.test.internal.performance.data.Assert;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Sample;
import org.eclipse.test.internal.performance.data.Scalar;


public class SystemTimePerformanceMeter extends InternalPerformanceMeter {
	
	private static final int DEFAULT_INITIAL_CAPACITY= 3;
	
	private long fStartDate;
	private List fStartTime;
	private List fStopTime;
	
	/**
	 * @param scenarioId the scenario id
	 */
	public SystemTimePerformanceMeter(String scenarioId) {
		this(scenarioId, DEFAULT_INITIAL_CAPACITY);
		fStartDate= System.currentTimeMillis();
	}
	
	/**
	 * @param scenarioId the scenario id
	 * @param initalCapacity the initial capacity in the number of measurments
	 */
	public SystemTimePerformanceMeter(String scenarioId, int initalCapacity) {
	    super(scenarioId);
		fStartTime= new ArrayList(initalCapacity);
		fStopTime= new ArrayList(initalCapacity);
	}
	
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#start()
	 */
	public void start() {
		fStartTime.add(new Long(System.currentTimeMillis()));
	}
	
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#stop()
	 */
	public void stop() {
		fStopTime.add(new Long(System.currentTimeMillis()));
	}
	
	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#commit()
	 */
	public void commit() {
		Assert.isTrue(fStartTime.size() == fStopTime.size());
		System.out.println("Scenario: " + getScenarioName()); //$NON-NLS-1$
		int maxOccurenceLength= String.valueOf(fStartTime.size()).length();
		for (int i= 0; i < fStartTime.size(); i++) {
			String occurence= String.valueOf(i + 1);
			System.out.println("Occurence " + replicate(" ", maxOccurenceLength - occurence.length()) + occurence + ": " + (((Long) fStopTime.get(i)).longValue() - ((Long) fStartTime.get(i)).longValue())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	private String replicate(String s, int n) {
		StringBuffer buf= new StringBuffer(n * s.length());
		for (int i= 0; i < n; i++)
			buf.append(s);
		return buf.toString();
	}

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#dispose()
	 */
	public void dispose() {
		fStartTime= null;
		fStopTime= null;
		super.dispose();
	}

	public Sample getSample() {
	    	Assert.isTrue(fStartTime.size() == fStopTime.size());
	    	
	    	Map properties= new HashMap();
	    	/*
	    	properties.put(DRIVER_PROPERTY, PerformanceTestPlugin.getBuildId());
	    	properties.put(HOSTNAME_PROPERTY, getHostName());
	    	*/
	    	
	    	DataPoint[] data= new DataPoint[2*fStartTime.size()];
	    	for (int i= 0; i < fStartTime.size(); i++) {
	    		data[2*i]= createDataPoint(BEFORE, InternalDimensions.SYSTEM_TIME, ((Long) fStartTime.get(i)).longValue());
	    		data[2*i+1]= createDataPoint(AFTER, InternalDimensions.SYSTEM_TIME, ((Long) fStopTime.get(i)).longValue());
	    	}
	    	
	    	return new Sample(getScenarioName(), fStartDate, properties, data);
    }

	private DataPoint createDataPoint(int step, Dim dimension, long value) {
		Map scalars= new HashMap();
		scalars.put(dimension, new Scalar(dimension, value));
		return new DataPoint(step, scalars);
	}
}

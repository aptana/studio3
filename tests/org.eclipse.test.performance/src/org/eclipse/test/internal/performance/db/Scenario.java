/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.db;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;

import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.eval.StatisticsSession;

/**
 * A Scenario contains a series of data points for a single scenario.
 * The axis of the data points can be specified when creating a scenario.
 * Typical examples are:
 * - datapoints corresponding to different builds
 * - datapoints corresponding to different OSes
 * - datapoints corresponding to different JVMs
 * @since 3.1
 */
public class Scenario {
    
    private final static boolean DEBUG= false;
    
    public static class SharedState {
        
        private Variations fVariations;
        private String fSeriesKey;
        private Set fQueryDimensions;
        private String fScenarioPattern;
        private Map fMessages;
        
      
        SharedState(Variations variations, String scenarioPattern, String seriesKey, Dim[] dimensions) {
            fVariations= variations;
            fScenarioPattern= scenarioPattern;
            fSeriesKey= seriesKey;
            if (dimensions != null && dimensions.length > 0) {
                fQueryDimensions= new HashSet();
                for (int i= 0; i < dimensions.length; i++)
                    fQueryDimensions.add(dimensions[i]);
            }
        }
        
        String[] getFailures(String[] names, String scenarioId) {
            if (fMessages == null) {
	            fMessages= new HashMap();
	            Variations v= (Variations) fVariations.clone();
	            for (int i= 0; i < names.length; i++) {
	                v.put(fSeriesKey, names[i]);
	                Map map= DB.queryFailure(fScenarioPattern, v);
	                fMessages.put(names[i], map);
	            }
            }
            String[] result= new String[names.length];
            for (int i= 0; i < names.length; i++) {
                Map messages= (Map) fMessages.get(names[i]);
                if (messages != null)
                    result[i]= (String) messages.get(scenarioId);
            }
            return result;
        }
    }

    private SharedState fSharedState;
    private String fScenarioName;
    private String[] fSeriesNames;
    private StatisticsSession[] fSessions;
    private Map fSeries= new HashMap();
    private Dim[] fDimensions;
   
    /** 
     * @param scenario
     * @param variations
     * @param seriesKey
     * @param dimensions
     * @deprecated
     */
    public Scenario(String scenario, Variations variations, String seriesKey, Dim[] dimensions) {
        Assert.assertFalse(scenario.indexOf('%') >= 0);
        fScenarioName= scenario;
        fSharedState= new SharedState(variations, scenario, seriesKey, dimensions);
    }

    /** 
     * @param scenario
     * @param sharedState
     */
    public Scenario(String scenario, SharedState sharedState) {
        Assert.assertFalse(scenario.indexOf('%') >= 0);
        fScenarioName= scenario;
        fSharedState= sharedState;
    }

    public String getScenarioName() {
        return fScenarioName;
    }

    public Dim[] getDimensions() {
        loadSessions();
        if (fDimensions == null)
            return new Dim[0];
        return fDimensions;
    }
    
    public String[] getTimeSeriesLabels() {
        loadSeriesNames();
        if (fSeriesNames == null)
            return new String[0];
        return fSeriesNames;
    }
    
    public String[] getFailureMessages() {
        loadSeriesNames();
        return fSharedState.getFailures(fSeriesNames, fScenarioName);
    }

    public TimeSeries getTimeSeries(Dim dim) {
        loadSessions();
        TimeSeries ts= (TimeSeries) fSeries.get(dim);
        if (ts == null) {
            double[] ds= new double[fSessions.length];
            double[] sd= new double[fSessions.length];
            long[] sizes= new long[fSessions.length];
            for (int i= 0; i < ds.length; i++) {
                ds[i]= fSessions[i].getAverage(dim);
                sd[i]= fSessions[i].getStddev(dim);                
                sizes[i]= fSessions[i].getCount(dim);
            }
            ts= new TimeSeries(fSeriesNames, ds, sd, sizes);
            fSeries.put(dim, ts);
        }
        return ts;
    }
    
    public void dump(PrintStream ps, String key) {
	    ps.println("Scenario: " + getScenarioName()); //$NON-NLS-1$
	    Report r= new Report(2);
	    
	    String[] timeSeriesLabels= getTimeSeriesLabels();
	    r.addCell(key + ":"); //$NON-NLS-1$
	    for (int j= 0; j < timeSeriesLabels.length; j++)
	        r.addCellRight(timeSeriesLabels[j]);
	    r.nextRow();
	                
	    Dim[] dimensions= getDimensions();
	    for (int i= 0; i < dimensions.length; i++) {
	        Dim dim= dimensions[i];
	        r.addCell(dim.getName() + ':');
	        
	        TimeSeries ts= getTimeSeries(dim);
	        int n= ts.getLength();
	        for (int j= 0; j < n; j++) {
	            String stddev= ""; //$NON-NLS-1$
	            double stddev2= ts.getStddev(j);
	            if (stddev2 != 0.0)
	            	stddev= " [" + dim.getDisplayValue(stddev2) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	            r.addCellRight(dim.getDisplayValue(ts.getValue(j)) + stddev);
	        }
	        r.nextRow();
	    }
	    r.print(ps);
	    ps.println();
    }
    
    //---- private
        
    private void loadSeriesNames() {
        if (fSeriesNames == null) {
            long start;
            if (DEBUG) start= System.currentTimeMillis();
            fSeriesNames= DB.querySeriesValues(fScenarioName, fSharedState.fVariations, fSharedState.fSeriesKey);
            if (DEBUG) System.err.println("names: " + (System.currentTimeMillis()-start)); //$NON-NLS-1$
        }
    }
    
    private void loadSessions() {
        if (fSessions != null)
            return;
        
        loadSeriesNames();
        
        long start;
        Variations v= (Variations) fSharedState.fVariations.clone();
        if (DEBUG) start= System.currentTimeMillis();
        ArrayList sessions= new ArrayList();
        ArrayList names2= new ArrayList();
        Set dims= new HashSet();
        for (int t= 0; t < fSeriesNames.length; t++) {
            v.put(fSharedState.fSeriesKey, fSeriesNames[t]);
            DataPoint[] dps= DB.queryDataPoints(v, fScenarioName, fSharedState.fQueryDimensions);
            if (DEBUG) System.err.println("  dps length: " + dps.length); //$NON-NLS-1$
            if (dps.length > 0) {
                dims.addAll(dps[0].getDimensions2());
                sessions.add(new StatisticsSession(dps));
                names2.add(fSeriesNames[t]);
            }
        }
        if (DEBUG) System.err.println("data: " + (System.currentTimeMillis()-start)); //$NON-NLS-1$

        fSessions= (StatisticsSession[]) sessions.toArray(new StatisticsSession[sessions.size()]);
        fSeriesNames= (String[]) names2.toArray(new String[sessions.size()]);
        
        fDimensions= (Dim[]) dims.toArray(new Dim[dims.size()]);
        Arrays.sort(fDimensions,
        new Comparator() {
            	public int compare(Object o1, Object o2) {
            	    Dim d1= (Dim)o1;
            	    Dim d2= (Dim)o2;
            	    return d1.getName().compareTo(d2.getName());
            	}
        	}
        );
    }
}

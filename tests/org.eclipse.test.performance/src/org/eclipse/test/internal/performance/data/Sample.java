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

import java.util.Map;
import java.util.Set;

import org.eclipse.test.performance.Dimension;

import junit.framework.Assert;

/**
 * @since 3.1
 */
public class Sample {
	String fScenarioID;
	long fStartTime;
	Map fProperties;
	DataPoint[] fDataPoints;
	
	boolean fIsSummary;
	boolean fSummaryIsGlobal;
	String fShortName;
	Dimension[] fSummaryDimensions;
	int fCommentType;
	String fComment;
	
	
	public Sample(String scenarioID, long starttime, Map properties, DataPoint[] dataPoints) {
		Assert.assertTrue("scenarioID is null", scenarioID != null); //$NON-NLS-1$
		fScenarioID= scenarioID;
		fStartTime= starttime;
		fProperties= properties;
		fDataPoints= dataPoints;
	}
	
    public Sample(DataPoint[] dataPoints) {
        fDataPoints= dataPoints;
    }

    public void setComment(int commentType, String comment) {
        fCommentType= commentType;
        fComment= comment;
    }
    
    public void tagAsSummary(boolean global, String shortName, Dimension[] summaryDimensions, int commentType, String comment) {
        fIsSummary= true;
        fSummaryIsGlobal= global;
        fShortName= shortName;
        fSummaryDimensions= summaryDimensions;
        fCommentType= commentType;
        fComment= comment;
    }
    
	public String getScenarioID() {
	    return fScenarioID;
	}
	
    public long getStartTime() {
         return fStartTime;
    }
    
	public String getProperty(String name) {
		return (String) fProperties.get(name);
	}
	
	public String[] getPropertyKeys() {
	    if (fProperties == null)
	        return new String[0];
	    Set set= fProperties.keySet();
	    return (String[]) set.toArray(new String[set.size()]);
	}
	
	public DataPoint[] getDataPoints() {
		DataPoint[] dataPoints= new DataPoint[fDataPoints.length];
		System.arraycopy(fDataPoints, 0, dataPoints, 0, fDataPoints.length);
		return dataPoints;
	}
	
	public String toString() {
	    return "MeteringSession [scenarioID= " + fScenarioID + ", #datapoints: " + fDataPoints.length + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

    public boolean isSummary() {
        return fIsSummary;
    }
    
    public boolean isGlobal() {
        return fSummaryIsGlobal;
    }
    
    public String getShortname() {
        return fShortName;
    }
    
    public Dimension[] getSummaryDimensions() {
        return fSummaryDimensions;
    }

	public int getCommentType() {
		return fCommentType;
	}

	public String getComment() {
		return fComment;
	}
}

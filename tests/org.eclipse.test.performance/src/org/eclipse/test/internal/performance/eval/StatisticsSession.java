/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.eval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.test.internal.performance.InternalPerformanceMeter;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Scalar;
import org.eclipse.test.internal.performance.eval.StatisticsUtil.Percentile;

import junit.framework.Assert;

/**
 * @since 3.1
 */
public class StatisticsSession {

	static final class Statistics {
		public long count;
		public long sum;
		public double average;
		public double stddev;
	}
	
	private final DataPoint[] fDataPoints;
	private final Map fStatistics= new HashMap();

	public StatisticsSession(DataPoint[] datapoints) {
	    fDataPoints= datapoints;
	}
	
	public double getAverage(Dim dimension) {
		return getStats(dimension).average;
	}
	
	public long getSum(Dim dimension) {
		return getStats(dimension).sum;
	}
	
	public long getCount(Dim dimension) {
		return getStats(dimension).count;
	}
	
	public double getStddev(Dim dimension) {
		return getStats(dimension).stddev;
	}
	
	double getStderr_mean(Dim dimension) {
		return getStats(dimension).stddev / Math.sqrt(getStats(dimension).count);
	}
	
	double getStudentsT(Dim dimension, Percentile percentile) {
		int df= (int) getStats(dimension).count - 1;
		return StatisticsUtil.getStudentsT(df, percentile);
	}
	
	/**
	 * Returns the confidence interval for the given dimension and the percentile.
	 * 
	 * @param dimension the dimension 
	 * @param percentile the percentile
	 * @return an array of length two, with the lower and upper bounds of the confidence interval
	 */
	public double[] getConfidenceInterval(Dim dimension, Percentile percentile) {
	    double mean= getAverage(dimension);
		double stat_err= getStderr_mean(dimension);
		
		double t= getStudentsT(dimension, percentile);
		
		double[] interval= {mean - t * stat_err, mean + t * stat_err};
		
		return interval;
	}
	
	private Statistics getStats(Dim dimension) {
		Statistics stats= (Statistics) fStatistics.get(dimension);
		if (stats == null) {
			stats= computeStats(dimension);
			fStatistics.put(dimension, stats);
		}
		return stats;
	}

	private Statistics computeStats(Dim dimension) {
	    		
	    Statistics stats;
		
		Set steps= new HashSet();
		for (int j= 0; j < fDataPoints.length; j++) {
		    DataPoint dp= fDataPoints[j];
		    steps.add(new Integer(dp.getStep()));
		}
        
        if (steps.contains(new Integer(InternalPerformanceMeter.AVERAGE))) {
            // an already aggregated set of data points from the DB
            stats= computeStatsFromAggregates(dimension);
        } else if (steps.contains(new Integer(InternalPerformanceMeter.AFTER))) {
            // raw values from measurement
            stats= computeStatsFromMeasurements(dimension, steps);
        } else {
            Assert.fail("illegal data set: contains neither AVERAGE nor AFTER values."); //$NON-NLS-1$
            stats= null; // dummy
        }

		return stats;
	}

    private Statistics computeStatsFromAggregates(Dim dimension) {
        Statistics stats= new Statistics();
        long aggregateCount= 0;
        double averageSum= 0;
        long countSum= 0;
        double stdevSum= 0;
        
//        Set acquiredAggregates= new HashSet();
        for (int i= 0; i < fDataPoints.length; i++) {
            DataPoint point= fDataPoints[i];
            Scalar scalar= point.getScalar(dimension);
            if (scalar == null)
                continue;
            
            Integer aggregate= new Integer(point.getStep());
            // allow for multiple measurements that were each stored with their own 
            // aggregate values
//            Assert.assertTrue(acquiredAggregates.add(aggregate));
            
            long magnitude= scalar.getMagnitude();
            switch (aggregate.intValue()) {
                case InternalPerformanceMeter.AVERAGE:
                    averageSum += magnitude;
                    aggregateCount++;
                    break;
                case InternalPerformanceMeter.STDEV:
                    // see DB.internalStore
                    stdevSum += Double.longBitsToDouble(magnitude);
                    break;
                case InternalPerformanceMeter.SIZE:
                    countSum += magnitude;
                    break;
                default:
                    Assert.fail("only average, stdev and size are supported in aggregate mode"); //$NON-NLS-1$
                    break;
            }
        }
        
        stats.average= averageSum / aggregateCount;
        stats.stddev= stdevSum / aggregateCount; // XXX this does not work! have to treat multiple runs like normal measurement data
        stats.count= countSum;
        stats.sum= Math.round(stats.count * stats.average);
        
        return stats;
    }

    private Statistics computeStatsFromMeasurements(Dim dimension, Set steps) {
        Statistics stats= new Statistics();
        long mags[];
        switch (steps.size()) {
            case 1:
                // if there is only one Step, we don't calculate the delta. happens for startup tests
                mags= new long[fDataPoints.length];
                for (int i= 0; i < fDataPoints.length; i++) {
                    Scalar sc= fDataPoints[i].getScalar(dimension);
                    mags[i]= sc == null ? 0 : sc.getMagnitude();
                }
                break;
            case 2:
                int count= fDataPoints.length / 2;
                mags= new long[count];
                for (int i= 0; i < count; i ++) {
                    DataPoint before= fDataPoints[2 * i];
                    Assert.assertTrue("wrong order of steps", before.getStep() == InternalPerformanceMeter.BEFORE); //$NON-NLS-1$
                    DataPoint after= fDataPoints[2 * i + 1];
                    Assert.assertTrue("wrong order of steps", after.getStep() == InternalPerformanceMeter.AFTER); //$NON-NLS-1$

                    Scalar delta= getDelta(before, after, dimension);
                    long magnitude= delta.getMagnitude();
                    mags[i]= magnitude;
                }
                break;
            default:
                Assert.fail("cannot handle more than two steps in measurement mode"); //$NON-NLS-1$
                return null; // dummy
        }
        
        for (int i= 0; i < mags.length; i++) {
            stats.sum += mags[i];
            stats.count++;
        }
        
        if (stats.count > 0) {
            stats.average= (double) stats.sum / stats.count;
            if (stats.count == 1) {
	            stats.stddev= 0;
            } else {
	            double squaredDeviations= 0;
	            for (int i= 0; i < mags.length; i++) {
	                double deviation= stats.average - mags[i];
	                squaredDeviations += deviation * deviation;
	            }
	            stats.stddev= Math.sqrt(squaredDeviations / (stats.count - 1)); // unbiased sample stdev
            }
        } else {
            stats.average= 0;
            stats.stddev= 0;
        }
        
        return stats;
    }

	private Scalar getDelta(DataPoint before, DataPoint after, Dim dimension) {
		Scalar one= before.getScalar(dimension);
		Assert.assertTrue("reference has no value for dimension " + dimension, one != null); //$NON-NLS-1$

		Scalar two= after.getScalar(dimension);
		Assert.assertTrue("reference has no value for dimension " + dimension, two != null); //$NON-NLS-1$
		
		return new Scalar(one.getDimension(), two.getMagnitude() - one.getMagnitude());
	}

	public boolean contains(Dim dimension) {
		if (fDataPoints.length > 0)
			return fDataPoints[0].contains(dimension);
		return false;
	}
}

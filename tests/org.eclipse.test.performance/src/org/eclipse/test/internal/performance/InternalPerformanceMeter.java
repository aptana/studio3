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

package org.eclipse.test.internal.performance;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.test.internal.performance.data.DataPoint;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Sample;
import org.eclipse.test.internal.performance.db.DB;
import org.eclipse.test.internal.performance.db.Variations;
import org.eclipse.test.internal.performance.eval.StatisticsSession;
import org.eclipse.test.internal.performance.eval.StatisticsUtil;
import org.eclipse.test.internal.performance.eval.StatisticsUtil.Percentile;
import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceMeter;


public abstract class InternalPerformanceMeter extends PerformanceMeter {

    
	private static class DimensionComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			return ((Dim) o1).getId() - ((Dim) o2).getId();
		}

	}

	public static final int AVERAGE= -3;
	public static final int SIZE= -4;
	public static final int STDEV= -5;
    public static final int BEFORE= 0;
    public static final int AFTER= 1;
    
    protected static final String VERBOSE_PERFORMANCE_METER_PROPERTY= "InternalPrintPerformanceResults"; //$NON-NLS-1$

	private String fScenarioId;
	
	private String fShortName;
	private Dimension[] fSummaryDimensions;
	private boolean fSummaryIsGlobal;
	private int fCommentType;
	private String fComment;

	
	public InternalPerformanceMeter(String scenarioId) {
	    fScenarioId= scenarioId;
    }

	public void dispose() {
	    fScenarioId= null;
	}

    public abstract Sample getSample();

	/**
	 * Answer the scenario ID.
	 * @return the scenario ID
	 */
	public String getScenarioName() {
		return fScenarioId;
	}

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#commit()
	 */
	public void commit() {
		Sample sample= getSample();
		if (sample != null) {
			if (fSummaryDimensions != null) {
				sample.tagAsSummary(fSummaryIsGlobal, fShortName, fSummaryDimensions, fCommentType, fComment);
			} else if (this.fComment != null) {
				sample.setComment(this.fCommentType, this.fComment);
			}
			Variations variations= PerformanceTestPlugin.getVariations();
			if (variations != null)
				DB.store(variations, sample);
			if (!DB.isActive() || System.getProperty(VERBOSE_PERFORMANCE_METER_PROPERTY) != null) {
				printSample(System.out, sample);
//				printSampleCSV(System.out, sample);
			}
		}
	}

	private void printSample(PrintStream ps, Sample sample) {
		ps.print("Scenario '" + getScenarioName() + "' "); //$NON-NLS-1$ //$NON-NLS-2$
		DataPoint[] dataPoints= sample.getDataPoints();
		if (dataPoints.length > 0) {
			StatisticsSession s= new StatisticsSession(dataPoints);
			Dim[] dimensions= dataPoints[0].getDimensions();
			Arrays.sort(dimensions, new DimensionComparator());
			if (dimensions.length > 0) {
				List badDimensions= new ArrayList();
				long n= s.getCount(dimensions[0]);
				MessageFormat format= new MessageFormat("({0,number,percent} in [{1}, {2}])"); //$NON-NLS-1$

				String spaces= "                                                                                                       "; //$NON-NLS-1$

				ps.println("(average over " + n + " samples):"); //$NON-NLS-1$ //$NON-NLS-2$
				for (int i= 0; i < dimensions.length; i++) {
					Dim dimension= dimensions[i];
					double mean= s.getAverage(dimension);

					String nameString= "  " + dimension.getName() + ":"; //$NON-NLS-1$ //$NON-NLS-2$
					String meanString= dimension.getDisplayValue(mean);
					int align= firstNonDigit(meanString);
					int endIndex = 30 - align - nameString.length();
					if (endIndex > 0) meanString= spaces.substring(0, endIndex) + meanString;

					align= nameString.length() + meanString.length();

					Percentile percentile= StatisticsUtil.T95;
					double[] confidenceInterval= s.getConfidenceInterval(dimension, percentile);

					StringBuffer printBuffer;
					if (n <= 2) {
						printBuffer = new StringBuffer(" (no confidence)"); //$NON-NLS-1$
					} else {
						printBuffer = new StringBuffer();
						int ns = align;
						while (ns++ < 40) printBuffer.append(' ');
						printBuffer.append(format.format(new Object[] {new Double(percentile.inside()), dimension.getDisplayValue(confidenceInterval[0]), dimension.getDisplayValue(confidenceInterval[1])}));
					}

					align+= printBuffer.length();
					try {
						while (align++ < 70) printBuffer.append(' ');
						printBuffer.append(checkSampleSize(s, sample, dimension));
					} catch (CoreException x) {
						badDimensions.add(dimension);
						continue;
					}

					ps.print(nameString);
					ps.print(meanString);
					ps.println(printBuffer);
				}
				
				if (!badDimensions.isEmpty()) {
					ps.print("  Dimensions with unusable statistical properties: "); //$NON-NLS-1$
					for (Iterator iter= badDimensions.iterator(); iter.hasNext();) {
						Dim dimension= (Dim) iter.next();
						ps.print(dimension.getName());
						if (iter.hasNext())
							ps.print(", "); //$NON-NLS-1$
					}
					ps.println();
				}
			}
		}
		ps.println();
	}

	private String checkSampleSize(StatisticsSession s, Sample sample, Dim dimension) throws CoreException {
		long sampleSize= s.getCount(dimension);
		double stdev= s.getStddev(dimension);
		double mean= s.getAverage(dimension);
		
		if (stdev == 0)
			return ""; //$NON-NLS-1$
		
		// measurable effect size
		// sampleSize= 16 * stdev^2 / effect^2
		double effectSize= 4 * Math.sqrt(stdev * stdev / sampleSize);
		
		double base;
		String baseName;
		if (stdev > Math.abs(mean)) {
			base= stdev;
			baseName="stdev"; //$NON-NLS-1$
		} else {
			base= Math.abs(mean);
			baseName= "mean"; //$NON-NLS-1$
		}
		double fivePercentEffect= 0.05 * base;
		long requiredSampleSizeForFivePercentEffect= Math.round(16 * stdev * stdev / fivePercentEffect / fivePercentEffect + 0.5);
		
//		if (requiredSampleSizeForFivePercentEffect > 1000 || Double.isNaN(stdev))
//			throw new CoreException(new Status(IStatus.OK, "org.eclipse.text.performance", IStatus.OK, "no message", null)); //$NON-NLS-1$ //$NON-NLS-2$
		
		NumberFormat numberInstance= NumberFormat.getNumberInstance();
		numberInstance.setMaximumFractionDigits(1);
		numberInstance.setMinimumFractionDigits(1);
		
		String measurableMsg= " Measurable effect: " + dimension.getDisplayValue(effectSize) + " (" + numberInstance.format(effectSize / stdev) + " SDs)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (fivePercentEffect < effectSize)
			measurableMsg += " (required sample size for an effect of 5% of " + baseName + ": " + requiredSampleSizeForFivePercentEffect + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		return measurableMsg;
	}

	private int firstNonDigit(String string) {
		int length= string.length();
		for (int i= 0; i < length; i++)
			if (!Character.isDigit(string.charAt(i)) && string.charAt(i) != '-' && string.charAt(i) != '.')
				return i;
		return length;
	}

	void printSampleCSV(PrintStream ps, Sample sample) {
		final char SEPARATOR= '\t';
		DataPoint[] dataPoints= sample.getDataPoints();
		if (dataPoints.length > 0) {
			Dim[] dimensions= dataPoints[0].getDimensions();
			Arrays.sort(dimensions, new DimensionComparator());
			if (dimensions.length > 0) {
				/* print dimensions */
				for (int d= 0; d < dimensions.length; d++) {
					Dim dimension= dimensions[d];
					ps.print(dimension.getName());
					ps.print(SEPARATOR);
				}
				ps.println("scenario"); //$NON-NLS-1$
				
				for (int p= 0; p < dataPoints.length - 1; p+=2) {
					DataPoint before= dataPoints[p];
					DataPoint after= dataPoints[p + 1];
					for (int d= 0; d < dimensions.length; d++) {
						Dim dimension= dimensions[d];
						long valBefore= before.getScalar(dimension).getMagnitude();
						long valAfter= after.getScalar(dimension).getMagnitude();
						ps.print(valAfter - valBefore);
						ps.print(SEPARATOR);
					}
					ps.print(sample.getShortname() != null ? sample.getShortname() : sample.getScenarioID());
					ps.println();
				}
				
				ps.println();
			}
		}
	}

    public void tagAsSummary(boolean global, String shortName, Dimension[] dims) {
        fSummaryIsGlobal= global;
        fShortName= shortName;
        fSummaryDimensions= dims;
     }

	public void setComment(int commentType, String comment) {
		fCommentType= commentType;
		fComment= comment;
	}
}

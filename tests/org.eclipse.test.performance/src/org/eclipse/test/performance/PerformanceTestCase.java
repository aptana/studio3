/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.performance;

import junit.framework.TestCase;

/**
 * A PerformanceTestCase is a convenience class that takes care of managing a <code>PerformanceMeter</code>.
 * <p>
 * Here is an example:
 * 
 * <pre>
 * public class MyPerformanceTestCase extends PeformanceTestCase {
 * 		
 *   public void testMyOperation() {
 *     for (int i= 0; i < 10; i++) {
 *       // preparation
 *       startMeasuring();
 *       // my operation
 *       stopMeasuring();
 *       // clean up
 *     }
 *     commitMeasurements();
 *     assertPerformance();
 *   }
 * }
 */
public class PerformanceTestCase extends TestCase
{

	protected PerformanceMeter fPerformanceMeter;

	/**
	 * Constructs a performance test case.
	 */
	public PerformanceTestCase()
	{
		super();
	}

	/**
	 * Constructs a performance test case with the given name.
	 * 
	 * @param name
	 *            the name of the performance test case
	 */
	public PerformanceTestCase(String name)
	{
		super(name);
	}

	/**
	 * Overridden to create a default performance meter for this test case.
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception
	{
		Performance performance = Performance.getDefault();
		fPerformanceMeter = performance.createPerformanceMeter(performance.getDefaultScenarioId(this));
	}

	/**
	 * Overridden to dispose of the performance meter.
	 * 
	 * @throws Exception
	 */
	protected void tearDown() throws Exception
	{
		fPerformanceMeter.dispose();
	}

	/**
	 * Mark the scenario of this test case to be included into the global and the component performance summary. The
	 * summary shows the given dimension of the scenario and labels the scenario with the short name.
	 * 
	 * @param shortName
	 *            a short (shorter than 40 characters) descritive name of the scenario
	 * @param dimension
	 *            the dimension to show in the summary
	 */
	public void tagAsGlobalSummary(String shortName, Dimension dimension)
	{
		Performance performance = Performance.getDefault();
		performance.tagAsGlobalSummary(fPerformanceMeter, shortName, new Dimension[] { dimension });
	}

	/**
	 * Mark the scenario represented by the given PerformanceMeter to be included into the global and the component
	 * performance summary. The summary shows the given dimensions of the scenario and labels the scenario with the
	 * short name.
	 * 
	 * @param shortName
	 *            a short (shorter than 40 characters) descritive name of the scenario
	 * @param dimensions
	 *            an array of dimensions to show in the summary
	 */
	public void tagAsGlobalSummary(String shortName, Dimension[] dimensions)
	{
		Performance performance = Performance.getDefault();
		performance.tagAsGlobalSummary(fPerformanceMeter, shortName, dimensions);
	}

	/**
	 * Mark the scenario of this test case to be included into the component performance summary. The summary shows the
	 * given dimension of the scenario and labels the scenario with the short name.
	 * 
	 * @param shortName
	 *            a short (shorter than 40 characters) descritive name of the scenario
	 * @param dimension
	 *            the dimension to show in the summary
	 */
	public void tagAsSummary(String shortName, Dimension dimension)
	{
		Performance performance = Performance.getDefault();
		performance.tagAsSummary(fPerformanceMeter, shortName, new Dimension[] { dimension });
	}

	/**
	 * Mark the scenario represented by the given PerformanceMeter to be included into the component performance
	 * summary. The summary shows the given dimensions of the scenario and labels the scenario with the short name.
	 * 
	 * @param shortName
	 *            a short (shorter than 40 characters) descritive name of the scenario
	 * @param dimensions
	 *            an array of dimensions to show in the summary
	 */
	public void tagAsSummary(String shortName, Dimension[] dimensions)
	{
		Performance performance = Performance.getDefault();
		performance.tagAsSummary(fPerformanceMeter, shortName, dimensions);
	}

	/**
	 * Set a comment for the scenario represented by this TestCase. Currently only comments with a commentKind of
	 * EXPLAINS_DEGRADATION_COMMENT are used. Their commentText is shown in a hover of the performance summaries graph
	 * if a performance degradation exists.
	 * 
	 * @param commentKind
	 *            kind of comment. Must be EXPLAINS_DEGRADATION_COMMENT to have an effect.
	 * @param commentText
	 *            the comment (shorter than 400 characters)
	 */
	public void setComment(int commentKind, String commentText)
	{
		Performance performance = Performance.getDefault();
		performance.setComment(fPerformanceMeter, commentKind, commentText);
	}

	/**
	 * Called from within a test case immediately before the code to measure is run. It starts capturing of performance
	 * data. Must be followed by a call to {@link PerformanceTestCase#stopMeasuring()} before subsequent calls to this
	 * method or {@link PerformanceTestCase#commitMeasurements()}.
	 * 
	 * @see PerformanceMeter#start()
	 */
	protected void startMeasuring()
	{
		fPerformanceMeter.start();
	}

	/**
	 * Called from within a test case immediately after the operation to measure. Must be preceded by a call to
	 * {@link PerformanceTestCase#startMeasuring()}, that follows any previous call to this method.
	 * 
	 * @see PerformanceMeter#stop()
	 */
	protected void stopMeasuring()
	{
		fPerformanceMeter.stop();
	}

	/**
	 * Called exactly once after repeated measurements are done and before their analysis. Afterwards
	 * {@link PerformanceTestCase#startMeasuring()} and {@link PerformanceTestCase#stopMeasuring()} must not be called.
	 * 
	 * @see PerformanceMeter#commit()
	 */
	protected void commitMeasurements()
	{
		fPerformanceMeter.commit();
	}

	/**
	 * Asserts default properties of the measurements captured for this test case.
	 * 
	 * @throws RuntimeException
	 *             if the properties do not hold
	 */
	protected void assertPerformance()
	{
		Performance.getDefault().assertPerformance(fPerformanceMeter);
	}

	/**
	 * Asserts that the measurement specified by the given dimension is within a certain range with respect to some
	 * reference value. If the specified dimension isn't available, the call has no effect.
	 * 
	 * @param dim
	 *            the Dimension to check
	 * @param lowerPercentage
	 *            a negative number indicating the percentage the measured value is allowed to be smaller than some
	 *            reference value
	 * @param upperPercentage
	 *            a positive number indicating the percentage the measured value is allowed to be greater than some
	 *            reference value
	 * @throws RuntimeException
	 *             if the properties do not hold
	 */
	protected void assertPerformanceInRelativeBand(Dimension dim, int lowerPercentage, int upperPercentage)
	{
		Performance.getDefault().assertPerformanceInRelativeBand(fPerformanceMeter, dim, lowerPercentage,
				upperPercentage);
	}
}

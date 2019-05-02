/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.editor.epl.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import com.aptana.testing.categories.PerformanceTests;

/**
 * Superclass of Text performance test cases.
 *
 * @since 3.1
 */
@Category({ PerformanceTests.class })
public abstract class TextPerformanceTestCase {

	@Rule
	public final TestName name = new TestName();
	
	private static final boolean DEBUG= false;

	/** custom number of warm-up runs */
	private int fCustomWarmUpRuns= -1;

	/** custom number of measured runs */
	private int fCustomMeasuredRuns= -1;

	/** created performance meters */
	private List<PerformanceMeter> fPerformanceMeters;

	/** {@link KeyboardProbe} singleton */
	private static KeyboardProbe fgKeyboardProbe;

	/** base scenario id */
	private String fBaseScenarioId;

	@Before
	public void setUp() throws Exception {

		EditorTestHelper.forceFocus();

		if (DEBUG)
			System.out.println(getClass().getName() + "." + name.getMethodName() + ": " + System.currentTimeMillis());
	}

	@After
	public void tearDown() throws Exception {
		if (fPerformanceMeters != null)
			for (Iterator<PerformanceMeter> iter= fPerformanceMeters.iterator(); iter.hasNext();)
				iter.next().dispose();

		if (DEBUG)
			System.out.println("    torn down: " + System.currentTimeMillis());
	}

	/**
	 * @return number of warm-up runs, must have been set before
	 */
	protected final int getWarmUpRuns() {
		assertTrue(fCustomWarmUpRuns >= 0);
		return fCustomWarmUpRuns;
	}

	/**
	 * Sets the number of warm-up runs. Can be overridden.
	 *
	 * @param runs number of warm-up runs
	 */
	protected final void setWarmUpRuns(int runs) {
		fCustomWarmUpRuns= runs;
	}

	/**
	 * @return number of measured runs, must have been set before
	 */
	protected final int getMeasuredRuns() {
		assertTrue(fCustomMeasuredRuns >= 0);
		return fCustomMeasuredRuns;
	}

	/**
	 * Sets the number of measured runs. Can be overridden.
	 *
	 * @param runs number of measured runs
	 */
	protected final void setMeasuredRuns(int runs) {
		fCustomMeasuredRuns= runs;
	}

	/**
	 * @return the default scenario id for this test
	 */
	protected final String getDefaultScenarioId() {
		return getClass().getName() + '#' + name.getMethodName() + "()"; //$NON-NLS-1$
	}

	/**
	 * Returns the base scenario id for this test which has the default
	 * scenario id as its default.
	 *
	 * @return the base scenario id
	 */
	protected final String getBaseScenarioId() {
		if (fBaseScenarioId == null)
			fBaseScenarioId= getDefaultScenarioId();
		return fBaseScenarioId;
	}

	/**
	 * Sets the base scenario id for this test.
	 *
	 * @param baseScenarioId the base scenario id
	 */
	protected final void setBaseScenarioId(String baseScenarioId) {
		fBaseScenarioId= baseScenarioId;
	}

	/**
	 * Create a performance meter with the base scenario id. The
	 * performance meter will be disposed on {@link #tearDown()}.
	 *
	 * @return the created performance meter
	 */
	protected PerformanceMeter createPerformanceMeter() {
		return createPerformanceMeter("");
	}

	/**
	 * Create a performance meter with the given sub-scenario id. The
	 * performance meter will be disposed on {@link #tearDown()}.
	 *
	 * @param subScenarioId the sub-scenario id
	 * @return the created performance meter
	 */
	protected final PerformanceMeter createPerformanceMeter(String subScenarioId) {
		return internalCreatePerformanceMeter(getBaseScenarioId() + subScenarioId);
	}

	/**
	 * Create a performance meter with the base scenario id and mark the
	 * scenario to be included into the component performance summary. The
	 * summary shows the given dimension of the scenario and labels the
	 * scenario with the short name. The performance meter will be disposed
	 * on {@link #tearDown()}.
	 *
	 * @param shortName a short (shorter than 40 characters) descriptive
	 *                name of the scenario
	 * @param dimension the dimension to show in the summary
	 * @return the created performance meter
	 */
	protected final PerformanceMeter createPerformanceMeterForSummary(String shortName, Dimension dimension) {
		return createPerformanceMeterForSummary("", shortName, dimension);
	}

	/**
	 * Create a performance meter with the given sub-scenario id and mark
	 * the scenario to be included into the component performance summary.
	 * The summary shows the given dimension of the scenario and labels the
	 * scenario with the short name. The performance meter will be disposed
	 * on {@link #tearDown()}.
	 *
	 * @param subScenarioId the sub-scenario id
	 * @param shortName a short (shorter than 40 characters) descriptive
	 *                name of the scenario
	 * @param dimension the dimension to show in the summary
	 * @return the created performance meter
	 */
	protected final PerformanceMeter createPerformanceMeterForSummary(String subScenarioId, String shortName, Dimension dimension) {
		PerformanceMeter performanceMeter= createPerformanceMeter(subScenarioId);
		Performance.getDefault().tagAsSummary(performanceMeter, shortName, dimension);
		return performanceMeter;
	}

	/**
	 * Create a performance meter with the base scenario id and mark the
	 * scenario to be included into the global performance summary. The
	 * summary shows the given dimension of the scenario and labels the
	 * scenario with the short name. The performance meter will be disposed
	 * on {@link #tearDown()}.
	 *
	 * @param shortName a short (shorter than 40 characters) descriptive
	 *                name of the scenario
	 * @param dimension the dimension to show in the summary
	 * @return the created performance meter
	 */
	protected final PerformanceMeter createPerformanceMeterForGlobalSummary(String shortName, Dimension dimension) {
		return createPerformanceMeterForGlobalSummary("", shortName, dimension);
	}

	/**
	 * Create a performance meter with the given sub-scenario id and mark
	 * the scenario to be included into the global performance summary. The
	 * summary shows the given dimension of the scenario and labels the
	 * scenario with the short name. The performance meter will be disposed
	 * on {@link #tearDown()}.
	 *
	 * @param subScenarioId the sub-scenario id
	 * @param shortName a short (shorter than 40 characters) descriptive
	 *                name of the scenario
	 * @param dimension the dimension to show in the summary
	 * @return the created performance meter
	 */
	protected final PerformanceMeter createPerformanceMeterForGlobalSummary(String subScenarioId, String shortName, Dimension dimension) {
		PerformanceMeter performanceMeter= createPerformanceMeter(subScenarioId);
		Performance.getDefault().tagAsGlobalSummary(performanceMeter, shortName, dimension);
		return performanceMeter;
	}

	/**
	 * Commits the measurements captured by all performance meters created
	 * through one of this class' factory methods.
	 */
	protected final void commitAllMeasurements() {
		if (fPerformanceMeters != null)
			for (Iterator<PerformanceMeter> iter= fPerformanceMeters.iterator(); iter.hasNext();)
				iter.next().commit();
	}

	/**
	 * Asserts default properties of the measurements captured by the given
	 * performance meter.
	 *
	 * @param performanceMeter the performance meter
	 * @throws RuntimeException if the properties do not hold
	 */
	protected final void assertPerformance(PerformanceMeter performanceMeter) {
		Performance.getDefault().assertPerformance(performanceMeter);
	}

	/**
	 * Asserts default properties of the measurements captured by all
	 * performance meters created through one of this class' factory
	 * methods.
	 *
	 * @throws RuntimeException if the properties do not hold
	 */
	protected final void assertAllPerformance() {
		if (fPerformanceMeters != null)
			for (Iterator<PerformanceMeter> iter= fPerformanceMeters.iterator(); iter.hasNext();)
				assertPerformance(iter.next());
	}

	/**
	 * Returns the null performance meter singleton.
	 *
	 * @return the null performance meter singleton
	 */
	protected static final PerformanceMeter getNullPerformanceMeter() {
		return Performance.getDefault().getNullPerformanceMeter();
	}

	/**
	 * Returns the keyboard probe singleton.
	 *
	 * @return the keyboard probe singleton.
	 */
	protected static final KeyboardProbe getKeyboardProbe() {
		if (fgKeyboardProbe == null) {
			fgKeyboardProbe= new KeyboardProbe();
			fgKeyboardProbe.initialize();
		}
		return fgKeyboardProbe;
	}

	/*
	 * @see PerformanceTestCase#setComment(int, String)
	 * @since 3.1
	 */
	protected final void explainDegradation(String explanation, PerformanceMeter performanceMeter) {
		Performance performance= Performance.getDefault();
		performance.setComment(performanceMeter, Performance.EXPLAINS_DEGRADATION_COMMENT, explanation);
	}

	/**
	 * Create a performance meter with the given scenario id. The
	 * performance meter will be disposed on {@link #tearDown()}.
	 *
	 * @param scenarioId the scenario id
	 * @return the created performance meter
	 */
	private PerformanceMeter internalCreatePerformanceMeter(String scenarioId) {
		PerformanceMeter performanceMeter= Performance.getDefault().createPerformanceMeter(scenarioId);
		addPerformanceMeter(performanceMeter);
		return performanceMeter;
	}

	/**
	 * Add the given performance meter to the managed performance meters.
	 *
	 * @param performanceMeter the performance meter
	 */
	private void addPerformanceMeter(PerformanceMeter performanceMeter) {
		if (fPerformanceMeters == null)
			fPerformanceMeters= new ArrayList<PerformanceMeter>();
		fPerformanceMeters.add(performanceMeter);
	}
}
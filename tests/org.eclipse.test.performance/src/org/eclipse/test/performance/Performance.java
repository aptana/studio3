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
package org.eclipse.test.performance;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;

import org.eclipse.test.internal.performance.InternalDimensions;
import org.eclipse.test.internal.performance.InternalPerformanceMeter;
import org.eclipse.test.internal.performance.NullPerformanceMeter;
import org.eclipse.test.internal.performance.OSPerformanceMeterFactory;
import org.eclipse.test.internal.performance.PerformanceMeterFactory;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.eval.AbsoluteBandChecker;
import org.eclipse.test.internal.performance.eval.AssertChecker;
import org.eclipse.test.internal.performance.eval.Evaluator;
import org.eclipse.test.internal.performance.eval.IEvaluator;
import org.eclipse.test.internal.performance.eval.RelativeBandChecker;
import org.osgi.framework.Bundle;

/**
 * Helper for performance measurements. Currently provides performance meter
 * creation and checking of measurements.
 * 
 * This class is not intended to be subclassed by clients.
 * 
 * @since 3.1
 */
public class Performance {
	
	/**
	 *  A comment kind of a comment that explains a performance degradation.
	 */
	public static final int EXPLAINS_DEGRADATION_COMMENT= 1;

	private static final String PERFORMANCE_METER_FACTORY= "/option/performanceMeterFactory"; //$NON-NLS-1$
	private static final String PERFORMANCE_METER_FACTORY_PROPERTY= "PerformanceMeterFactory"; //$NON-NLS-1$

	private static Performance fgDefault;
	
	private PerformanceMeterFactory fPerformanceMeterFactory;
	private IEvaluator fDefaultEvaluator;

	/** Null performance meter singleton */
	private NullPerformanceMeter fNullPeformanceMeter;
	

	/**
	 * Private constructor to block instance creation.
	 */
	private Performance() {
		// empty
	}
	
	/**
	 * Returns the singleton of <code>Performance</code>
	 * 
	 * @return the singleton of <code>Performance</code>
	 */
	public static Performance getDefault() {
		if (fgDefault == null)
			fgDefault= new Performance();
		return fgDefault;
	}
	
	/**
	 * Asserts default properties of the measurements captured by the given
	 * performance meter.
	 * 
	 * @param performanceMeter the performance meter
	 * @throws RuntimeException if the properties do not hold
	 */
	public void assertPerformance(PerformanceMeter performanceMeter) {
		if (fDefaultEvaluator == null) {
			fDefaultEvaluator= new Evaluator();
			fDefaultEvaluator.setAssertCheckers(new AssertChecker[] {
			        new RelativeBandChecker(InternalDimensions.ELAPSED_PROCESS, 0.0f, 1.10f),
			        //new RelativeBandChecker(InternalDimensions.CPU_TIME, 0.0f, 1.10f),
			        //new RelativeBandChecker(InternalDimensions.WORKING_SET, 0.0f, 3.00f),
			        //new RelativeBandChecker(InternalDimensions.USED_JAVA_HEAP, 0.0f, 2.00f),
			        //new RelativeBandChecker(InternalDimensions.SYSTEM_TIME, 0.0f, 1.10f)
			});
		}
		fDefaultEvaluator.evaluate(performanceMeter);
	}

	/**
	 * Asserts that the measurement specified by the dimension captured in the given
	 * performance meter is within a certain range with respect to some reference value.
	 * If the performance meter doesn't provide the specified dimension, the call has no effect.
	 * 
	 * @param performanceMeter the performance meter
	 * @param dim the Dimension to check
	 * @param lowerPercentage a negative number indicating the percentage the measured value is allowed to be smaller than some reference value
	 * @param upperPercentage a positive number indicating the percentage the measured value is allowed to be greater than some reference value
	 * @throws RuntimeException if the properties do not hold
	 */
	public void assertPerformanceInRelativeBand(PerformanceMeter performanceMeter, Dimension dim, int lowerPercentage, int upperPercentage) {
	    Evaluator e= new Evaluator();
		e.setAssertCheckers(new AssertChecker[] {
		        new RelativeBandChecker((Dim) dim, 1.0+(lowerPercentage / 100.0), 1.0+(upperPercentage / 100.0)),
		});
		e.evaluate(performanceMeter);
	}

	/**
	 * Asserts that the measurement specified by the dimension captured in the given
	 * performance meter is within a certain range with respect to some reference value.
	 * If the performance meter doesn't provide the specified dimension, the call has no effect.
	 * 
	 * @param performanceMeter the performance meter
	 * @param dim the Dimension to check
	 * @param lowerBand a negative number indicating the absolute amount the measured value is allowed to be smaller than some reference value
	 * @param upperBand a positive number indicating the absolute amount the measured value is allowed to be greater than some reference value
	 * @throws RuntimeException if the properties do not hold
	 */
	public void assertPerformanceInAbsoluteBand(PerformanceMeter performanceMeter, Dimension dim, int lowerBand, int upperBand) {
	    Evaluator e= new Evaluator();
		e.setAssertCheckers(new AssertChecker[] {
		        new AbsoluteBandChecker((Dim) dim, lowerBand, upperBand),
		});
		e.evaluate(performanceMeter);
	}

	/**
	 * Creates a performance meter for the given scenario id.
	 * 
	 * @param scenarioId the scenario id
	 * @return a performance meter for the given scenario id
	 * @throws IllegalArgumentException if a performance meter for the given
	 *                 scenario id has already been created
	 */
	public PerformanceMeter createPerformanceMeter(String scenarioId) {
		return getPeformanceMeterFactory().createPerformanceMeter(scenarioId);
	}

	/**
	 * Returns the null performance meter singleton.
	 * 
	 * @return the null performance meter singleton
	 */
	public PerformanceMeter getNullPerformanceMeter() {
		if (fNullPeformanceMeter == null)
			fNullPeformanceMeter= new NullPerformanceMeter();
		return fNullPeformanceMeter;
	}

	/**
	 * Returns a default scenario id for the given test. The test's name
	 * must have been set, such that <code>test.getName()</code> is not
	 * <code>null</code>.
	 * 
	 * @param test the test
	 * @return the default scenario id for the test
	 */
	public String getDefaultScenarioId(TestCase test) {
		return test.getClass().getName() + '#' + test.getName() + "()"; //$NON-NLS-1$
	}
	
	/**
	 * Returns a default scenario id for the given test and id. The test's
	 * name must have been set, such that <code>test.getName()</code> is
	 * not <code>null</code>. The id distinguishes multiple scenarios in
	 * the same test.
	 * 
	 * @param test the test
	 * @param id the id
	 * @return the default scenario id for the test and the id
	 */
	public String getDefaultScenarioId(TestCase test, String id) {
		return getDefaultScenarioId(test) + '-' + id;
	}

	private PerformanceMeterFactory getPeformanceMeterFactory() {
		if (fPerformanceMeterFactory == null)
			fPerformanceMeterFactory= createPerformanceMeterFactory();
		return fPerformanceMeterFactory;
	}
	
	private PerformanceMeterFactory createPerformanceMeterFactory() {
		PerformanceMeterFactory factory;
		factory= tryInstantiate(System.getProperty(PERFORMANCE_METER_FACTORY_PROPERTY));
		if (factory != null)
			return factory;
		
		factory= tryInstantiate(Platform.getDebugOption(PerformanceTestPlugin.PLUGIN_ID + PERFORMANCE_METER_FACTORY));
		if (factory != null)
			return factory;
		
		return createDefaultPerformanceMeterFactory();
	}
	
	private PerformanceMeterFactory tryInstantiate(String className) {
		PerformanceMeterFactory instance= null;
		if (className != null && className.length() > 0) {
			try {
				int separator= className.indexOf(':');
				Bundle bundle= null;
				if (separator == -1) {
					bundle= PerformanceTestPlugin.getDefault().getBundle();
				} else {
					String bundleName= className.substring(0, separator);
					className= className.substring(separator + 1);
					bundle= Platform.getBundle(bundleName);
				}
				Class c= bundle.loadClass(className);
				instance= (PerformanceMeterFactory) c.newInstance();
			} catch (ClassNotFoundException e) {
		        PerformanceTestPlugin.log(e);
			} catch (InstantiationException e) {
		        PerformanceTestPlugin.log(e);
			} catch (IllegalAccessException e) {
		        PerformanceTestPlugin.log(e);
			} catch (ClassCastException e) {
		        PerformanceTestPlugin.log(e);
			}
		}
		return instance;
	}

	private PerformanceMeterFactory createDefaultPerformanceMeterFactory() {
		return new OSPerformanceMeterFactory();
	}
	
	/**
	 * Mark the scenario represented by the given PerformanceMeter
	 * to be included into the global and the component performance summary. The summary shows
	 * the given dimension of the scenario and labels the scenario with the short name.
	 * 
	 * @param pm the PerformanceMeter
	 * @param shortName a short (shorter than 40 characters) descriptive name of the scenario
	 * @param dimension the dimension to show in the summary
	 */
	public void tagAsGlobalSummary(PerformanceMeter pm, String shortName, Dimension dimension) {
	    tagAsGlobalSummary(pm, shortName, new Dimension[] { dimension } );
	}

	/**
	 * Mark the scenario represented by the given PerformanceMeter
	 * to be included into the global and the component performance summary. The summary shows
	 * the given dimensions of the scenario and labels the scenario with the short name.
	 * 
	 * @param pm the PerformanceMeter
	 * @param shortName a short (shorter than 40 characters) descriptive name of the scenario
	 * @param dimensions an array of dimensions to show in the summary
	 */
	public void tagAsGlobalSummary(PerformanceMeter pm, String shortName, Dimension[] dimensions) {
	    if (pm instanceof InternalPerformanceMeter) {
	        InternalPerformanceMeter ipm= (InternalPerformanceMeter) pm;
	        ipm.tagAsSummary(true, shortName, dimensions);
	    }
	}

	
	/**
	 * Mark the scenario represented by the given PerformanceMeter
	 * to be included into the component performance summary. The summary shows
	 * the given dimension of the scenario and labels the scenario with the short name.
	 * 
	 * @param pm the PerformanceMeter
	 * @param shortName a short (shorter than 40 characters) descriptive name of the scenario
	 * @param dimension the dimension to show in the summary
	 */
	public void tagAsSummary(PerformanceMeter pm, String shortName, Dimension dimension) {
	    tagAsSummary(pm, shortName, new Dimension[] { dimension } );
	}

	/**
	 * Mark the scenario represented by the given PerformanceMeter
	 * to be included into the component performance summary. The summary shows
	 * the given dimensions of the scenario and labels the scenario with the short name.
	 * 
	 * @param pm the PerformanceMeter
	 * @param shortName a short (shorter than 40 characters) descriptive name of the scenario
	 * @param dimensions an array of dimensions to show in the summary
	 */
	public void tagAsSummary(PerformanceMeter pm, String shortName, Dimension[] dimensions) {
	    if (pm instanceof InternalPerformanceMeter) {
	        InternalPerformanceMeter ipm= (InternalPerformanceMeter) pm;
	        ipm.tagAsSummary(false, shortName, dimensions);
	    }
	}

	/**
	 * Set a comment for the scenario represented by the given PerformanceMeter.
	 * Currently only comments with a commentKind of EXPLAINS_DEGRADATION_COMMENT are used.
	 * Their commentText is shown in a hover of the performance summaries graph if a performance
	 * degradation exists.
	 * 
	 * @param pm the PerformanceMeter
	 * @param commentKind kind of comment. Must be EXPLAINS_DEGRADATION_COMMENT to have an effect.
	 * @param commentText the comment (shorter than 400 characters)
	 */
	public void setComment(PerformanceMeter pm, int commentKind, String commentText) {
		if (commentKind == EXPLAINS_DEGRADATION_COMMENT) {
		    if (pm instanceof InternalPerformanceMeter) {
		        InternalPerformanceMeter ipm= (InternalPerformanceMeter) pm;
		        ipm.setComment(commentKind, commentText);
		    }
		}
	}
}

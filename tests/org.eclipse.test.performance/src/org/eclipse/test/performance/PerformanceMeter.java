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

/**
 * A <code>PerformanceMeter</code> is used for doing repeated measurements of
 * an arbitrary operation.
 * 
 * The kind of measurement and the retrieval of the results remain internal to
 * the implementation. Measurements can include time, CPU cycle and memory
 * consumption.
 * 
 * A <code>PerformanceMeter</code> is created using the method
 * {@link Performance#createPerformanceMeter(String)}. An operation is measured
 * by calling {@link PerformanceMeter#start()} before and
 * {@link PerformanceMeter#stop()} after that operation. The measurement can be
 * repeated, for example, to let the VM warm up and to allow for statistical
 * analysis afterwards.
 * 
 * After measurements are done and before an analysis of the results can be made
 * {@link PerformanceMeter#commit()} has to be called. This allows for example
 * to prepare the measurements for analysis or persist them.
 * {@link Performance#assertPerformance(PerformanceMeter)} provides a default
 * analysis of the measurements. After the <code>PerformanceMeter</code> is no
 * longer used {@link PerformanceMeter#dispose()} must be called.
 * 
 * Example usage in a test case:
 * <pre>
 * public void testOpenEditor() {
 * 	Performance perf= Performance.getDefault();
 * 	PerformanceMeter performanceMeter= perf.createPerformanceMeter(perf.getDefaultScenarioId(this));
 * 	try {
 * 		for (int i= 0; i &lt; 10; i++) {
 * 			performanceMeter.start();
 * 			openEditor();
 * 			performanceMeter.stop();
 * 			closeEditor();
 * 		}
 * 		performanceMeter.commit();
 * 		perf.assertPerformance(performanceMeter);
 * 	} finally {
 * 		performanceMeter.dispose();
 * 	}
 * }
 * </pre>
 * 
 * This class is not intended to be subclassed by clients.
 */
public abstract class PerformanceMeter {

	/**
	 * Called immediately before the operation to measure. Must be followed
	 * by a call to {@link PerformanceMeter#stop()} before subsequent calls
	 * to this method or {@link PerformanceMeter#commit()}.
	 */
	public abstract void start();

	/**
	 * Called immediately after the operation to measure. Must be preceded
	 * by a call to {@link PerformanceMeter#start()}, that follows any
	 * previous call to this method.
	 */
	public abstract void stop();

	/**
	 * Called exactly once after repeated measurements are done and before
	 * their analysis. Afterwards {@link PerformanceMeter#start()} and
	 * {@link PerformanceMeter#stop()} must not be called.
	 */
	public abstract void commit();

	/**
	 * Dispose associated resources. Clients must call this method exactly
	 * once. Afterwards no methods must be called on the performance meter.
	 */
	public abstract void dispose();
}

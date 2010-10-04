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
package org.eclipse.test.internal.performance.eval;

import org.eclipse.test.performance.PerformanceMeter;

/**
 * @since 3.1
 */
public interface IEvaluator {

	/**
	 * Evaluates the given performance meter by comparing its measurements
	 * against the reference data selected with
	 * <code>setReferenceFilterProperties</code>.
	 * 
	 * @param performanceMeter the performance meter
	 * @throws RuntimeException when the comparison does not pass
	 */
	void evaluate(PerformanceMeter performanceMeter) throws RuntimeException;

	/**
	 * Sets the asserts that should be evaluated by the evaluator.
	 * 
	 * @param asserts the assert checkers that should be evaluated by the evaluator
	 */
	void setAssertCheckers(AssertChecker[] asserts);

	/**
	 * Selects the reference build against which the current session will
	 * be compared in <code>evaluate</code>.
	 * @param driver 
	 * @param timestamp the timestamp of the reference, may be null to use any
	 */
	void setReferenceFilterProperties(String driver, String timestamp);
}

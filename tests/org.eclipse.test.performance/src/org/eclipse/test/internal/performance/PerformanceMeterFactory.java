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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;

import junit.framework.TestCase;

public abstract class PerformanceMeterFactory {
	
	private static Set fScenarios= new HashSet();
	
	public PerformanceMeter createPerformanceMeter(String scenario) {
		assertUniqueScenario(scenario);
		return doCreatePerformanceMeter(scenario);
	}

	public PerformanceMeter createPerformanceMeter(TestCase testCase, String monitorId) {
		return createPerformanceMeter(Performance.getDefault().getDefaultScenarioId(testCase, monitorId));
	}

	public PerformanceMeter createPerformanceMeter(TestCase testCase) {
		return createPerformanceMeter(Performance.getDefault().getDefaultScenarioId(testCase));
	}

	protected abstract PerformanceMeter doCreatePerformanceMeter(String scenario);
	
	private static void assertUniqueScenario(String scenario) {
		if (fScenarios.contains(scenario))
			throw new IllegalArgumentException();
		fScenarios.add(scenario);
	}
}

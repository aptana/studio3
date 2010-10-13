/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.tests;

import org.eclipse.test.internal.performance.OSPerformanceMeter;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;

import junit.framework.TestCase;

public class PerformanceMeterFactoryTest extends TestCase {

    public void testPerformanceMeterFactory() {
		System.setProperty("PerformanceMeterFactory", "org.eclipse.test.performance:org.eclipse.test.internal.performance.OSPerformanceMeterFactory"); //$NON-NLS-1$ //$NON-NLS-2$
		
		PerformanceMeter pm= Performance.getDefault().createPerformanceMeter("scenarioId"); //$NON-NLS-1$
		
		assertTrue(pm instanceof OSPerformanceMeter);
	}
}

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

import junit.framework.TestCase;

import org.eclipse.test.internal.performance.OSPerformanceMeter;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;

public class SimplePerformanceMeterTest extends TestCase {
	
    public void testPerformanceMeterFactory() {
		PerformanceMeter meter= Performance.getDefault().createPerformanceMeter("scenarioId"); //$NON-NLS-1$
		
		assertTrue(meter instanceof OSPerformanceMeter);
	
		meter.start();
		meter.stop();
		
		meter.commit();
		
		meter.dispose();
	}

}

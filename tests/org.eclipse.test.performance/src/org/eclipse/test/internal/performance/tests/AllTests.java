/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

	public static Test suite() {
		TestSuite suite= new TestSuite("Performance Test plugin tests"); //$NON-NLS-1$
		
		//suite.addTestSuite(SimplePerformanceMeterTest.class);
		suite.addTestSuite(VariationsTests.class);
		suite.addTestSuite(DBTests.class);
		suite.addTestSuite(PerformanceMeterFactoryTest.class);
		
		return suite;
	}
}

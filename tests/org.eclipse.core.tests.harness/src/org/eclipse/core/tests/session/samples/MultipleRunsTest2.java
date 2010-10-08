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
package org.eclipse.core.tests.session.samples;

import junit.framework.*;
import org.eclipse.core.tests.harness.CoreTest;
import org.eclipse.core.tests.session.PerformanceSessionTestSuite;
import org.eclipse.core.tests.session.TestDescriptor;

public class MultipleRunsTest2 extends TestCase {
	public static Test suite() {
		PerformanceSessionTestSuite suite = new PerformanceSessionTestSuite(CoreTest.PI_HARNESS, 10);
		suite.addTest(new TestDescriptor(SampleSessionTest.class.getName(), "testApplicationStartup"));
		return suite;
	}
}

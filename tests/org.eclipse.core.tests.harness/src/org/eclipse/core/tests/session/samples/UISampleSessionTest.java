/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.session.samples;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import org.eclipse.core.tests.harness.CoreTest;
import org.eclipse.core.tests.session.SessionTestSuite;
import org.eclipse.test.performance.*;

public class UISampleSessionTest extends TestCase {
	public UISampleSessionTest(String methodName) {
		super(methodName);
	}
	
	/**
	 * Print a debug message to the console. 
	 * Pre-pend the message with the current date and the name of the current thread.
	 */
	public static void message(String message) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(new Date(System.currentTimeMillis()));
		buffer.append(" - ["); //$NON-NLS-1$
		buffer.append(Thread.currentThread().getName());
		buffer.append("] "); //$NON-NLS-1$
		buffer.append(message);
		System.out.println(buffer.toString());
	}

	public void testApplicationStartup() {
		message("Running " + getName());
		PerformanceMeter meter = Performance.getDefault().createPerformanceMeter(getClass().getName() + ".UIStartup");
		try {
			meter.stop();
			meter.commit();
			Performance.getDefault().assertPerformanceInRelativeBand(meter, Dimension.ELAPSED_PROCESS, -50, 5);
		} finally {
			meter.dispose();
		}
	}

	public static Test suite() {
		SessionTestSuite suite = new SessionTestSuite(CoreTest.PI_HARNESS);
		suite.setApplicationId(SessionTestSuite.UI_TEST_APPLICATION);
		for (int i = 0; i < 3; i++)
			suite.addTest(new UISampleSessionTest("testApplicationStartup"));
		return suite;
	}

}

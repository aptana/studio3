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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.core.tests.harness.CoreTest;
import org.eclipse.core.tests.session.SessionTestSuite;

public class SampleTests extends TestSuite {
	public SampleTests() {
		addTest(SampleSessionTest.suite());
		addTest(UISampleSessionTest.suite());
		TestSuite another = new SessionTestSuite(CoreTest.PI_HARNESS);
		another.addTestSuite(SampleSessionTest.class);
		addTest(another);
		// these tests should run in the same session (don't add to a non-shared session test suite)
		SessionTestSuite shared = new SessionTestSuite(CoreTest.PI_HARNESS);
		shared.addTestSuite(SameSessionTest.class);
		shared.setSharedSession(true);
		addTest(shared);
		// play with a crash test
		addTest(SampleCrashTest.suite());
	}

	public static Test suite() {
		return new SampleTests();
	}
}

/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 467667
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Test;
import junit.textui.TestRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class EclipseTestHarnessApplication implements IApplication {
	protected List<String> tests;

	/** command line arguments made available to all tests. */
	public static String[] args;

	/** true if workspace tests should log their deltas */
	private static boolean deltas;

	/** The id of the test harness plug-in */
	private static final String PI_TESTHARNESS = "org.eclipse.core.tests.harness"; //$NON-NLS-1$

	/** the simple id of the tests extension point in the test harness plug-in */
	private static final String PT_TESTS = "tests"; //$NON-NLS-1$

	public EclipseTestHarnessApplication() {
		tests = new ArrayList<>(5);
	}

	public static boolean deltasEnabled() {
		return deltas;
	}

	/**
	 * Finds, creates and returns a prototypical test object for the test with
	 * the given name/id.  Returns <code>null</code> if no such test is found
	 * or the class defined by the test extension could not be found.
	 * In either failure case a message is output on the System console.
	 */
	protected Object findTestFor(String testName) {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(PI_TESTHARNESS, PT_TESTS);
		IConfigurationElement[] elements = point.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			if (element.getName().equals("test")) {
				String id = element.getAttribute("id");
				if (id != null && id.equals(testName)) {
					try {
						return element.createExecutableExtension("run");
					} catch (CoreException e) {
						System.err.println("Could not instantiate test: " + testName);
						e.printStackTrace();
						return null;
					}
				}
			}
		}
		System.out.println("Could not find test: " + testName);
		return null;
	}

	protected String[] processCommandLine(String[] args1) {
		int[] configArgs = new int[100];
		configArgs[0] = -1; // need to initialize the first element to something that could not be an index.
		int configArgIndex = 0;
		for (int i = 0; i < args1.length; i++) {
			boolean found = false;
			// check for args without parameters (i.e., a flag arg)
			// see if we should be logging deltas
			if (args1[i].equalsIgnoreCase("-deltas")) {
				found = true;
				deltas = true;
			}
			if (found) {
				configArgs[configArgIndex++] = i;
				continue;
			}

			// check for args with parameters
			if (i == args1.length - 1 || args1[i + 1].startsWith("-")) {
				continue;
			}

			String arg = args1[++i];
			// check for the which test to run
			if (args1[i - 1].equalsIgnoreCase("-test")) {
				found = true;
				// fully qualified name of the test class to run
				tests.add(arg);
			}

			// done checking for args.  Remember where an arg was found
			if (found) {
				configArgs[configArgIndex++] = i - 1;
				configArgs[configArgIndex++] = i;
			}
		}

		//remove all the arguments consumed by this argument parsing
		if (configArgIndex == 0) {
			return args1;
		}
		String[] passThruArgs = new String[args1.length - configArgIndex];
		configArgIndex = 0;
		int j = 0;
		for (int i = 0; i < args1.length; i++) {
			if (i == configArgs[configArgIndex]) {
				configArgIndex++;
			} else {
				passThruArgs[j++] = args1[i];
			}
		}
		return passThruArgs;
	}

	protected Object run(String testName) throws Exception {
		Object testObject = findTestFor(testName);
		if (testObject == null) {
			return null;
		}
		Class<?> testClass = testObject.getClass();
		Method method = testClass.getDeclaredMethod("suite", new Class[0]); //$NON-NLS-1$
		Test suite = null;
		try {
			suite = (Test) method.invoke(testClass, new Object[0]);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof Error) {
				throw (Error) e.getTargetException();
			}
			throw e;
		}
		run(suite);
		return null;
	}

	protected void run(Test suite) throws Exception {
		TestRunner.run(suite);
	}

	/**
	 * Runs a set of tests as defined by the given command line args. This is
	 * the platform application entry point.
	 */
	@Override
	public Object start(IApplicationContext context) throws Exception {
		args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		processCommandLine(args);
		for (Iterator<String> i = tests.iterator(); i.hasNext();) {
			run(i.next());
		}
		return null;
	}

	@Override
	public void stop() {
		// Nothing to do here
	}
}

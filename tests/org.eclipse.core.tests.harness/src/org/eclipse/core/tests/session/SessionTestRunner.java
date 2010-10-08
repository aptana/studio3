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
package org.eclipse.core.tests.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.tests.harness.CoreTest;

/**
 * This class is responsible for launching JUnit tests on a separate Eclipse session and collect
 * the tests results sent back through a socket . 
 */
public class SessionTestRunner {

	class Result {
		final static int ERROR = 2;
		final static int FAILURE = 1;
		final static int SUCCESS = 0;

		String message;

		String stackTrace;
		Test test;
		int type;

		public Result(Test test) {
			this.test = test;
		}
	}

	/**
	 * Collectors can be used a single time only.
	 */
	class ResultCollector implements Runnable {
		private boolean finished;
		private Result newResult;
		private Map results = new HashMap();
		ServerSocket serverSocket;
		private boolean shouldRun = true;
		private StringBuffer stack;
		private TestResult testResult;
		// tests completed during this session
		private int testsRun;

		ResultCollector(Test test, TestResult testResult) throws IOException {
			serverSocket = new ServerSocket(0);
			this.testResult = testResult;
			initResults(test);
		}

		public int getPort() {
			return serverSocket.getLocalPort();
		}

		public int getTestsRun() {
			return testsRun;
		}

		private void initResults(Test test) {
			if (test instanceof TestSuite) {
				for (Enumeration e = ((TestSuite) test).tests(); e.hasMoreElements();)
					initResults((Test) e.nextElement());
				return;
			}
			results.put(test.toString(), new Result(test));
		}

		public synchronized boolean isFinished() {
			return finished;
		}

		private synchronized void markAsFinished() {
			finished = true;
			notifyAll();
		}

		private String parseTestId(String message) {
			if (message.length() == 0 || message.charAt(0) != '%')
				return null;
			int firstComma = message.indexOf(',');
			if (firstComma == -1)
				return null;
			int secondComma = message.indexOf(',', firstComma + 1);
			if (secondComma == -1)
				secondComma = message.length();
			return message.substring(firstComma + 1, secondComma);
		}

		private void processAvailableMessages(BufferedReader messageReader) throws IOException {
			while (messageReader.ready()) {
				String message = messageReader.readLine();
				processMessage(message);
			}
		}

		private void processMessage(String message) {
			if (message.startsWith("%TESTS")) {
				String testId = parseTestId(message);
				if (!results.containsKey(testId))
					throw new IllegalStateException("Unknown test id: " + testId);
				newResult = (Result) results.get(testId);
				testResult.startTest(newResult.test);
				return;
			}
			if (message.startsWith("%TESTE")) {
				if (newResult.type == Result.FAILURE)
					testResult.addFailure(newResult.test, new RemoteAssertionFailedError(newResult.message, newResult.stackTrace));
				else if (newResult.type == Result.ERROR)
					testResult.addError(newResult.test, new RemoteTestException(newResult.message, newResult.stackTrace));
				testResult.endTest(newResult.test);
				testsRun++;
				newResult = null;
				return;
			}
			if (message.startsWith("%ERROR")) {
				newResult.type = Result.ERROR;
				newResult.message = "";
				return;
			}
			if (message.startsWith("%FAILED")) {
				newResult.type = Result.FAILURE;
				newResult.message = "";
				return;
			}
			if (message.startsWith("%TRACES")) {
				// just create the string buffer that will hold all the frames of the stack trace
				stack = new StringBuffer();
				return;
			}
			if (message.startsWith("%TRACEE")) {
				// stack trace fully read - fill the slot in the result object and reset the string buffer
				newResult.stackTrace = stack.toString();
				stack = null;
				return;
			}
			if (message.startsWith("%"))
				// ignore any other messages
				return;
			if (stack != null) {
				// build the stack trace line by line
				stack.append(message);
				stack.append(System.getProperty("line.separator"));
				return;
			}
		}

		public void run() {
			Socket connection = null;
			try {
				// someone asked us to stop before we could do anything
				if (!shouldRun())
					return;
				try {
					connection = serverSocket.accept();
				} catch (SocketException se) {
					if (!shouldRun())
						// we have been finished without ever getting any connections
						// no need to throw exception
						return;
					// something else stopped us
					throw se;
				}
				BufferedReader messageReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				try {
					// main loop
					while (true) {
						synchronized (this) {
							processAvailableMessages(messageReader);
							if (!shouldRun())
								return;
							this.wait(150);
						}
					}
				} catch (InterruptedException e) {
					// not expected
				}
			} catch (IOException e) {
				CoreTest.log(CoreTest.PI_HARNESS, e);
			} finally {
				// remember we are already finished
				markAsFinished();
				// cleanup
				try {
					if (connection != null && !connection.isClosed())
						connection.close();
				} catch (IOException e) {
					CoreTest.log(CoreTest.PI_HARNESS, e);
				}
				try {
					if (serverSocket != null && !serverSocket.isClosed())
						serverSocket.close();
				} catch (IOException e) {
					CoreTest.log(CoreTest.PI_HARNESS, e);
				}
			}
		}

		private synchronized boolean shouldRun() {
			return shouldRun;
		}

		/*
		 * Politely asks the collector thread to stop and wait until it is finished.
		 */
		public void shutdown() {
			// ask the collector to stop
			synchronized (this) {
				if (isFinished())
					return;
				shouldRun = false;
				try {
					serverSocket.close();
				} catch (IOException e) {
					CoreTest.log(CoreTest.PI_HARNESS, e);
				}
				notifyAll();
			}
			// wait until the collector is done
			synchronized (this) {
				while (!isFinished())
					try {
						wait(100);
					} catch (InterruptedException e) {
						// we don't care
					}
			}
		}

	}

	/**
	 * Runs the setup. Returns a status object indicating the outcome of the operation.
	 *   
	 * @return a status object indicating the outcome 
	 */
	private IStatus launch(Setup setup) {
		Assert.isNotNull(setup.getEclipseArgument(Setup.APPLICATION), "test application is not defined");
		Assert.isNotNull(setup.getEclipseArgument("testpluginname"), "test plug-in id not defined");
		Assert.isTrue(setup.getEclipseArgument("classname") != null ^ setup.getEclipseArgument("test") != null, "either a test suite or a test case must be provided");
		// to prevent changes in the protocol from breaking us, 
		// force the version we know we can work with 
		setup.setEclipseArgument("version", "3");
		IStatus outcome = Status.OK_STATUS;
		try {
			int returnCode = setup.run();
			if (returnCode != 0)
				outcome = new Status(IStatus.WARNING, Platform.PI_RUNTIME, returnCode, "Process returned non-zero code: " + returnCode + "\n\tCommand: " + setup, null);
		} catch (Exception e) {
			outcome = new Status(IStatus.ERROR, Platform.PI_RUNTIME, -1, "Error running process\n\tCommand: " + setup, e);
		}
		return outcome;
	}

	/**
	 * Runs the test described  in a separate session.
	 */
	public final void run(Test test, TestResult result, Setup setup, boolean crashTest) {
		ResultCollector collector = null;
		try {
			collector = new ResultCollector(test, result);
		} catch (IOException e) {
			result.addError(test, e);
			return;
		}
		setup.setEclipseArgument("port", Integer.toString(collector.getPort()));
		new Thread(collector, "Test result collector").start();
		IStatus status = launch(setup);
		collector.shutdown();
		// ensure the session ran without any errors
		if (!status.isOK()) {
			CoreTest.log(CoreTest.PI_HARNESS, status);
			if (status.getSeverity() == IStatus.ERROR) {
				result.addError(test, new CoreException(status));
				return;
			}
		}
		if (collector.getTestsRun() == 0) {
			if (crashTest)
				// explicitly end test since process crashed before test could finish
				result.endTest(test);
			else
				result.addError(test, new Exception("Test did not run: " + test.toString()));
		} else if (crashTest)
			result.addError(test, new Exception("Should have caused crash"));
	}
}

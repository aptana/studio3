/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import junit.framework.TestFailure;

/**
 * A LoggingPerformanceTestResult adds the ability to create an HTML or
 * other output file, and have test results written to that file instead
 * of the standard output.
 */
public class LoggingPerformanceTestResult extends PerformanceTestResult {
	private static PrintWriter createWriter(File logFile) {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(logFile), true);
			return writer;
		} catch (IOException e) {
			System.out.println("Unable to open log output file: " + logFile);
			return new PrintWriter(System.out, true);
		}
	}

	public LoggingPerformanceTestResult(File logFile) {
		super(createWriter(logFile));
		printHTMLHeader(output);
	}

	/**
	 * Logs the given string in the test log file
	 */
	public synchronized void log(String s) {
		output.println(s);
	}

	/**
	 * Prints the test result
	 */
	public synchronized void print() {
		try {
			super.print();
			printHTMLTrailer(output);
		} finally {
			output.flush();
			output.close();
		}
	}

	/**
	 * Prints the errors to the standard output
	 */
	protected void printErrors(PrintWriter out) {
		out.println("<h3>Error summary</h3>");
		int count = errorCount();
		if (count != 0) {
			if (count == 1)
				out.println("There was " + count + " error:<p>");
			else
				out.println("There were " + count + " errors:<p>");
			int i = 1;
			for (Enumeration e = errors(); e.hasMoreElements(); i++) {
				TestFailure failure = (TestFailure) e.nextElement();
				out.println(i + ") " + failure.failedTest() + "<p>");
				failure.thrownException().printStackTrace(out);
				out.println("<p>");
			}
		} else {
			out.println("No errors reported.");
		}
	}

	/**
	 * Prints the failures to the output
	 */
	protected void printFailures(PrintWriter out) {
		out.println("<h3>Failure summary</h3>");
		int count = failureCount();
		if (count != 0) {
			if (count == 1)
				out.println("There was " + count + " failure:<p>");
			else
				out.println("There were " + count + " failures:<p>");
			int i = 1;
			for (Enumeration e = failures(); e.hasMoreElements(); i++) {
				TestFailure failure = (TestFailure) e.nextElement();
				out.println(i + ") " + failure.failedTest() + "<p>");
				failure.thrownException().printStackTrace(out);
				out.println("<p>");
			}
		} else {
			out.println("No failures reported.");
		}
	}

	/**
	 * Prints the header of the report
	 */
	protected void printHeader(PrintWriter out) {
	}

	/**
	 * Prints the header of the report
	 */
	protected void printHTMLHeader(PrintWriter out) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n<head>\n<title>Eclipse Performance Test Output Page</title>");
		buf.append("</head>\n<body>\n");
		out.println(buf.toString());
	}

	/**
	 * Prints the header of the report
	 */
	protected void printHTMLTrailer(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}

	/**
	 * Prints the timings of the result.
	 */

	protected void printTimings(PrintWriter out) {
		out.println("<h3>Timing summary</h3>");
		out.println("<ul>");

		// print out all timing results to the console
		for (Iterator it = timerList.iterator(); it.hasNext();) {
			PerformanceTimer timer = (PerformanceTimer) it.next();
			out.println("<li>" + timer.getName() + " : " + timer.getElapsedTime() + " ms</li>");
		}
		out.println("</ul>");
	}

	/**
	 * Start the timer with the given name.  If the timer has already
	 * been created, send it a startTiming message.  If not, create it
	 * and send the new timer the startTiming message.
	 */

	public synchronized void startTimer(String timerName) {
		super.startTimer(timerName);
		//log("Starting timer: " + timerName);
	}

	/**
	 * Look up the timer with the given name and send it a stopTiming
	 * message.  If the timer does not exist, report an error.
	 */

	public synchronized void stopTimer(String timerName) {
		super.stopTimer(timerName);
		//log("Stopping timer: " + timerName);
	}
}

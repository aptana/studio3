/*******************************************************************************
 * Copyright (c) 2005, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Fran Litterio (Wind River) - initial API and implementation
 * Ted Williams (Wind River) - refactored into org.eclipse namespace
 * Michael Scharf (Wind River) - split into core, view and connector plugins
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.provisional.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tm.internal.terminal.control.impl.TerminalPlugin;

/**
 * A simple logger class. Every method in this class is static, so they can be
 * called from both class and instance methods. To use this class, write code
 * like this:
 * <p>
 *
 * <pre>
 * Logger.log(&quot;something has happened&quot;);
 * Logger.log(&quot;counter is &quot; + counter);
 * </pre>
 *
 * @author Fran Litterio <francis.litterio@windriver.com>
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API without
 * consulting with the <a href="http://www.eclipse.org/dsdp/tm/">Target Management</a> team.
 * </p>
 */
public final class Logger {
    public static final String  TRACE_DEBUG_LOG                = "org.eclipse.tm.terminal/debug/log"; //$NON-NLS-1$
    public static final String  TRACE_DEBUG_LOG_ERROR          = "org.eclipse.tm.terminal/debug/log/error"; //$NON-NLS-1$
    public static final String  TRACE_DEBUG_LOG_INFO           = "org.eclipse.tm.terminal/debug/log/info"; //$NON-NLS-1$
    public static final String  TRACE_DEBUG_LOG_CHAR           = "org.eclipse.tm.terminal/debug/log/char"; //$NON-NLS-1$
    public static final String  TRACE_DEBUG_LOG_BUFFER_SIZE    = "org.eclipse.tm.terminal/debug/log/buffer/size"; //$NON-NLS-1$

    private static PrintStream logStream;

	static {
		String logFile = null;
		//TODO I think this should go into the workspace metadata instead.
		File logDirWindows = new File("C:\\eclipselogs"); //$NON-NLS-1$
		File logDirUNIX = new File("/tmp/eclipselogs"); //$NON-NLS-1$

		if (logDirWindows.isDirectory()) {
			logFile = logDirWindows + "\\tmterminal.log"; //$NON-NLS-1$
		} else if (logDirUNIX.isDirectory()) {
			logFile = logDirUNIX + "/tmterminal.log"; //$NON-NLS-1$
		}

		if (logFile != null) {
			try {
				logStream = new PrintStream(new FileOutputStream(logFile, true));
			} catch (Exception ex) {
				logStream = System.err;
				logStream
						.println("Exception when opening log file -- logging to stderr!"); //$NON-NLS-1$
				ex.printStackTrace(logStream);
			}
		}
	}

	/**
	 * Encodes a String such that non-printable control characters are
	 * converted into user-readable escape sequences for logging.
	 * @param message String to encode
	 * @return encoded String
	 */
	public static final String encode(String message) {
		boolean encoded = false;
		StringBuffer buf = new StringBuffer(message.length()+32);
		for (int i=0; i<message.length(); i++) {
			char c=message.charAt(i);
			switch(c) {
				case '\\':
				case '\'':
					buf.append('\\'); buf.append(c); encoded=true;
					break;
				case '\r':
					buf.append('\\'); buf.append('r'); encoded=true;
					break;
				case '\n':
					buf.append('\\'); buf.append('n'); encoded=true;
					break;
				case '\t':
					buf.append('\\'); buf.append('t'); encoded=true;
					break;
				case '\f':
					buf.append('\\'); buf.append('f'); encoded=true;
					break;
				case '\b':
					buf.append('\\'); buf.append('b'); encoded=true;
					break;
				default:
					if (c <= '\u000f') {
						buf.append('\\'); buf.append('x'); buf.append('0');
						buf.append(Integer.toHexString(c));
						encoded=true;
					} else if (c>=' ' && c<'\u007f') {
						buf.append(c);
					} else if (c <= '\u00ff') {
							buf.append('\\'); buf.append('x');
							buf.append(Integer.toHexString(c));
							encoded=true;
					} else {
						buf.append('\\'); buf.append('u');
						if (c<='\u0fff') {
							buf.append('0');
						}
						buf.append(Integer.toHexString(c));
						encoded=true;
					}
			}
		}
		if (encoded) {
			return buf.toString();
		}
		return message;
	}

	/**
	 * Checks if logging is enabled.
	 * @return true if logging is enabled.
	 */
	public static final boolean isLogEnabled() {
		return (logStream!=null);
	}

	/**
	 * Logs the specified message. Do not append a newline to parameter
	 * <i>message</i>. This method does that for you.
	 *
     * @param message           A String containing the message to log.
	 */
	public static final void log(String message) {
		if (logStream != null) {
			// Read my own stack to get the class name, method name, and line
			// number of
			// where this method was called.

			StackTraceElement caller = new Throwable().getStackTrace()[1];
			int lineNumber = caller.getLineNumber();
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			className = className.substring(className.lastIndexOf('.') + 1);

            logStream.println(className + "." + methodName + ":" + lineNumber + ": " + message);   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
			logStream.flush();
		}
	}

	/**
	 * Writes a stack trace for an exception to both Standard Error and to the
	 * log file.
	 */
	public static final void logException(Exception ex) {
		// log in eclipse error log
		if (TerminalPlugin.getDefault() != null) {
			TerminalPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, TerminalPlugin.PLUGIN_ID, IStatus.OK, ex.getMessage(), ex));
		} else {
			ex.printStackTrace();
		}
		// Additional Tracing for debug purposes:
		// Read my own stack to get the class name, method name, and line number
		// of where this method was called
		if(logStream!=null) {
			StackTraceElement caller = new Throwable().getStackTrace()[1];
			int lineNumber = caller.getLineNumber();
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			className = className.substring(className.lastIndexOf('.') + 1);

			PrintStream tmpStream = System.err;

			if (logStream != null) {
				tmpStream = logStream;
			}

			tmpStream.println(className
					+ "." + methodName + ":" + lineNumber + ": " + //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					"Caught exception: " + ex); //$NON-NLS-1$
			ex.printStackTrace(tmpStream);
		}
	}
}

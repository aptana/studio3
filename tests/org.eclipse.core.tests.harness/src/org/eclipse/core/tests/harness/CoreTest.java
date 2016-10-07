/*******************************************************************************
 * Copyright (c) 2004, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *	IBM Corporation - initial API and implementation
 *	Martin Oberhuber (Wind River) - [232426] createSymLink() method
 *	Martin Oberhuber (Wind River) - [335864] ResourceAttributeTest fails on Win7
 *	Sergey Prigogin (Google) - [440283] Modify symlink tests to run on Windows with or without administrator privileges
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Version;

/**
 * @since 3.1
 */
public class CoreTest extends TestCase {
	private static Boolean canCreateSymLinks;

	/** counter for generating unique random file system locations */
	protected static int nextLocationCounter = 0;

	// plug-in identified for the core.tests.harness plug-in.
	public static final String PI_HARNESS = "org.eclipse.core.tests.harness";

	public static void debug(String message) {
		String id = "org.eclipse.core.tests.harness/debug";
		String option = Platform.getDebugOption(id);
		if (Boolean.TRUE.toString().equalsIgnoreCase(option))
			System.out.println(message);
	}

	/**
	 * Fails the test due to the given throwable.
	 */
	public static void fail(String message, Throwable e) {
		// If the exception is a CoreException with a multistatus
		// then print out the multistatus so we can see all the info.
		if (e instanceof CoreException) {
			IStatus status = ((CoreException) e).getStatus();
			//if the status does not have an exception, print the stack for this one
			if (status.getException() == null)
				e.printStackTrace();
			write(status, 0);
		} else
			e.printStackTrace();
		AssertionFailedError assertFail = new AssertionFailedError(message + ": " + e);
		assertFail.initCause(e);
		throw assertFail;
	}

	private static void indent(OutputStream output, int indent) {
		for (int i = 0; i < indent; i++)
			try {
				output.write("\t".getBytes());
			} catch (IOException e) {
				// ignore
			}
	}

	public static void log(String pluginID, IStatus status) {
		Platform.getLog(Platform.getBundle(pluginID)).log(status);
	}

	public static void log(String pluginID, Throwable e) {
		log(pluginID, new Status(IStatus.ERROR, pluginID, IStatus.ERROR, "Error", e)); //$NON-NLS-1$
	}

	private static void write(IStatus status, int indent) {
		PrintStream output = System.out;
		indent(output, indent);
		output.println("Severity: " + status.getSeverity());

		indent(output, indent);
		output.println("Plugin ID: " + status.getPlugin());

		indent(output, indent);
		output.println("Code: " + status.getCode());

		indent(output, indent);
		output.println("Message: " + status.getMessage());

		if (status.getException() != null) {
			indent(output, indent);
			output.print("Exception: ");
			status.getException().printStackTrace(output);
		}

		if (status.isMultiStatus()) {
			IStatus[] children = status.getChildren();
			for (int i = 0; i < children.length; i++)
				write(children[i], indent + 1);
		}
	}

	public CoreTest() {
		super();
	}

	public CoreTest(String name) {
		super(name);
	}

	/**
	 * Asserts that a stream closes successfully. Null streams
	 * are ignored, but failure to close the stream is reported as
	 * an assertion failure.
	 * @since 3.2
	 */
	protected void assertClose(InputStream stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (IOException e) {
			fail("Failed close in assertClose", e);
		}
	}

	/**
	 * Asserts that a stream closes successfully. Null streams
	 * are ignored, but failure to close the stream is reported as
	 * an assertion failure.
	 * @since 3.2
	 */
	protected void assertClose(OutputStream stream) {
		if (stream == null)
			return;
		try {
			stream.close();
		} catch (IOException e) {
			fail("Failed close in assertClose", e);
		}
	}

	protected void assertEquals(String message, Object[] expected, Object[] actual) {
		if (expected == null && actual == null)
			return;
		if (expected == null || actual == null)
			fail(message);
		if (expected.length != actual.length)
			fail(message);
		for (int i = 0; i < expected.length; i++)
			assertEquals(message, expected[i], actual[i]);
	}

	protected void assertEquals(String message, Object[] expected, Object[] actual, boolean orderImportant) {
		// if the order in the array must match exactly, then call the other method
		if (orderImportant) {
			assertEquals(message, expected, actual);
			return;
		}
		// otherwise use this method and check that the arrays are equal in any order
		if (expected == null && actual == null)
			return;
		if (expected == actual)
			return;
		if (expected == null || actual == null)
			assertTrue(message + ".1", false);
		if (expected.length != actual.length)
			assertTrue(message + ".2", false);
		boolean[] found = new boolean[expected.length];
		for (int i = 0; i < expected.length; i++) {
			for (int j = 0; j < expected.length; j++) {
				if (!found[j] && expected[i].equals(actual[j]))
					found[j] = true;
			}
		}
		for (int i = 0; i < found.length; i++)
			if (!found[i])
				assertTrue(message + ".3." + i, false);
	}

	/**
	 * Create the given file in the file system.
	 */
	public void createFileInFileSystem(File file, InputStream contents) throws IOException {
		file.getParentFile().mkdirs();
		FileOutputStream output = new FileOutputStream(file);
		transferData(contents, output);
	}

	/**
	 * Creates a symbolic link.
	 * Should only be called on platforms where symbolic links can actually
	 * be created, i.e. an "ln" command is available.
	 * @param basedir folder in which the symbolic link should be created
	 * @param linkName name of the symbolic link
	 * @param linkTarget target to which the symbolic link should point
	 * @param isDir <code>true</code> if the link should point to a folder
	 * @throws AssertionFailedError if creation of the symbolic link failed
	 */
	protected void createSymLink(File basedir, String linkName, String linkTarget, boolean isDir) {
		// Deliberately use an empty environment to make the test reproducible.
		String[] envp = {};
		try {
			Process p;
			if (isWindowsVistaOrHigher()) {
				if (isDir) {
					String[] cmd = {"cmd", "/c", "mklink", "/d", linkName, linkTarget};
					p = Runtime.getRuntime().exec(cmd, envp, basedir);
				} else {
					String[] cmd = {"cmd", "/c", "mklink", linkName, linkTarget};
					p = Runtime.getRuntime().exec(cmd, envp, basedir);
				}
			} else {
				String[] cmd = {"ln", "-s", linkTarget, linkName};
				p = Runtime.getRuntime().exec(cmd, envp, basedir);
			}
			int exitcode = p.waitFor();
			if (exitcode != 0) {
				String result = new BufferedReader(new InputStreamReader(p.getErrorStream())).readLine();
				assertEquals("createSymLink: " + result + ", exitcode", 0, exitcode);
			}
		} catch (IOException e) {
			fail("createSymLink", e);
		} catch (InterruptedException e) {
			fail("createSymLink", e);
		}
	}

	/**
	 * Checks whether it is possible for a test to create a symbolic link.
	 *
	 * @return <code>true</code> if symbolic links can be created by a test
	 */
	protected boolean canCreateSymLinks() {
		if (canCreateSymLinks == null) {
			if (isWindowsVistaOrHigher()) {
				// Creation of a symbolic link on Windows requires administrator privileges,
				// so it may or may not be possible.
				IPath tempDir = getTempDir();
				String linkName = FileSystemHelper.getRandomLocation(tempDir).lastSegment();
				try {
					// Try to create a symlink.
					createSymLink(tempDir.toFile(), linkName, "testTarget", false);
					// Clean up if the link was created.
					new File(tempDir.toFile(), linkName).delete();
					canCreateSymLinks = Boolean.TRUE;
				} catch (AssertionFailedError e) {
					// This exception indicates that creation of the symlink failed.
					canCreateSymLinks = Boolean.FALSE;
				}
			} else {
				canCreateSymLinks = Boolean.TRUE;
			}
		}
		return canCreateSymLinks.booleanValue();
	}

	protected void ensureDoesNotExistInFileSystem(java.io.File file) {
		FileSystemHelper.clear(file);
	}

	public InputStream getContents(java.io.File target, String errorCode) {
		try {
			return new FileInputStream(target);
		} catch (IOException e) {
			fail(errorCode, e);
		}
		return null; // never happens
	}

	/**
	 * Return an input stream with some the specified text to use
	 * as contents for a file resource.
	 */
	public InputStream getContents(String text) {
		return new ByteArrayInputStream(text.getBytes());
	}

	public IProgressMonitor getMonitor() {
		return new FussyProgressMonitor();
	}

	/**
	 * Return an input stream with some random text to use
	 * as contents for a file resource.
	 */
	public InputStream getRandomContents() {
		return new ByteArrayInputStream(getRandomString().getBytes());
	}

	/**
	 * Returns a unique location on disk.  It is guaranteed that no file currently
	 * exists at that location.  The returned location will be unique with respect
	 * to all other locations generated by this method in the current session.
	 * If the caller creates a folder or file at this location, they are responsible for
	 * deleting it when finished.
	 */
	public IPath getRandomLocation() {
		return FileSystemHelper.getRandomLocation(getTempDir());
	}

	/**
	 * Return String with some random text to use
	 * as contents for a file resource.
	 */
	public String getRandomString() {
		switch ((int) Math.round(Math.random() * 10)) {
			case 0 :
				return "este e' o meu conteudo (portuguese)";
			case 1 :
				return "ho ho ho";
			case 2 :
				return "I'll be back";
			case 3 :
				return "don't worry, be happy";
			case 4 :
				return "there is no imagination for more sentences";
			case 5 :
				return "Alexandre Bilodeau, Canada's first home gold. 14/02/2010";
			case 6 :
				return "foo";
			case 7 :
				return "bar";
			case 8 :
				return "foobar";
			case 9 :
				return "case 9";
			default :
				return "these are my contents";
		}
	}

	public IPath getTempDir() {
		return FileSystemHelper.getTempDir();
	}

	public String getUniqueString() {
		return System.currentTimeMillis() + "-" + Math.random();
	}

	protected static boolean isWindowsMinVersion(int major, int minor, int micro) {
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			try {
				Version v = Version.parseVersion(System.getProperty("org.osgi.framework.os.version")); //$NON-NLS-1$
				System.out.println("Windows version: " + Version.parseVersion(System.getProperty("org.osgi.framework.os.version")));
				return v.compareTo(new Version(major, minor, micro)) >= 0;
			} catch (IllegalArgumentException e) {
				/* drop down to returning false */
			}

		}
		return false;
	}

	/**
	 * Test if running on Windows Vista or higher.
	 * @return <code>true</code> if running on Windows Vista or higher.
	 */
	protected static boolean isWindowsVistaOrHigher() {
		return isWindowsMinVersion(6, 0, 0);
	}

	/**
	 * Copy the data from the input stream to the output stream.
	 * Close both streams when finished.
	 */
	public void transferData(InputStream input, OutputStream output) {
		try {
			try {
				int c = 0;
				while ((c = input.read()) != -1)
					output.write(c);
			} finally {
				input.close();
				output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
		}
	}

	/**
	 * Copy the data from the input stream to the output stream.
	 * Do not close either of the streams.
	 */
	public void transferDataWithoutClose(InputStream input, OutputStream output) {
		try {
			int c = 0;
			while ((c = input.read()) != -1)
				output.write(c);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
		}
	}

	public static void assertSame(String message, int expected, int actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	public static void failNotSame(String message, int expected, int actual) {
		StringBuffer formatted = new StringBuffer();
		if (message != null) {
			formatted.append(message).append(' ');
		}
		formatted.append("expected same:<").append(expected).append("> was not:<").append(actual).append(">");
		fail(String.valueOf(formatted));
	}

	public static void assertSame(String message, boolean expected, boolean actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	public static void failNotSame(String message, boolean expected, boolean actual) {
		StringBuffer formatted = new StringBuffer();
		if (message != null) {
			formatted.append(message).append(' ');
		}
		formatted.append("expected same:<").append(expected).append("> was not:<").append(actual).append(">");
		fail(String.valueOf(formatted));
	}

	public static void assertSame(String message, float expected, float actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	public static void failNotSame(String message, float expected, float actual) {
		StringBuffer formatted = new StringBuffer();
		if (message != null) {
			formatted.append(message).append(' ');
		}
		formatted.append("expected same:<").append(expected).append("> was not:<").append(actual).append(">");
		fail(String.valueOf(formatted));
	}

	public static void assertSame(String message, double expected, double actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	public static void failNotSame(String message, double expected, double actual) {
		StringBuffer formatted = new StringBuffer();
		if (message != null) {
			formatted.append(message).append(' ');
		}
		formatted.append("expected same:<").append(expected).append("> was not:<").append(actual).append(">");
		fail(String.valueOf(formatted));
	}

	public static void assertSame(String message, long expected, long actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	public static void failNotSame(String message, long expected, long actual) {
		StringBuffer formatted = new StringBuffer();
		if (message != null) {
			formatted.append(message).append(' ');
		}
		formatted.append("expected same:<").append(expected).append("> was not:<").append(actual).append(">");
		fail(String.valueOf(formatted));
	}
}

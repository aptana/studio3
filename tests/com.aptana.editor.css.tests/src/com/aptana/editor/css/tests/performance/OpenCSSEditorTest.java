/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests.performance;

import java.io.File;
import java.net.URL;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.eclipse.ui.PartInitException;

import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.epl.tests.OpenEditorTest;
import com.aptana.editor.epl.tests.ResourceTestHelper;

public class OpenCSSEditorTest extends OpenEditorTest
{

	private static final String PROJECT = "css_perf";
	private static final int WARM_UP_RUNS = 10;
	private static final int MEASURED_RUNS = 50;
	private static final String FILE_PREFIX = "yui";
	private static final String PREFIX = "/" + PROJECT + "/" + FILE_PREFIX;
	private static final String FILE_SUFFIX = ".css";
	private static final String LARGE_MINIFIED_FILE = "/" + PROJECT + "/wp-admin.css";
	private static final String LARGE_FILE = "/" + PROJECT + "/wp-admin.dev.css";

	public OpenCSSEditorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		// ensure sequence
		TestSuite suite = new TestSuite(OpenCSSEditorTest.class.getName());
		suite.addTest(new OpenCSSEditorTest("testOpenCSSEditor1"));
		suite.addTest(new OpenCSSEditorTest("testOpenCSSEditor2"));
		suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOnOutlineOn"));
		// suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOffOutlineOn"));
		suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOnOutlineOff"));
		// suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOffOutlineOff"));
		suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOnOutlineOn"));
		// suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOffOutlineOn"));
		suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOnOutlineOff"));
		// suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOffOutlineOff"));
		return new Setup(suite);
	}

	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		EditorTestHelper.runEventQueue();
		setWarmUpRuns(WARM_UP_RUNS);
		setMeasuredRuns(MEASURED_RUNS);
	}

	/*
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		EditorTestHelper.closeAllEditors();
	}

	public void testOpenCSSEditor1() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, 0, getWarmUpRuns()), Performance
				.getDefault().getNullPerformanceMeter(), false);
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	public void testOpenCSSEditor2() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, 0, getWarmUpRuns()), Performance
				.getDefault().getNullPerformanceMeter(), false);
		PerformanceMeter performanceMeter = createPerformanceMeter();
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				performanceMeter, false);
	}

	public void testOpenLargeFileFoldingOnOutlineOn() throws Exception
	{
		PerformanceMeter performanceMeter = createPerformanceMeter();
		measureOpenInEditor(LARGE_FILE, true, true, performanceMeter);
	}

	// public void testOpenLargeFileFoldingOffOutlineOn() throws Exception
	// {
	// measureOpenInEditor(LARGE_FILE, false, true, createPerformanceMeter());
	// }

	public void testOpenLargeFileFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(LARGE_FILE, true, false, createPerformanceMeter());
	}

	// public void testOpenLargeFileFoldingOffOutlineOff() throws Exception
	// {
	// measureOpenInEditor(LARGE_FILE, false, false, createPerformanceMeter());
	// }

	public void testOpenLargeMinifiedFileFoldingOnOutlineOn() throws Exception
	{
		PerformanceMeter performanceMeter = createPerformanceMeter();
		measureOpenInEditor(LARGE_MINIFIED_FILE, true, true, performanceMeter);
	}

	// public void testOpenLargeMinifiedFileFoldingOffOutlineOn() throws Exception
	// {
	// measureOpenInEditor(LARGE_MINIFIED_FILE, false, true, createPerformanceMeter());
	// }

	public void testOpenLargeMinifiedFileFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(LARGE_MINIFIED_FILE, true, false, createPerformanceMeter());
	}

	// public void testOpenLargeMinifiedFileFoldingOffOutlineOff() throws Exception
	// {
	// measureOpenInEditor(LARGE_MINIFIED_FILE, false, false, createPerformanceMeter());
	// }

	protected void measureOpenInEditor(String file, boolean enableFolding, boolean showOutline,
			PerformanceMeter performanceMeter) throws PartInitException
	{
		boolean shown = EditorTestHelper.isViewShown(EditorTestHelper.OUTLINE_VIEW_ID);
		try
		{
			EditorTestHelper.enableFolding(enableFolding);
			showOutline(showOutline);
			measureOpenInEditor(file, performanceMeter);
		}
		finally
		{
			EditorTestHelper.resetFolding();
			showOutline(shown);
		}
	}

	private boolean showOutline(boolean show) throws PartInitException
	{
		return EditorTestHelper.showView(EditorTestHelper.OUTLINE_VIEW_ID, show);
	}

	/**
	 * Setup and teardown done once for the whole suite.
	 * 
	 * @author cwilliams
	 */
	public static class Setup extends TestSetup
	{

		private boolean fSetPerspective;
		private boolean fTearDown;

		public Setup(Test test)
		{
			this(test, true, true);
		}

		public Setup(Test test, boolean tearDown, boolean setPerspective)
		{
			super(test);
			fTearDown = tearDown;
			fSetPerspective = setPerspective;
		}

		protected void setUp() throws Exception
		{
			EditorTestHelper.showView(EditorTestHelper.INTRO_VIEW_ID, false);

			if (fSetPerspective)
				EditorTestHelper.showPerspective(EditorTestHelper.WEB_PERSPECTIVE_ID);

			if (!ResourceTestHelper.projectExists(PROJECT))
			{
				// boolean wasAutobuilding = CoreUtility.setAutoBuilding(false);
				setUpProject();
				// ResourceTestHelper.fullBuild();
				// if (wasAutobuilding)
				// CoreUtility.setAutoBuilding(true);

				EditorTestHelper.joinBackgroundActivities();
			}
			ResourceTestHelper.replicate(PREFIX + FILE_SUFFIX, PREFIX, FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS,
					FILE_PREFIX, FILE_PREFIX, ResourceTestHelper.SKIP_IF_EXISTS);
		}

		private void setUpProject() throws Exception
		{
			IProject project = ResourceTestHelper.createExistingProject(PROJECT);
			// Copy all project contents from under "performance"
			URL perfFolderURL = FileLocator.find(Platform.getBundle("com.aptana.editor.css.tests"),
					Path.fromPortableString("performance"), null);
			perfFolderURL = FileLocator.toFileURL(perfFolderURL);
			File perFolder = new File(perfFolderURL.toURI());
			File[] children = perFolder.listFiles();
			for (File child : children)
			{
				IFile file = project.getFile(child.getName());
				file.create(
						FileLocator.openStream(Platform.getBundle("com.aptana.editor.css.tests"),
								Path.fromPortableString("performance/" + child.getName()), false), true, null);
			}
			assertTrue(project.exists());
		}

		protected void tearDown() throws Exception
		{
			if (fTearDown)
				ResourceTestHelper.delete(PREFIX, FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
		}
	}
}
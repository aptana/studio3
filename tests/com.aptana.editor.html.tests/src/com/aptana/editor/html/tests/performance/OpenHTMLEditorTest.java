/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests.performance;

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

public class OpenHTMLEditorTest extends OpenEditorTest
{

	private static final String PROJECT = "performance_project";
	private static final int WARM_UP_RUNS = 10;
	private static final int MEASURED_RUNS = 50;
	private static final String PATH = "/";
	private static final String FILE_PREFIX = "amazon";
	private static final String PREFIX = "/" + PROJECT + PATH + FILE_PREFIX;
	private static final String FILE_SUFFIX = ".html";
	private static final String LARGE_FILE = PREFIX + FILE_SUFFIX;

	public OpenHTMLEditorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		// ensure sequence
		TestSuite suite = new TestSuite(OpenHTMLEditorTest.class.getName());
		suite.addTest(new OpenHTMLEditorTest("testOpenHTMLEditor1"));
		suite.addTest(new OpenHTMLEditorTest("testOpenHTMLEditor2"));
		suite.addTest(new OpenHTMLEditorTest("testOpenLargeFileFoldingOnOutlineOn"));
		// suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOffOutlineOn"));
		suite.addTest(new OpenHTMLEditorTest("testOpenLargeFileFoldingOnOutlineOff"));
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

	public void testOpenHTMLEditor1() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, 0, getWarmUpRuns()), Performance
				.getDefault().getNullPerformanceMeter(), false);
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	public void testOpenHTMLEditor2() throws Exception
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
				// boolean wasAutobuilding= CoreUtility.setAutoBuilding(false);
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
			// Copy project contents from under "performance"
			IFile file = project.getFile("amazon.html");
			file.create(
					FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
							Path.fromPortableString("performance/amazon.html"), false), true, null);
			assertTrue(project.exists());
		}

		protected void tearDown() throws Exception
		{
			if (fTearDown)
				ResourceTestHelper.delete(PREFIX, FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
		}
	}
}
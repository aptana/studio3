/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests.performance;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.IOException;
import java.text.MessageFormat;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.eclipse.ui.PartInitException;

import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.epl.tests.OpenEditorTest;
import com.aptana.editor.epl.tests.ResourceTestHelper;

@RunWith(Suite.class)
@SuiteClasses({})
public class OpenHTMLEditorTest extends OpenEditorTest
{

	/**
	 * Performance input files
	 */
	private static final String BIG_HTML = "BigHTML";
	private static final String REDDIT_NO_CSS_NO_JS = "reddit-no-css-no-js";
	private static final String REDDIT = "reddit";
	private static final String AMAZON = "amazon";

	/**
	 * Common setup constants
	 */
	private static final String PROJECT = "performance_project";
	private static final int WARM_UP_RUNS = 5;
	private static final int MEASURED_RUNS = 20;
	private static final String FILE_SUFFIX = ".html";

	private static String getPrefix(String baseName)
	{
		return "/" + PROJECT + "/" + baseName;
	}

	private static IPath getFile(String baseName)
	{
		return Path.fromPortableString(getPrefix(baseName) + FILE_SUFFIX);
	}

	public OpenHTMLEditorTest(String name)
	{
		super(name);
	}

//	public static Test suite()
//	{
//		// ensure sequence
//		TestSuite suite = new TestSuite(OpenHTMLEditorTest.class.getName());
//		suite.addTest(new OpenHTMLEditorTest("testOpenHTMLEditor1"));
////		suite.addTest(new OpenHTMLEditorTest("testOpenReddit"));
////		suite.addTest(new OpenHTMLEditorTest("testOpenRedditNoCSSNoJS"));
//		// suite.addTest(new OpenHTMLEditorTest("testOpenBigHTML"));
//		suite.addTest(new OpenHTMLEditorTest("testOpenLargeFileFoldingOnOutlineOn"));
//		// suite.addTest(new OpenHTMLEditorTest("testOpenLargeFileFoldingOffOutlineOn"));
//		suite.addTest(new OpenHTMLEditorTest("testOpenLargeFileFoldingOnOutlineOff"));
//		// suite.addTest(new OpenHTMLEditorTest("testOpenLargeFileFoldingOffOutlineOff"));
//		return new Setup(suite);
//	}
//
//	/*
//	 * @see junit.framework.TestCase#setUp()
//	 */
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
		EditorTestHelper.closeAllEditors();
		super.tearDown();
	}

	public void testOpenHTMLEditor1() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(AMAZON), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(AMAZON), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	public void testOpenReddit() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(REDDIT), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(REDDIT), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	public void testOpenRedditNoCSSNoJS() throws Exception
	{
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(REDDIT_NO_CSS_NO_JS), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(REDDIT_NO_CSS_NO_JS), FILE_SUFFIX, getWarmUpRuns(),
				getMeasuredRuns()), createPerformanceMeter(), false);
	}

	public void testBigHTML() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(BIG_HTML), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(BIG_HTML), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	public void testOpenLargeFileFoldingOnOutlineOn() throws Exception
	{
		PerformanceMeter performanceMeter = createPerformanceMeter();
		measureOpenInEditor(getFile(AMAZON), true, true, performanceMeter);
	}

	// public void testOpenLargeFileFoldingOffOutlineOn() throws Exception
	// {
	// measureOpenInEditor(getFile("amazon"), false, true, createPerformanceMeter());
	// }

	public void testOpenLargeFileFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(getFile(AMAZON), true, false, createPerformanceMeter());
	}

	// public void testOpenLargeFileFoldingOffOutlineOff() throws Exception
	// {
	// measureOpenInEditor(getFile("amazon"), false, false, createPerformanceMeter());
	// }

	protected void measureOpenInEditor(IPath file, boolean enableFolding, boolean showOutline,
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
			{
				EditorTestHelper.showPerspective(EditorTestHelper.WEB_PERSPECTIVE_ID);
			}

			if (!ResourceTestHelper.projectExists(PROJECT))
			{
				// boolean wasAutobuilding= CoreUtility.setAutoBuilding(false);
				setUpProject();
				// ResourceTestHelper.fullBuild();
				// if (wasAutobuilding)
				// CoreUtility.setAutoBuilding(true);

				EditorTestHelper.joinBackgroundActivities();
			}

			replicate(AMAZON);
			replicate(REDDIT);
			replicate(REDDIT_NO_CSS_NO_JS);
			replicate(BIG_HTML);

			EditorTestHelper.joinBackgroundActivities();
		}

		private void replicate(String baseName) throws CoreException
		{
			ResourceTestHelper.replicate(getFile(baseName), getPrefix(baseName), FILE_SUFFIX, WARM_UP_RUNS
					+ MEASURED_RUNS, ResourceTestHelper.IfExists.SKIP);
		}

		private void setUpProject() throws Exception
		{
			IProject project = ResourceTestHelper.createExistingProject(PROJECT);
			assertTrue("Failed to create an open project", project.isAccessible());

			copyFile(project, AMAZON);
			copyFile(project, BIG_HTML);
			copyFile(project, REDDIT_NO_CSS_NO_JS);
			copyFile(project, REDDIT);
		}

		protected void copyFile(IProject project, String baseName) throws CoreException, IOException
		{
			String fileName = baseName + FILE_SUFFIX;
			// Copy project contents from under "performance"
			IFile file = project.getFile(fileName);
			file.create(
					FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
							Path.fromPortableString("performance/" + fileName), false), true, null);
			// verify we created the file.
			assertTrue(MessageFormat.format("Failed to copy performance file ({0}) into project", fileName),
					file.exists());
		}

		protected void tearDown() throws Exception
		{
			if (fTearDown)
			{
				ResourceTestHelper.delete(getPrefix(AMAZON), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(getPrefix(BIG_HTML), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(getPrefix(REDDIT_NO_CSS_NO_JS), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(getPrefix(REDDIT), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(ResourceTestHelper.getProject(PROJECT).getLocation());
			}
		}
	}
}

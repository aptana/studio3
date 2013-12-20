/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests.performance;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.epl.tests.OpenEditorTest;
import com.aptana.editor.epl.tests.ResourceTestHelper;

@RunWith(Suite.class)
@SuiteClasses({})
public class OpenCSSEditorTest extends OpenEditorTest
{

	/**
	 * Performance input files
	 */
	private static final String FROM_METADATA = "from-metadata";
	private static final String GITHUB_FORMATTED = "github-formatted";
	private static final String GITHUB_MINIMIZED = "github-minimized";
	private static final String WORDPRESS_ADMIN_MINIMIZED = "wp-admin";
	private static final String WORDPRESS_ADMIN = "wp-admin.dev";
	private static final String YUI = "yui";

	private static final String PROJECT = "css_perf";
	private static final int WARM_UP_RUNS = 2;
	private static final int MEASURED_RUNS = 5;
	private static final String FILE_SUFFIX = ".css";

	private static String getPrefix(String baseName)
	{
		return "/" + PROJECT + "/" + baseName;
	}

	private static IPath getFile(String baseName)
	{
		return Path.fromPortableString(getPrefix(baseName) + FILE_SUFFIX);
	}

	public OpenCSSEditorTest(String name)
	{
		super(name);
	}

//	public static Test suite()
//	{
//		// ensure sequence
//		TestSuite suite = new TestSuite(OpenCSSEditorTest.class.getName());
//		suite.addTest(new OpenCSSEditorTest("testOpenCSSEditor1")); // YUI
//		// suite.addTest(new OpenCSSEditorTest("testOpenFromMetadata"));
//		// suite.addTest(new OpenCSSEditorTest("testOpenGithubFormatted"));
//		// suite.addTest(new OpenCSSEditorTest("testOpenGithubMinimized"));
//		suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOnOutlineOn")); // WORDPRESS_MINIMIZED
//		// suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOffOutlineOn")); // WORDPRESS_MINIMIZED
//		suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOnOutlineOff")); // WORDPRESS_MINIMIZED
//		// suite.addTest(new OpenCSSEditorTest("testOpenLargeMinifiedFileFoldingOffOutlineOff")); // WORDPRESS_MINIMIZED
//		suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOnOutlineOn")); // WORDPRESS_ADMIN
//		// suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOffOutlineOn")); // WORDPRESS_ADMIN
//		suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOnOutlineOff")); // WORDPRESS_ADMIN
//		// suite.addTest(new OpenCSSEditorTest("testOpenLargeFileFoldingOffOutlineOff")); // WORDPRESS_ADMIN
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

	public void testOpenCSSEditor1() throws Exception
	{
		timeOpening(YUI, false);
	}

	public void testOpenFromMetadata() throws Exception
	{
		timeOpening(FROM_METADATA, true);
	}

	public void testOpenGithubFormatted() throws Exception
	{
		timeOpening(GITHUB_FORMATTED, true);
	}

	public void testOpenGithubMinimized() throws Exception
	{
		timeOpening(GITHUB_MINIMIZED, true);
	}

	public void testOpenLargeFileFoldingOnOutlineOn() throws Exception
	{
		measureOpenInEditor(getFile(WORDPRESS_ADMIN), true, true, createPerformanceMeter());
	}

	// public void testOpenLargeFileFoldingOffOutlineOn() throws Exception
	// {
	// measureOpenInEditor(getFile(WORDPRESS_ADMIN), false, true, createPerformanceMeter());
	// }

	public void testOpenLargeFileFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(getFile(WORDPRESS_ADMIN), true, false, createPerformanceMeter());
	}

	// public void testOpenLargeFileFoldingOffOutlineOff() throws Exception
	// {
	// measureOpenInEditor(getFile(WORDPRESS_ADMIN), false, false, createPerformanceMeter());
	// }

	public void testOpenLargeMinifiedFileFoldingOnOutlineOn() throws Exception
	{
		measureOpenInEditor(getFile(WORDPRESS_ADMIN_MINIMIZED), true, true, createPerformanceMeter());
	}

	// public void testOpenLargeMinifiedFileFoldingOffOutlineOn() throws Exception
	// {
	// measureOpenInEditor(getFile(WORDPRESS_ADMIN_MINIMIZED), false, true, createPerformanceMeter());
	// }

	public void testOpenLargeMinifiedFileFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(getFile(WORDPRESS_ADMIN_MINIMIZED), true, false, createPerformanceMeter());
	}

	// public void testOpenLargeMinifiedFileFoldingOffOutlineOff() throws Exception
	// {
	// measureOpenInEditor(getFile(WORDPRESS_ADMIN_MINIMIZED), false, false, createPerformanceMeter());
	// }

	protected void timeOpening(String baseFileName, boolean closeEach) throws PartInitException
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(baseFileName), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), closeEach);
		if (!closeEach)
		{
			EditorTestHelper.closeAllEditors();
		}
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(baseFileName), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), closeEach);
	}

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
				// boolean wasAutobuilding = CoreUtility.setAutoBuilding(false);
				setUpProject();
				// ResourceTestHelper.fullBuild();
				// if (wasAutobuilding)
				// CoreUtility.setAutoBuilding(true);

				EditorTestHelper.joinBackgroundActivities();
			}

			replicate(FROM_METADATA);
			replicate(GITHUB_FORMATTED);
			replicate(GITHUB_MINIMIZED);
			replicate(YUI);
			// Wordpress files don't need to be replicated
		}

		private void replicate(String baseFileName) throws CoreException
		{
			ResourceTestHelper.replicate(getFile(baseFileName), getPrefix(baseFileName), FILE_SUFFIX, WARM_UP_RUNS
					+ MEASURED_RUNS, ResourceTestHelper.IfExists.SKIP);
		}

		private void setUpProject() throws Exception
		{
			IProject project = ResourceTestHelper.createExistingProject(PROJECT);
			assertTrue("Failed to create an open project", project.isAccessible());

			// Copy all project contents from under "performance"
			Bundle bundle = Platform.getBundle("com.aptana.editor.css.tests");
			Enumeration<URL> urls = bundle.findEntries("performance", "*.css", true);
			assertTrue("Got no performance files to copy", urls.hasMoreElements());
			while (urls.hasMoreElements())
			{
				// Extract performance file to filesystem
				File file = ResourceUtil.resourcePathToFile(urls.nextElement());
				// create a file in the new project with the extracted contents.
				IFile iFile = project.getFile(file.getName());
				InputStream stream = new FileInputStream(file);
				iFile.create(stream, true, null);
				stream.close();
				// verify we created the file.
				assertTrue("Failed to copy performance file into project", iFile.exists());
			}
		}

		protected void tearDown() throws Exception
		{
			if (fTearDown)
			{
				ResourceTestHelper.delete(getPrefix(FROM_METADATA), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(getPrefix(GITHUB_FORMATTED), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(getPrefix(GITHUB_MINIMIZED), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
				ResourceTestHelper.delete(getPrefix(YUI), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
			}
		}
	}
}

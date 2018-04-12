/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests.performance;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.MessageFormat;

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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.epl.tests.OpenEditorTest;
import com.aptana.editor.epl.tests.ResourceTestHelper;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
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

	@BeforeClass
	public void setUpSuite() throws Exception
	{
		EditorTestHelper.showView(EditorTestHelper.INTRO_VIEW_ID, false);

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

		replicate(AMAZON);
		replicate(REDDIT);
		replicate(REDDIT_NO_CSS_NO_JS);
		replicate(BIG_HTML);

		EditorTestHelper.joinBackgroundActivities();
	}

	@AfterClass
	public void tearDownSuite() throws Exception
	{
		ResourceTestHelper.delete(getPrefix(AMAZON), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
		ResourceTestHelper.delete(getPrefix(BIG_HTML), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
		ResourceTestHelper.delete(getPrefix(REDDIT_NO_CSS_NO_JS), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
		ResourceTestHelper.delete(getPrefix(REDDIT), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
		ResourceTestHelper.delete(ResourceTestHelper.getProject(PROJECT).getLocation());
	}

	@Before
	public void setUp() throws Exception
	{
		EditorTestHelper.runEventQueue();
		setWarmUpRuns(WARM_UP_RUNS);
		setMeasuredRuns(MEASURED_RUNS);
	}

	@After
	public void tearDown() throws Exception
	{
		EditorTestHelper.closeAllEditors();
	}

	@Test
	public void testOpenHTMLEditor1() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(AMAZON), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(AMAZON), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	@Test
	public void testOpenReddit() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(REDDIT), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(REDDIT), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	@Test
	public void testOpenRedditNoCSSNoJS() throws Exception
	{
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(REDDIT_NO_CSS_NO_JS), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(REDDIT_NO_CSS_NO_JS), FILE_SUFFIX, getWarmUpRuns(),
				getMeasuredRuns()), createPerformanceMeter(), false);
	}

	@Test
	public void testBigHTML() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(getPrefix(BIG_HTML), FILE_SUFFIX, 0, getWarmUpRuns()),
				Performance.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(
				ResourceTestHelper.findFiles(getPrefix(BIG_HTML), FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	@Test
	public void testOpenLargeFileFoldingOnOutlineOn() throws Exception
	{
		PerformanceMeter performanceMeter = createPerformanceMeter();
		measureOpenInEditor(getFile(AMAZON), true, true, performanceMeter);
	}

	// @Test
	// public void testOpenLargeFileFoldingOffOutlineOn() throws Exception
	// {
	// measureOpenInEditor(getFile("amazon"), false, true, createPerformanceMeter());
	// }

	@Test
	public void testOpenLargeFileFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(getFile(AMAZON), true, false, createPerformanceMeter());
	}

	// @Test
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

	private void replicate(String baseName) throws CoreException
	{
		ResourceTestHelper.replicate(getFile(baseName), getPrefix(baseName), FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS,
				ResourceTestHelper.IfExists.SKIP);
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
		file.create(FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromPortableString("performance/" + fileName), false), true, null);
		// verify we created the file.
		assertTrue(MessageFormat.format("Failed to copy performance file ({0}) into project", fileName), file.exists());
	}

	private static String getPrefix(String baseName)
	{
		return "/" + PROJECT + "/" + baseName;
	}

	private static IPath getFile(String baseName)
	{
		return Path.fromPortableString(getPrefix(baseName) + FILE_SUFFIX);
	}
}
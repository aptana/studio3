package com.aptana.editor.js.tests.performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class OpenJSEditorTest extends OpenEditorTest
{

	private static final String PROJECT = "js_perf";
	private static final int WARM_UP_RUNS = 10;
	private static final int MEASURED_RUNS = 50;
	private static final String PREFIX = "/" + PROJECT + "/timobile";
	private static final String FILE_SUFFIX = ".js";
	private static final IPath LARGE_MINIFIED_FILE = Path.fromPortableString("/" + PROJECT + "/ext-all.js");
	private static final IPath LARGE_FILE = Path.fromPortableString("/" + PROJECT + "/ext-all-dev.js");
	private static final IPath SRC_FILE = Path.fromPortableString(PREFIX + FILE_SUFFIX);

	public OpenJSEditorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		// ensure sequence
		TestSuite suite = new TestSuite(OpenJSEditorTest.class.getName());
		suite.addTest(new OpenJSEditorTest("testOpenJSEditor1"));
		suite.addTest(new OpenJSEditorTest("testOpenLargeMinifiedFileFoldingOnOutlineOn"));
		// suite.addTest(new OpenJSEditorTest("testOpenLargeMinifiedFileFoldingOffOutlineOn"));
		suite.addTest(new OpenJSEditorTest("testOpenLargeMinifiedFileFoldingOnOutlineOff"));
		// suite.addTest(new OpenJSEditorTest("testOpenLargeMinifiedFileFoldingOffOutlineOff"));
		suite.addTest(new OpenJSEditorTest("testOpenLargeFileFoldingOnOutlineOn"));
		// suite.addTest(new OpenJSEditorTest("testOpenLargeFileFoldingOffOutlineOn"));
		suite.addTest(new OpenJSEditorTest("testOpenLargeFileFoldingOnOutlineOff"));
		// suite.addTest(new OpenJSEditorTest("testOpenLargeFileFoldingOffOutlineOff"));
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
		EditorTestHelper.closeAllEditors();
		super.tearDown();
	}

	public void testOpenJSEditor1() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, 0, getWarmUpRuns()), Performance
				.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
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
			ResourceTestHelper.replicate(SRC_FILE, PREFIX, FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS,
					ResourceTestHelper.IfExists.SKIP);
		}

		private void setUpProject() throws Exception
		{
			IProject project = ResourceTestHelper.createExistingProject(PROJECT);
			assertTrue("Failed to create an open project", project.isAccessible());

			// Copy all project contents from under "performance"
			Bundle bundle = Platform.getBundle("com.aptana.editor.js.tests");
			Enumeration<URL> urls = bundle.findEntries("performance", "*.js", true);
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
				ResourceTestHelper.delete(PREFIX, FILE_SUFFIX, WARM_UP_RUNS + MEASURED_RUNS);
			}
		}
	}
}
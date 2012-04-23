package com.aptana.editor.js.tests.performance;

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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.epl.tests.OpenEditorTest;
import com.aptana.editor.epl.tests.ResourceTestHelper;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.validator.JSLintValidator;

public class OpenJSEditorTest extends OpenEditorTest
{

	private static final String PROJECT = "js_perf";
	private static final int WARM_UP_RUNS = 2;
	private static final int MEASURED_RUNS = 5;
	private static final String PREFIX = "/" + PROJECT + "/timobile";
	private static final String FILE_SUFFIX = ".js";
	private static final IPath EXT_MINIFIED = Path.fromPortableString("/" + PROJECT + "/ext.js");
	private static final IPath EXT_DEV = Path.fromPortableString("/" + PROJECT + "/ext-dev.js");
	private static final IPath SRC_FILE = Path.fromPortableString(PREFIX + FILE_SUFFIX);

	public OpenJSEditorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		// ensure sequence
		TestSuite suite = new TestSuite(OpenJSEditorTest.class.getName());
		suite.addTest(new OpenJSEditorTest("testOpenTiMobile"));
		// suite.addTest(new OpenJSEditorTest("testOpenExtMinifiedFoldingOnOutlineOn"));
		// suite.addTest(new OpenJSEditorTest("testOpenExtMinifiedFoldingOnOutlineOff"));
		suite.addTest(new OpenJSEditorTest("testOpenExtDevFoldingOnOutlineOn"));
		suite.addTest(new OpenJSEditorTest("testOpenExtDevFoldingOnOutlineOff"));
		return new Setup(suite);
	}

	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		IEclipsePreferences prefs = (EclipseUtil.instanceScope()).getNode(JSPlugin.PLUGIN_ID);

		// Turn off all JS validators!!!!
		JSLintValidator jsLintValidator = new JSLintValidator()
		{
			@Override
			public String getId()
			{
				return ID;
			}
		};
		prefs.putBoolean(jsLintValidator.getEnablementPreferenceKey(BuildType.BUILD), false);
		prefs.putBoolean(jsLintValidator.getEnablementPreferenceKey(BuildType.RECONCILE), false);

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

	public void testOpenTiMobile() throws Exception
	{
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, 0, getWarmUpRuns()), Performance
				.getDefault().getNullPerformanceMeter(), false);
		EditorTestHelper.closeAllEditors();
		measureOpenInEditor(ResourceTestHelper.findFiles(PREFIX, FILE_SUFFIX, getWarmUpRuns(), getMeasuredRuns()),
				createPerformanceMeter(), false);
	}

	public void testOpenExtDevFoldingOnOutlineOn() throws Exception
	{
		PerformanceMeter performanceMeter = createPerformanceMeter();
		measureOpenInEditor(EXT_DEV, true, true, performanceMeter);
	}

	public void testOpenExtDevFoldingOnOutlineOff() throws Exception
	{
		measureOpenInEditor(EXT_DEV, true, false, createPerformanceMeter());
	}

	// public void testOpenExtMinifiedFoldingOnOutlineOn() throws Exception
	// {
	// PerformanceMeter performanceMeter = createPerformanceMeter();
	// measureOpenInEditor(EXT_MINIFIED, true, true, performanceMeter);
	// }

	// public void testOpenExtMinifiedFoldingOnOutlineOff() throws Exception
	// {
	// measureOpenInEditor(EXT_MINIFIED, true, false, createPerformanceMeter());
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
/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests.performance;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.GlobalTimePerformanceTestCase;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.tests.TestProject;
import com.aptana.core.util.IOUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.index.core.RebuildIndexJob;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.internal.core.build.JSParserValidator;

public class JSParserValidatorPerformanceTest extends GlobalTimePerformanceTestCase
{
	private AbstractBuildParticipant validator;
	private TestProject project;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		project = new TestProject("jsparservalidator", new String[] { "com.aptana.projects.webnature" });
		validator = createValidator();
	}

	@Override
	protected void tearDown() throws Exception
	{
		if (project != null)
		{
			project.delete();
			project = null;
		}
		validator = null;
		super.tearDown();
	}

	protected AbstractBuildParticipant createValidator()
	{
		return new JSParserValidator()
		{

			@Override
			protected String getPreferenceNode()
			{
				return JSCorePlugin.PLUGIN_ID;
			}

			@Override
			public String getId()
			{
				return ID;
			}
		};
	}

	protected void perfValidate(String filename, int iterations) throws Exception
	{
		// read in the file
		InputStream in = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID),
				Path.fromPortableString("performance/" + filename), false);
		IFile file = project.createFile(filename, IOUtil.read(in));
		RebuildIndexJob job = new RebuildIndexJob(project.getURI());
		job.run(null);

		// Ok now actually validate the thing, the real work
		for (int i = 0; i < iterations; i++)
		{
			EditorTestHelper.joinBackgroundActivities();

			BuildContext context = new BuildContext(file);
			// Don't measure reading in string...
			context.getContents();

			startMeasuring();
			validator.buildFile(context, null);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	protected boolean reparseEveryTime()
	{
		return false;
	}

	public void testThreeMinJS() throws Exception
	{
		perfValidate("three.min.js", 1);
	}
}

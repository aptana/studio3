/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.build.UnifiedBuilder;
import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.ResourceUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.testing.categories.PerformanceTests;
import com.aptana.testing.utils.TestProject;

@Category({ PerformanceTests.class })
public class JSBuildPerformanceTest extends GlobalTimePerformanceTestCase
{

	private TestProject project;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		project = new TestProject("perf_jaxer_build_test", new String[] { "com.aptana.projects.webnature" },
				new String[] { UnifiedBuilder.ID });

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription description = workspace.getDescription();
		boolean autoBuilding = description.isAutoBuilding();
		if (autoBuilding)
		{
			description.setAutoBuilding(false);
			workspace.setDescription(description);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		project.delete();
		project = null;

		super.tearDown();
	}

	public void testBuildJaxer() throws Exception
	{
		URL url = FileLocator.find(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path("performance/jaxer"), null);
		IFileStore fileStore = EFS.getStore(ResourceUtil.resourcePathToURI(url));
		IFileStore destination = EFS.getStore(project.getURI());
		fileStore.copy(destination, EFS.NONE, new NullProgressMonitor());

		// TODO turn off autobuild?

		project.getInnerProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		final int numRuns = 2;
		for (int i = 0; i < numRuns; i++)
		{
			System.out.println(MessageFormat.format("Starting run {0} of jaxer build", i + 1));
			startMeasuring();
			project.getInnerProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

}
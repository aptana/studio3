/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.io.File;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.build.BuildContext;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class HTMLTidyValidatorPerformanceTest extends GlobalTimePerformanceTestCase
{

	private HTMLTidyValidator validator;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		validator = new HTMLTidyValidator()
		{

			@Override
			protected String getPreferenceNode()
			{
				return HTMLPlugin.PLUGIN_ID;
			}

			@Override
			public String getId()
			{
				return ID;
			}
		};
	}

	@Override
	public void tearDown() throws Exception
	{
		validator = null;
		super.tearDown();
	}

	@Test
	public void testValidate() throws Exception
	{
		// read in the file
		URL url = FileLocator.find(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromPortableString("performance/amazon.html"), null);
		File file = ResourceUtil.resourcePathToFile(url);
		IFileStore fileStore = EFS.getStore(file.toURI());

		EditorTestHelper.joinBackgroundActivities();

		// Ok now actually validate the thing, the real work
		for (int i = 0; i < 700; i++)
		{
			// Don't measure reading in string...
			BuildContext context = new FileStoreBuildContext(fileStore);
			context.getContents();

			startMeasuring();
			validator.buildFile(context, null);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}

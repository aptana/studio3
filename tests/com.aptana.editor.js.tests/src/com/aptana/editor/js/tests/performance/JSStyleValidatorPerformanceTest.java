/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests.performance;

import java.io.File;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.experimental.categories.Category;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.JSParser;
import com.aptana.js.internal.core.build.JSStyleValidator;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class JSStyleValidatorPerformanceTest extends GlobalTimePerformanceTestCase
{
	
	private AbstractBuildParticipant validator;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		validator = createValidator();
	}

	@Override
	protected void tearDown() throws Exception
	{
		validator = null;
		super.tearDown();
	}

	protected AbstractBuildParticipant createValidator()
	{
		return new JSStyleValidator()
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
		URL url = FileLocator.find(Platform.getBundle(JSCorePlugin.PLUGIN_ID),
				Path.fromPortableString("performance/" + filename), null);
		File file = ResourceUtil.resourcePathToFile(url);
		IFileStore fileStore = EFS.getStore(file.toURI());

		// Ok now actually validate the thing, the real work
		for (int i = 0; i < iterations; i++)
		{
			EditorTestHelper.joinBackgroundActivities();

			// Force a re-parse every time so we're comparing apples to apples for JSLint
			BuildContext context = new FileStoreBuildContext(fileStore)
			{
				@Override
				protected ParseResult parse(String contentType, IParseState parseState, WorkingParseResult working)
						throws Exception
				{
					if (reparseEveryTime())
					{
						return new JSParser().parse(parseState);
					}
					return super.parse(contentType, parseState, working);
				}
			};
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

	public void testValidateUncompressedDojo() throws Exception
	{
		perfValidate("dojo.js.uncompressed.js", 10);
	}

	public void testValidateMinifiedDojo() throws Exception
	{
		perfValidate("dojo.js.minified.js", 10);
	}

	// public void testValidateExtAllDev() throws Exception
	// {
	// perfValidate("ext/ext-all-dev.js", 10);
	// }

	public void testValidateTiMobile() throws Exception
	{
		perfValidate("timobile.js", 10);
	}

	public void testValidateTinyMCE() throws Exception
	{
		perfValidate("tiny_mce.js", 10);
	}
}
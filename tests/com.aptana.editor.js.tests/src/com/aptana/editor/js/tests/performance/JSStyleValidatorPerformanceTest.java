/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests.performance;

import org.junit.experimental.categories.Category;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.internal.core.build.JSStyleValidator;
import com.aptana.testing.categories.PerformanceTests;

@Category({ PerformanceTests.class })
public class JSStyleValidatorPerformanceTest extends JSLintValidatorPerformanceTest
{

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
}
/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.js.core.build.CoreBuildTests;
import com.aptana.js.core.index.JSIndexQueryHelperTest;
import com.aptana.js.core.inferencing.CoreInferencingTests;
import com.aptana.js.core.model.CoreModelTests;
import com.aptana.js.core.parsing.CoreParsingTests;
import com.aptana.js.internal.core.build.InternalCoreBuildTests;
import com.aptana.js.internal.core.index.InternalCoreIndexTests;
import com.aptana.js.internal.core.inferencing.InternalCoreInferencingTests;
import com.aptana.js.internal.core.node.InternalCoreNodeTests;
import com.aptana.js.internal.core.parsing.InternalCoreParsingTests;
import com.aptana.js.internal.core.parsing.sdoc.InternalCoreParsingSDocTests;

@RunWith(Suite.class)
@SuiteClasses({ CoreBuildTests.class, JSIndexQueryHelperTest.class, CoreInferencingTests.class, CoreParsingTests.class,
		InternalCoreBuildTests.class, InternalCoreIndexTests.class, InternalCoreInferencingTests.class,
		InternalCoreParsingTests.class, InternalCoreParsingSDocTests.class, CoreModelTests.class,
		InternalCoreNodeTests.class })
public class AllJSCoreTests
{
}

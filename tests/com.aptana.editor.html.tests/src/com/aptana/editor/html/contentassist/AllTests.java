/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({
	ContentAssistFineLocationTests.class,
	ContentAssistCoarseLocationTests.class,
	HTMLContentAssistProcessorTest.class,
	MetadataTests.class,
	HTMLNestedLanguageContentAssistTests.class,
})
// @formatter:on
public class AllTests
{
}

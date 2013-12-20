/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.html.contentassist.index.HTMLFileIndexingParticipantTest;
import com.aptana.editor.html.internal.build.HTMLTaskDetectorTest;
import com.aptana.editor.html.validator.ValidatorTests;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	com.aptana.editor.html.HTMLEditorTests.class,
	com.aptana.editor.html.parsing.HTMLParsingTests.class,
	com.aptana.editor.html.outline.AllTests.class,
	com.aptana.editor.html.contentassist.AllTests.class,
	ValidatorTests.class,
	com.aptana.editor.html.text.AllTests.class,
	HTMLFileIndexingParticipantTest.class,
	HTMLTaskDetectorTest.class
})
// @formatter:on
public class AllTests
{
}

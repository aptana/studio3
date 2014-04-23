/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.js.contentassist.JSContentAssistProposalTest;
import com.aptana.editor.js.contentassist.JSContextInfoTest;
import com.aptana.editor.js.contentassist.JSUserAgentFilteringTest;
import com.aptana.editor.js.contentassist.RangeTest;
import com.aptana.editor.js.hyperlink.JSHyperlinkDetectorTests;
import com.aptana.editor.js.sdoc.parsing.SDocAutoCompletionTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	JSContentAssistProposalTest.class,
	JSContextInfoTest.class,
	JSHyperlinkDetectorTests.class,
	JSUserAgentFilteringTest.class,
	RangeTest.class,
	SDocAutoCompletionTest.class
})
//@formatter:on
public class IntegrationTests
{
}

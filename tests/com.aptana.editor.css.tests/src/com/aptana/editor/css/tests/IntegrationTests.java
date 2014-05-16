/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.css.contentassist.CSSContentAssistProposalTests;
import com.aptana.editor.css.contentassist.CSSTextHoverTests;
import com.aptana.editor.css.contentassist.RangeTests;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	CSSContentAssistProposalTests.class,
	CSSTextHoverTests.class,
	RangeTests.class
})
//@formatter:on
public class IntegrationTests
{

}

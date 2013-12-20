/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.git.ui.dialogs.CreateBranchDialogTest;
import com.aptana.git.ui.hyperlink.HyperlinkDetectorTest;
import com.aptana.git.ui.internal.DiffFormatterTest;
import com.aptana.git.ui.internal.GitLightweightDecoratorTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	CreateBranchDialogTest.class,
	DiffFormatterTest.class,
	GitLightweightDecoratorTest.class,
	HyperlinkDetectorTest.class
})
//@formatter:on
public class AllTests
{
}

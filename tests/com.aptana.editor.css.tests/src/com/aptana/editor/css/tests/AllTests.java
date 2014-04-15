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

import com.aptana.editor.css.CSSCodeScannerFlexTest;
import com.aptana.editor.css.CSSCodeScannerTest;
import com.aptana.editor.css.CSSEditorTest;
import com.aptana.editor.css.CSSSourcePartitionScannerFlexTest;
import com.aptana.editor.css.CSSSourcePartitionScannerTest;
import com.aptana.editor.css.internal.text.CSSFoldingComputerTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	CSSCodeScannerTest.class,
	CSSCodeScannerFlexTest.class,
	CSSEditorTest.class,
	CSSFoldingComputerTest.class,
	CSSSourcePartitionScannerTest.class,
	CSSSourcePartitionScannerFlexTest.class,
	com.aptana.editor.css.outline.AllTests.class,
	com.aptana.editor.css.contentassist.AllTests.class
})
//@formatter:on
public class AllTests
{

}

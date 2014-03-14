/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.xml.OpenTagCloserTest;
import com.aptana.editor.xml.TagUtilTest;
import com.aptana.editor.xml.XMLEditorTest;
import com.aptana.editor.xml.XMLPartitionScannerTest;
import com.aptana.editor.xml.XMLScannerTest;
import com.aptana.editor.xml.XMLTagScannerTest;
import com.aptana.editor.xml.contentassist.XMLContentAssistProcessorTest;
import com.aptana.editor.xml.contentassist.QuickFixProcessorsRegistryTest;
import com.aptana.editor.xml.internal.text.XMLFoldingComputerTest;
import com.aptana.editor.xml.outline.XMLOutlineTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	TagUtilTest.class,
	XMLContentAssistProcessorTest.class,
	QuickFixProcessorsRegistryTest.class,
	XMLPartitionScannerTest.class,
	XMLScannerTest.class,
	XMLFoldingComputerTest.class,
	XMLTagScannerTest.class,
	XMLEditorTest.class,
	XMLOutlineTest.class,
	OpenTagCloserTest.class
})
//@formatter:on
public class AllTests
{
}

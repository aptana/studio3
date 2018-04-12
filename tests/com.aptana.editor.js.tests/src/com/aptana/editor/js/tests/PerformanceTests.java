/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.js.contentassist.JSBuildPerformanceTest;
import com.aptana.editor.js.contentassist.JSContentAssistProcessorPerformanceTest;
import com.aptana.editor.js.contentassist.JSIndexingPerformanceTest;
import com.aptana.editor.js.tests.performance.JSLintValidatorPerformanceTest;
import com.aptana.editor.js.tests.performance.JSParserValidatorPerformanceTest;
import com.aptana.editor.js.tests.performance.JSStyleValidatorPerformanceTest;
import com.aptana.editor.js.tests.performance.OpenJSEditorTest;
import com.aptana.editor.js.text.JSCodeScannerPerformanceTest;
import com.aptana.editor.js.text.JSSourcePartitionScannerPerformanceTest;

@RunWith(Suite.class)
@SuiteClasses({ JSBuildPerformanceTest.class, JSContentAssistProcessorPerformanceTest.class,
		JSIndexingPerformanceTest.class, JSCodeScannerPerformanceTest.class,
		JSSourcePartitionScannerPerformanceTest.class, JSLintValidatorPerformanceTest.class,
		JSParserValidatorPerformanceTest.class, JSStyleValidatorPerformanceTest.class, OpenJSEditorTest.class, })
public class PerformanceTests
{
}

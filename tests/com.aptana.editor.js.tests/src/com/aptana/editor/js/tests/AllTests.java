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

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	com.aptana.editor.js.EditorJSTests.class,
	com.aptana.editor.js.contentassist.ContentAssistTests.class,
	com.aptana.editor.js.folding.FoldingTests.class,
	com.aptana.editor.js.hyperlink.HyperlinkTests.class,
	com.aptana.editor.js.index.IndexTests.class,
	com.aptana.editor.js.inferencing.InferencingTests.class,
	com.aptana.editor.js.internal.text.InternalTextTests.class,
	com.aptana.editor.js.outline.OutlineTests.class,
	com.aptana.editor.js.sdoc.parsing.SDocParsingTests.class,
	com.aptana.editor.js.text.TextTests.class,
	com.aptana.editor.js.navigate.selection.JSSubWordSelectTest.class
})
//@formatter:on
public class AllTests
{
}

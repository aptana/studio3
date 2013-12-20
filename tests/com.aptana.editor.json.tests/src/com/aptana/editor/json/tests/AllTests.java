/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.json.outline.JSONOutlineProviderTest;
@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	com.aptana.editor.json.AllTests.class,
	JSONOutlineProviderTest.class,
	com.aptana.editor.json.internal.text.AllTests.class
})
//@formatter:on
public class AllTests
{

}

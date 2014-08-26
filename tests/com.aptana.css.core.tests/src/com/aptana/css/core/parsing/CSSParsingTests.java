/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CSSIdentifierTest.class, CSSKeywordTest.class, CSSLiteralTest.class, CSSNotTest.class,
		CSSParserTest.class, CSSPunctuatorTest.class, CSSSpecialTokenHandlingTest.class, })
public class CSSParsingTests
{
}

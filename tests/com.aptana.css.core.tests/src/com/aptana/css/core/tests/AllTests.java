/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.css.core.index.CSSIndexTests;
import com.aptana.css.core.internal.build.CSSBuildParticipantsTests;
import com.aptana.css.core.parsing.CSSParsingTests;

@RunWith(Suite.class)
@SuiteClasses({ CSSParsingTests.class, CSSIndexTests.class, CSSBuildParticipantsTests.class, })
public class AllTests
{
}

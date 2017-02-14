/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.core.epl.downloader.FileReaderTest;
import com.aptana.core.epl.downloader.ProgressStatisticsTest;
import com.aptana.core.epl.util.LRUCacheTest;
import com.aptana.core.epl.util.LRUCacheWithSoftPrunedValuesTest;
import com.aptana.core.epl.util.SoftHashMapTest;

@RunWith(Suite.class)
@SuiteClasses({ LRUCacheTest.class, LRUCacheWithSoftPrunedValuesTest.class, SoftHashMapTest.class, FileReaderTest.class, ProgressStatisticsTest.class })
public class AllTests
{

}

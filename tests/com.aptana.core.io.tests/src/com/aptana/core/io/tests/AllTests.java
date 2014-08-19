/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.ide.core.io.downloader.CoreIODownloaderTests;
import com.aptana.ide.core.io.internal.auth.InternalAuthTests;
import com.aptana.ide.core.io.preferences.CloakingUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EFSUtilsTest.class, WorkspaceFileSystemTest.class, WorkspaceConnectionPointTest.class,
		ConnectionPointManagerTest.class, CloakingUtilsTest.class, CoreIODownloaderTests.class, InternalAuthTests.class })
public class AllTests
{
}

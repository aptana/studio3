/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.secureftp.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.filesystem.secureftp.FTPSConnectionPointTest;
import com.aptana.filesystem.secureftp.SFTPConnectionPointTest;

@RunWith(Suite.class)
@SuiteClasses({ SFTPConnectionPointTest.class, SFTPConnectionTest.class, FTPSConnectionPointTest.class,
		FTPSConnectionTest.class, FTPSConnectionWithBasePathTest.class, ImplicitFTPSConnectionTest.class, })
public class AllTests
{

}

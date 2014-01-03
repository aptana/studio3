/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public class UserBundleMonitorTests extends BundleMonitorTests
{
	private static final int WAIT_TIME = 5000;

	/**
	 * UserBundleMonitorTests
	 */
	public UserBundleMonitorTests()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.BundleMonitorTests#createFileSystem()
	 */
	protected IBundleFileSystem createFileSystem()
	{
		return new LocalFileSystem();
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.scripting.model.BundleMonitorTests#waitForEvent(com.aptana.scripting.model.BundleMonitorTests.
	 * FileSystemAction)
	 */
	protected void waitForAction(FileSystemAction action) throws Exception
	{
		// Thread.sleep(WAIT_TIME);
		synchronized (_monitor)
		{
			action.performAction();

			_monitor.wait(WAIT_TIME);
		}
	}
}

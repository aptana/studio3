/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import org.junit.Ignore;

@Ignore("We're still getting intermittent failures due to timing issues")
public class ProjectBundleMonitorTests extends BundleMonitorTests
{
	/**
	 * ProjectBundleMonitorTests
	 */
	public ProjectBundleMonitorTests()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.BundleMonitorTests#createFileSystem()
	 */
	protected IBundleFileSystem createFileSystem()
	{
		return new ProjectFileSystem();
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.scripting.model.BundleMonitorTests#waitForEvent(com.aptana.scripting.model.BundleMonitorTests.
	 * FileSystemAction)
	 */
	protected void waitForAction(FileSystemAction action) throws Exception
	{
		action.performAction();

		// TODO: Should be relying on events or monitors here
		Thread.sleep(750);
	}
}

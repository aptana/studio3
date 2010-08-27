package com.aptana.scripting.model;

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

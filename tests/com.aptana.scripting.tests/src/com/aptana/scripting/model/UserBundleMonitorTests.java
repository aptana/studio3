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
		synchronized (BundleMonitor.getInstance())
		{
			action.performAction();

			BundleMonitor.getInstance().wait(WAIT_TIME);
		}
	}
}

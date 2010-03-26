package com.aptana.scripting.model;

public class UserBundleMonitorTests extends BundleMonitorTests
{
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
}

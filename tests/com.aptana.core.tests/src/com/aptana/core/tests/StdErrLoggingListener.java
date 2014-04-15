package com.aptana.core.tests;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

public class StdErrLoggingListener extends RunListener
{
	@Override
	public void testStarted(Description description) throws Exception
	{
		System.err.println("Running test: " + description.getDisplayName());
		super.testStarted(description);
	}
}
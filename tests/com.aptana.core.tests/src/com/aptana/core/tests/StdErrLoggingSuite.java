package com.aptana.core.tests;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class StdErrLoggingSuite extends Suite
{
	public StdErrLoggingSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError
	{
		super(klass, builder);
	}

	@Override
	public void run(RunNotifier notifier)
	{
		notifier.addListener(new StdErrLoggingListener());
		super.run(notifier);
	}
}
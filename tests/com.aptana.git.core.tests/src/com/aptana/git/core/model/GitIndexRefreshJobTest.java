package com.aptana.git.core.model;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GitIndexRefreshJobTest
{

	private GitIndexRefreshJob job;
	private Mockery context;
	private GitIndex index;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		index = context.mock(GitIndex.class);
		job = new GitIndexRefreshJob(index);
	}

	@After
	public void tearDown() throws Exception
	{
		context = null;
		index = null;
		job.cancel();
		job = null;

	}

	@Test
	public void testRefreshAll() throws Exception
	{
		// TODO Test that when we call refreshAll we get scheduled, and when run, we call index.refresh(true, null,
		// whatever)
		// And that we wipe the queue of any specific requests received to that point.
		context.checking(new Expectations()
		{
			{
				oneOf(index).refresh(with(any(IProgressMonitor.class)));
				will(returnValue(Status.OK_STATUS));
			}
		});
		// set up a request for a path first...
		Collection<IPath> paths = new ArrayList<IPath>(1);
		paths.add(Path.ROOT);
		job.refresh(paths);
		// Then ask to refresh everything
		job.refreshAll();
		// wait for job to fire and finish
		job.join();
		context.assertIsSatisfied();
	}

	// TODO Uncomment when CGILIB doesn't f--k up here. It actually calls refresh on the real GitIndex class, which
	// doesn't work, since we didn't actually set it up
	// @Test
	// public void testRefresh() throws Exception
	// {
	// final Collection<IPath> paths = new ArrayList<IPath>(1);
	// paths.add(Path.ROOT);
	// context.checking(new Expectations()
	// {
	// {
	// oneOf(index).refresh(with(true), with(paths), with(any(IProgressMonitor.class)));
	// will(returnValue(Status.OK_STATUS));
	// }
	// });
	//
	// job.refresh(paths);
	// job.join();
	// context.assertIsSatisfied();
	// }
}

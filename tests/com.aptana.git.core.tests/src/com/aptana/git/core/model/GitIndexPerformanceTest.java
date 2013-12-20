package com.aptana.git.core.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.core.runtime.IPath;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class GitIndexPerformanceTest extends GitTestCase
{

	@Rule
	public TestName name = new TestName();
	private PerformanceMeter fPerformanceMeter;

	@Before
	public void setUp() throws Exception
	{
		Performance performance = Performance.getDefault();
		fPerformanceMeter = performance
				.createPerformanceMeter(getClass().getName() + '#' + name.getMethodName() + "()"); //$NON-NLS-1$
	}

	@Override
	public void tearDown() throws Exception
	{
		fPerformanceMeter.dispose();
		super.tearDown();
	}

	@Test
	public void testRefresh() throws Exception
	{
		GitRepository repo = getRepo();
		// Write 1000 small files to the repo
		writeFiles(repo.workingDirectory(), 1000);

		GitIndex index = repo.index();
		for (int i = 0; i < 1200; i++)
		{
			startMeasuring();
			index.refresh(null);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	private void writeFiles(IPath workingDirectory, int numFiles)
	{
		for (int i = 0; i < numFiles; i++)
		{
			FileWriter writer = null;
			try
			{
				writer = new FileWriter(workingDirectory.append("fake_file" + i + ".txt").toOSString());
				writer.write(UUID.randomUUID().toString());
			}
			catch (IOException e)
			{
				// ignore
			}
			finally
			{
				try
				{
					if (writer != null)
					{
						writer.close();
					}
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
	}

	@Test
	public void testRefreshWithListeners() throws Exception
	{
		GitRepository repo = getRepo();
		repo.addListener(new IGitRepositoryListener()
		{

			public void pushed(PushEvent e)
			{
			}

			public void pulled(PullEvent e)
			{
			}

			public void indexChanged(IndexChangedEvent e)
			{
			}

			public void branchRemoved(BranchRemovedEvent e)
			{
			}

			public void branchChanged(BranchChangedEvent e)
			{
			}

			public void branchAdded(BranchAddedEvent e)
			{
			}
		});
		// Write 1000 small files to the repo
		writeFiles(repo.workingDirectory(), 1000);

		GitIndex index = repo.index();
		for (int i = 0; i < 120; i++)
		{
			startMeasuring();
			index.refresh(null);
			stopMeasuring();
			writeFiles(repo.workingDirectory(), 1000);
		}
		commitMeasurements();
		assertPerformance();
	}

	protected void startMeasuring()
	{
		fPerformanceMeter.start();
	}

	protected void stopMeasuring()
	{
		fPerformanceMeter.stop();
	}

	protected void commitMeasurements()
	{
		fPerformanceMeter.commit();
	}

	protected void assertPerformance()
	{
		Performance.getDefault().assertPerformance(fPerformanceMeter);
	}
}

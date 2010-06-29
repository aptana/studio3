package com.aptana.editor.ruby;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.ruby.index.RubyFileIndexingParticipant;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexContainerJob;
import com.aptana.index.core.IndexManager;

public class CoreStubber extends Job
{

	private static final String GEM_COMMAND = "gem"; //$NON-NLS-1$
	private static final String RUBY_EXE = "ruby"; //$NON-NLS-1$
	private static final String VERSION_SWITCH = "-v"; //$NON-NLS-1$

	private static final String CORE_STUBBER_PATH = "ruby/core_stubber.rb"; //$NON-NLS-1$
	private static final String FINISH_MARKER_FILENAME = "finish_marker"; //$NON-NLS-1$

	public CoreStubber()
	{
		super("Generating stubs for Ruby Core"); //$NON-NLS-1$
		setPriority(Job.LONG);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		try
		{
			File outputDir = getRubyCoreStubDir();
			if (outputDir == null)
			{
				return Status.CANCEL_STATUS;
			}
			File finishMarker = new File(outputDir, FINISH_MARKER_FILENAME);
			// Skip if we already generated core stubs for this ruby...
			if (!finishMarker.exists())
			{
				generateCoreStubs(outputDir, finishMarker);
			}
			sub.setWorkRemaining(10);

			final IProgressMonitor pm = Job.getJobManager().createProgressGroup();
			final List<Job> jobs = new ArrayList<Job>();
			jobs.add(indexCoreStubs(outputDir));
			jobs.addAll(indexStdLib());
			jobs.addAll(indexGems());
			pm.beginTask("Indexing Ruby environment", jobs.size());
			for (Job job : jobs)
			{
				if (job == null)
				{
					continue;
				}
				job.setProgressGroup(pm, 1);
				job.schedule();
			}
			// Use a thread to report back to progress monitor when all the jobs are done.
			Thread t = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					for (Job job : jobs)
					{
						if (job == null)
						{
							continue;
						}
						try
						{
							job.join();
						}
						catch (InterruptedException e)
						{
							// ignore
						}
					}
					pm.done();
				}
			});
			t.start();
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		}
		finally
		{
			sub.done();
		}
		return Status.OK_STATUS;
	}

	public static Index getRubyCoreIndex()
	{
		File stubDir = getRubyCoreStubDir();
		if (stubDir == null)
		{
			return null;
		}
		return IndexManager.getInstance().getIndex(stubDir.toURI());
	}

	protected static File getRubyCoreStubDir()
	{
		String rubyVersion = ProcessUtil.outputForCommand(RUBY_EXE, null, ShellExecutable.getEnvironment(),
				VERSION_SWITCH);
		if (rubyVersion == null)
		{
			return null;
		}
		// Store core stubs based on ruby version string...
		IPath outputPath = Activator.getDefault().getStateLocation().append(Integer.toString(rubyVersion.hashCode()))
				.append(RUBY_EXE);
		return outputPath.toFile();
	}

	protected List<Job> indexGems()
	{
		List<Job> jobs = new ArrayList<Job>();
		for (IPath gemPath : getGemPaths())
		{
			jobs.add(indexFiles(gemPath.toFile().toURI()));
		}
		return jobs;
	}

	public static Set<IPath> getGemPaths()
	{
		IPath gemCommand = ExecutableUtil.find(GEM_COMMAND, true, null);
		String command = GEM_COMMAND;
		if (gemCommand != null)
		{
			command = gemCommand.toOSString();
		}
		// FIXME Not finding my user gem path on Windows...
		String gemEnvOutput = ProcessUtil.outputForCommand(command, null, ShellExecutable.getEnvironment(),
				"env", "gempath"); //$NON-NLS-1$ //$NON-NLS-2$
		if (gemEnvOutput == null)
		{
			return Collections.emptySet();
		}
		Set<IPath> paths = new HashSet<IPath>();
		String[] gemPaths = gemEnvOutput.split(File.pathSeparator);
		if (gemPaths != null)
		{
			for (String gemPath : gemPaths)
			{
				IPath gemsPath = new Path(gemPath).append("gems"); //$NON-NLS-1$
				paths.add(gemsPath);
			}
		}
		return paths;
	}

	protected List<Job> indexStdLib()
	{
		List<Job> jobs = new ArrayList<Job>();
		for (IPath loadpath : getLoadpaths())
		{
			Job job = indexFiles(loadpath.toFile().toURI());
			if (job != null)
			{
				jobs.add(job);
			}
		}
		return jobs;
	}

	public static Set<IPath> getLoadpaths()
	{
		String rawLoadPathOutput = ProcessUtil.outputForCommand(RUBY_EXE, null, ShellExecutable.getEnvironment(),
				"-e", "puts $:"); //$NON-NLS-1$ //$NON-NLS-2$
		if (rawLoadPathOutput == null)
		{
			return Collections.emptySet();
		}
		Set<IPath> paths = new HashSet<IPath>();
		String[] loadpaths = rawLoadPathOutput.split("\r\n|\r|\n"); //$NON-NLS-1$
		if (loadpaths != null)
		{
			// TODO What about when one loadpath is a parent of another, just filter to parent?
			for (String loadpath : loadpaths)
			{
				if (loadpath.equals(".")) //$NON-NLS-1$
					continue;
				paths.add(new Path(loadpath));
			}
		}
		return paths;
	}

	protected Job indexCoreStubs(File outputDir)
	{
		return indexFiles("Indexing Ruby Core", outputDir.toURI());
	}

	protected void generateCoreStubs(File outputDir, File finishMarker) throws IOException
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(CORE_STUBBER_PATH), null);
		File stubberScript = ResourceUtil.resourcePathToFile(url);

		Map<Integer, String> stubberResult = ProcessUtil.runInBackground(RUBY_EXE, null,
				ShellExecutable.getEnvironment(), stubberScript.getAbsolutePath(), outputDir.getAbsolutePath());
		int exitCode = stubberResult.keySet().iterator().next();
		if (exitCode != 0)
		{
			String stubberOutput = stubberResult.values().iterator().next();
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, stubberOutput, null));
		}
		else
		{
			// Now write empty file as a marker that core stubs were generated to completion...
			finishMarker.createNewFile();
		}
	}

	protected Job indexFiles(String message, URI outputDir)
	{
		return new IndexContainerJob(message, outputDir)
		{
			protected Map<IFileStoreIndexingParticipant, Set<IFileStore>> mapParticipantsToFiles(
					Set<IFileStore> fileStores)
			{
				Map<IFileStoreIndexingParticipant, Set<IFileStore>> map = new HashMap<IFileStoreIndexingParticipant, Set<IFileStore>>();
				map.put(new RubyFileIndexingParticipant(), fileStores);
				return map;
			}
		};
	}

	protected Job indexFiles(URI outputDir)
	{
		return new IndexContainerJob(outputDir)
		{
			protected Map<IFileStoreIndexingParticipant, Set<IFileStore>> mapParticipantsToFiles(
					Set<IFileStore> fileStores)
			{
				Map<IFileStoreIndexingParticipant, Set<IFileStore>> map = new HashMap<IFileStoreIndexingParticipant, Set<IFileStore>>();
				map.put(new RubyFileIndexingParticipant(), fileStores);
				return map;
			}
		};
	}

}

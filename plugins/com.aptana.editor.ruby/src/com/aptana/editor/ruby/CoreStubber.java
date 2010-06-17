package com.aptana.editor.ruby;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ProcessUtil;
import com.aptana.editor.ruby.index.RubyFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexActivator;
import com.aptana.index.core.IndexManager;

public class CoreStubber extends Job
{

	private static final String RUBY_EXE = "ruby"; //$NON-NLS-1$
	private static final String VERSION_SWITCH = "-v"; //$NON-NLS-1$

	private static final String CORE_STUBBER_PATH = "ruby/core_stubber.rb"; //$NON-NLS-1$
	private static final String FINISH_MARKER_FILENAME = "finish_marker"; //$NON-NLS-1$

	public CoreStubber()
	{
		super("Generating stubs for Ruby Core"); //$NON-NLS-1$
		// setSystem(true);
		setPriority(Job.LONG);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		try
		{
			String rubyVersion = ProcessUtil.outputForCommand(RUBY_EXE, null, ShellExecutable.getEnvironment(),
					VERSION_SWITCH);
			// Store core stubs based on ruby version string...
			IPath outputPath = Activator.getDefault().getStateLocation()
					.append(Integer.toString(rubyVersion.hashCode())).append(RUBY_EXE);
			File outputDir = outputPath.toFile();
			File finishMarker = new File(outputDir, FINISH_MARKER_FILENAME);
			// Skip if we already generated core stubs for this ruby...
			if (!finishMarker.exists())
			{
				generateCoreStubs(outputDir, finishMarker);
			}
			sub.setWorkRemaining(90);

			IProgressMonitor pm = Job.getJobManager().createProgressGroup();
			List<Job> jobs = new ArrayList<Job>();
			jobs.add(indexCoreStubs(rubyVersion, outputDir));
			jobs.addAll(indexStdLib());
			jobs.addAll(indexGems());
			pm.beginTask("Indexing Ruby environment", jobs.size());
			for (Job job : jobs)
			{
				job.setProgressGroup(pm, 1);
				job.schedule();
			}
			// TODO How can we ever call done on this progress monitor? it's sticking in the progress view..
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		}
		sub.done();
		return Status.OK_STATUS;
	}

	protected List<Job> indexGems()
	{
		List<Job> jobs = new ArrayList<Job>();
		String gemEnvOutput = ProcessUtil.outputForCommand("gem", null, ShellExecutable.getEnvironment(),
				"env", "gempath"); //$NON-NLS-1$
		String[] gemPaths = gemEnvOutput.split(":"); // FIXME Does this need to be File.pathSeparator?
		for (String gemPath : gemPaths)
		{
			IPath gemsPath = new Path(gemPath).append("gems");
			Index index = IndexManager.getInstance().getIndex(gemsPath.toPortableString());
			jobs.add(indexFiles(MessageFormat.format("Indexing {0}", gemsPath.toPortableString()), index,
					addFiles(gemsPath.toFile())));
		}
		return jobs;
	}

	protected List<Job> indexStdLib()
	{
		List<Job> jobs = new ArrayList<Job>();
		// Now index the loadpaths (Std Lib!)
		String rawLoadPathOutput = ProcessUtil.outputForCommand(RUBY_EXE, null, ShellExecutable.getEnvironment(),
				"-e", "puts $:"); //$NON-NLS-1$ //$NON-NLS-2$
		String[] loadpaths = rawLoadPathOutput.split("\r\n|\r|\n"); //$NON-NLS-1$
		// TODO What about when one loadpath is a parent of another, just filter to parent?
		for (String loadpath : loadpaths)
		{
			if (loadpath.equals(".")) //$NON-NLS-1$
				continue;
			Index index = IndexManager.getInstance().getIndex(loadpath);
			Job job = indexFiles(MessageFormat.format("Indexing {0}", loadpath), index, addFiles(new File(loadpath)));
			if (job != null)
			{
				jobs.add(job);
			}
		}
		return jobs;
	}

	protected Job indexCoreStubs(String rubyVersion, File outputDir)
	{
		Index coreIndex = IndexManager.getInstance().getIndex(rubyVersion);
		return indexFiles("Indexing Ruby Core", coreIndex, addFiles(outputDir));
	}

	protected void generateCoreStubs(File outputDir, File finishMarker) throws IOException
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(CORE_STUBBER_PATH), null);
		url = FileLocator.toFileURL(url);

		Map<Integer, String> stubberResult = ProcessUtil.runInBackground(RUBY_EXE, null,
				ShellExecutable.getEnvironment(), url.getFile(), outputDir.getAbsolutePath());
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

	protected Job indexFiles(String message, Index index, Set<IFileStore> files)
	{
		if (files == null || files.size() == 0)
			return null;
		return new IndexFileStoresJob(message, index, files);
	}

	private Set<IFileStore> addFiles(File file)
	{
		Set<IFileStore> files = new HashSet<IFileStore>();
		if (file == null || !file.exists())
			return files;
		if (file.isDirectory())
		{
			File[] fileList = file.listFiles();
			if (fileList == null)
				return files;

			for (File child : fileList)
			{
				files.addAll(addFiles(child));
			}
		}
		else
		{
			try
			{
				IFileStore store = EFS.getStore(file.toURI());
				if (store != null)
				{
					files.add(store);
				}
			}
			catch (CoreException e)
			{
				Activator.log(e);
			}
		}
		return files;
	}

	private static class IndexFileStoresJob extends Job
	{

		private final Set<IFileStore> files;
		private Index index;

		public IndexFileStoresJob(String message, Index index, Set<IFileStore> files)
		{
			super(message);
			this.files = files;
			this.index = index;
		}

		@Override
		public boolean belongsTo(Object family)
		{
			return index.getIndexFile().equals(family);
		}

		@Override
		public IStatus run(IProgressMonitor monitor)
		{
			SubMonitor sub = SubMonitor.convert(monitor, 2 * files.size());
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			try
			{
				// Should check timestamp of index versus timestamps of files, only index files that are out of date!
				filterFiles();

				// First cleanup indices for files
				for (IFileStore file : files)
				{
					if (sub.isCanceled())
					{
						return Status.CANCEL_STATUS;
					}
					// FIXME I'm storing everything in indices via URI's path, which is not right. We should store URI
					// string
					// and if it's a file for convenience then in proposals we can lop off the "file:" portion
					index.remove(file.toURI().getPath());
					sub.worked(1);
				}

				// Now parse and index the source
				RubyFileIndexingParticipant fileIndexingParticipant = new RubyFileIndexingParticipant();
				fileIndexingParticipant.index(files, index, sub.newChild(files.size()));
			}
			catch (CoreException e)
			{
				return e.getStatus();
			}
			finally
			{
				try
				{
					index.save();
				}
				catch (IOException e)
				{
					IndexActivator.logError("An error occurred while saving an index", e);
				}
			}
			return Status.OK_STATUS;
		}

		private void filterFiles()
		{
			long indexLastModified = index.getIndexFile().lastModified();
			Iterator<IFileStore> iter = files.iterator();
			while (iter.hasNext())
			{
				IFileStore file = iter.next();
				if (file.fetchInfo().getLastModified() < indexLastModified)
				{
					iter.remove();
				}
			}
		}

	}

}

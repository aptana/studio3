package com.aptana.editor.ruby;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.ruby.index.RubyFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexActivator;
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
			if (rubyVersion == null)
			{
				return Status.CANCEL_STATUS;
			}
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
			sub.setWorkRemaining(10);

			final IProgressMonitor pm = Job.getJobManager().createProgressGroup();
			final List<Job> jobs = new ArrayList<Job>();
			jobs.add(indexCoreStubs(rubyVersion, outputDir));
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

	protected List<Job> indexGems()
	{
		List<Job> jobs = new ArrayList<Job>();
		for (IPath gemPath : getGemPaths())
		{
			Index index = IndexManager.getInstance().getIndex(gemPath.toOSString());
			jobs.add(indexFiles(MessageFormat.format("Indexing {0}", gemPath.toOSString()), index,
					addFiles(gemPath.toFile())));
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
			Index index = IndexManager.getInstance().getIndex(loadpath.toOSString());
			Job job = indexFiles(MessageFormat.format("Indexing {0}", loadpath.toOSString()), index,
					addFiles(loadpath.toFile()));
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

	protected Job indexCoreStubs(String rubyVersion, File outputDir)
	{
		Index coreIndex = IndexManager.getInstance().getIndex(rubyVersion);
		return indexFiles("Indexing Ruby Core", coreIndex, addFiles(outputDir));
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
		private ArrayList<String> fileURIs;
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
				// Should check timestamp of index versus timestamps of files, only index files that are out of date
				// (for Ruby)!
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
				long timestamp = System.currentTimeMillis();
				fileIndexingParticipant.index(files, index, sub.newChild(files.size()));

				// Store some timestamp we can use to limit next pass indexing
				IEclipsePreferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);
				prefs.putLong(getIndexTimestampKey(), timestamp);
				prefs.flush();
			}
			catch (CoreException e)
			{
				return e.getStatus();
			}
			catch (BackingStoreException e)
			{
				Activator.log(e);
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

		private String getIndexTimestampKey()
		{
			return "LastIndex_Ruby_" + index.getIndexFile().getAbsolutePath();
		}

		private void filterFiles()
		{
			try
			{
				Set<String> documents = index.queryDocumentNames(null);
				for (String docName : documents)
				{
					if (!fileExists(docName))
					{
						index.remove(docName);
					}
				}
			}
			catch (IOException e)
			{
				IndexActivator.logError("Error occurred while removing stale index entries", e);
			}

			IEclipsePreferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);
			// We store something in the prefs for the last timestamp, since we can't go off timestamp of disk index.
			// Now we filter files to only those that have been added/changed since last index.
			long indexLastModified = prefs.getLong(getIndexTimestampKey(), -1);
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

		private boolean fileExists(String lastString)
		{
			return Collections.binarySearch(getFileURIs(), lastString) >= 0;
		}

		protected List<? extends String> getFileURIs()
		{
			if (fileURIs == null)
			{
				fileURIs = new ArrayList<String>();
				for (IFileStore store : files)
				{
					fileURIs.add(store.toURI().getPath());
				}
				Collections.sort(fileURIs);
			}
			return fileURIs;
		}
	}

}

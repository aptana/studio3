package com.aptana.editor.ruby;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
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
import com.aptana.index.core.IndexManager;

public class CoreStubber extends Job
{

	private static final String RUBY_EXE = "ruby"; //$NON-NLS-1$
	private static final String VERSION_SWITCH = "-v"; //$NON-NLS-1$

	private static final String CORE_STUBBER_PATH = "ruby/core_stubber.rb"; //$NON-NLS-1$
	private static final String FINISH_MARKER_FILENAME = "finish_marker"; //$NON-NLS-1$

	public CoreStubber()
	{
		super("Stubbing and Indexing Core Ruby...");
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
				URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(CORE_STUBBER_PATH), null);
				url = FileLocator.toFileURL(url);

				Map<Integer, String> stubberResult = ProcessUtil.runInBackground(RUBY_EXE, null,
						ShellExecutable.getEnvironment(), url.getFile(), outputDir.getAbsolutePath());
				int exitCode = stubberResult.keySet().iterator().next();
				if (exitCode != 0)
				{
					String stubberOutput = stubberResult.values().iterator().next();
					Activator.getDefault().getLog()
							.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, stubberOutput, null));
				}
				else
				{
					// Now write empty file as a marker that core stubs were generated to completion...
					finishMarker.createNewFile();
				}
			}
			sub.setWorkRemaining(75);

			// FIXME Don't re-index the core stubs if not necessary
			Index coreIndex = IndexManager.getInstance().getIndex(rubyVersion);
			indexFiles(coreIndex, addFiles(outputDir), sub.newChild(10));

			// FIXME Don't re-index the loadpaths if not necessary
			// Now index the loadpaths (Std Lib!)
			String rawLoadPathOutput = ProcessUtil.outputForCommand(RUBY_EXE, null, ShellExecutable.getEnvironment(),
					"-e", "puts $:"); //$NON-NLS-1$ //$NON-NLS-2$
			String[] loadpaths = rawLoadPathOutput.split("\r\n|\r|\n"); //$NON-NLS-1$
			for (String loadpath : loadpaths)
			{
				if (loadpath.equals(".")) //$NON-NLS-1$
					continue;
				Index index = IndexManager.getInstance().getIndex(loadpath);
				indexFiles(index, addFiles(new File(loadpath)), sub.newChild(65));
			}
		}
		catch (Exception e)
		{
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		}
		sub.done();
		return Status.OK_STATUS;
	}

	protected void indexFiles(Index index, Set<IFileStore> files, IProgressMonitor monitor) throws CoreException,
			IOException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 2);

		// First cleanup indices for files
		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}
			// FIXME I'm storing everything in indices via URI's path, which is not right. We should store URI string and if it's a file for convenience then in proposals we can lop off the "file:" portion
			index.remove(file.toURI().getPath());
			sub.worked(1);
		}

		// Now parse and index the source
		RubyFileIndexingParticipant fileIndexingParticipant = new RubyFileIndexingParticipant();
		fileIndexingParticipant.index(files, index, sub.newChild(files.size()));
		index.save();
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

}

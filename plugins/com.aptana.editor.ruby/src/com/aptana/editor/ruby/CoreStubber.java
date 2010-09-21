/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
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
			pm.beginTask(Messages.CoreStubber_IndexingRuby, jobs.size() * 1000);
			for (Job job : jobs)
			{
				if (job == null)
				{
					continue;
				}
				job.setProgressGroup(pm, 1000);
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
		return indexFiles(Messages.CoreStubber_IndexingRubyCore, outputDir.toURI());
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
		return new IndexRubyContainerJob(message, outputDir);
	}

	protected Job indexFiles(URI outputDir)
	{
		return new IndexRubyContainerJob(outputDir);
	}

	private static class IndexRubyContainerJob extends IndexContainerJob
	{
		public IndexRubyContainerJob(URI outputDir)
		{
			super(outputDir);
		}

		public IndexRubyContainerJob(String message, URI outputDir)
		{
			super(message, outputDir);
		}

		protected Map<IFileStoreIndexingParticipant, Set<IFileStore>> mapParticipantsToFiles(Set<IFileStore> fileStores)
		{
			Map<IFileStoreIndexingParticipant, Set<IFileStore>> map = new HashMap<IFileStoreIndexingParticipant, Set<IFileStore>>();
			map.put(new RubyFileIndexingParticipant(), fileStores);
			return map;
		}

		@Override
		protected Set<IFileStore> filterFiles(long indexLastModified, Set<IFileStore> files)
		{
			Set<IFileStore> firstPass = super.filterFiles(indexLastModified, files);
			if (firstPass == null || firstPass.isEmpty())
			{
				return firstPass;
			}
			// OK, now limit to only files that are ruby type!
			IContentTypeManager manager = Platform.getContentTypeManager();
			Set<IContentType> types = new HashSet<IContentType>();
			types.add(manager.getContentType(IRubyConstants.CONTENT_TYPE_RUBY));
			types.add(manager.getContentType(IRubyConstants.CONTENT_TYPE_RUBY_AMBIGUOUS));
			Set<IFileStore> filtered = new HashSet<IFileStore>();
			for (IFileStore store : firstPass)
			{
				if (hasType(store, types))
				{
					filtered.add(store);
				}
			}
			return filtered;
		}
	}

}

package com.aptana.git.ui.hyperlink;

import java.io.File;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.hyperlink.IHyperlinkDetector;
import com.aptana.terminal.views.TerminalView;

public class HyperlinkDetector implements IHyperlinkDetector
{
	private static Pattern GIT_FILEPATH_PATTERN = Pattern
			.compile("^(\\s*(Auto-merging|Removing|(delete|create) mode \\d+) (\\S+))|((\\S+)\\s+\\|\\s+((\\d+\\s+\\+*\\-*)|(Bin \\d+ \\-> \\d+ bytes)))|(rename (.+?) \\(\\d+%\\))"); //$NON-NLS-1$

	private static Pattern RENAME_PATTERN = Pattern.compile("(.+)\\{(.+?) => (.+?)\\}(.*)"); //$NON-NLS-1$

	public IHyperlink[] detectHyperlinks(String contents)
	{
		Matcher m = GIT_FILEPATH_PATTERN.matcher(contents);
		if (m.find())
		{
			int groupNum = 4; // try the first version
			String filepath = m.group(groupNum);
			if (filepath == null) // matched second version
			{
				groupNum = 6;
				filepath = m.group(groupNum);
			}
			if (filepath == null) // matched third version (rename)
			{
				groupNum = 11;
				filepath = m.group(groupNum);
				// Generate the renamed file
				Matcher renameMatcher = RENAME_PATTERN.matcher(filepath);
				if (renameMatcher.find())
				{
					filepath = renameMatcher.group(1) + renameMatcher.group(3) + renameMatcher.group(4);
				}
			}
			int start = m.start(groupNum);
			int length = m.end(groupNum) - start;
			return new IHyperlink[] { new GitHyperlink(new Region(start, length), filepath) };
		}
		return new IHyperlink[0];
	}

	private class GitHyperlink implements IHyperlink
	{

		private IRegion region;
		private String filepath;
		private HashSet<IPath> reposTried;

		public GitHyperlink(IRegion region, String filepath)
		{
			this.region = region;
			this.filepath = filepath;
		}

		public IRegion getHyperlinkRegion()
		{
			return region;
		}

		public String getTypeLabel()
		{
			return null;
		}

		public String getHyperlinkText()
		{
			return this.filepath;
		}

		public void open()
		{
			try
			{
				reposTried = new HashSet<IPath>();
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				File file = getFile();
				if (file == null)
				{
					return;
				}
				IFileStore store = EFS.getStore(file.toURI());
				if (store == null)
				{
					return;
				}
				IDE.openEditorOnFileStore(page, store);
			}
			catch (CoreException e)
			{
				GitUIPlugin.logError(e);
			}
			finally
			{
				reposTried = null;
			}
		}

		private File getFile()
		{
			// FIXME is there any way to find the "active"/"focused" terminal?

			// Let's try getting active terminals and getting the working directory!
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorReference[] refs = page.getEditorReferences();
			for (IEditorReference ref : refs)
			{
				if (TerminalEditor.ID.equals(ref.getId()))
				{
					File relative = getFileRelativeToWorkingDir(ref.getPart(false));
					if (relative != null)
					{
						return relative;
					}
				}
			}

			// Try Terminal Views
			IViewReference[] viewRefs = page.getViewReferences();
			for (IViewReference ref : viewRefs)
			{
				if (TerminalView.ID.equals(ref.getId()))
				{
					File relative = getFileRelativeToWorkingDir(ref.getPart(false));
					if (relative != null)
					{
						return relative;
					}
				}
			}

			// Now try all the repos you haven't tried before
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
			{
				GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(project);
				File relative = getFileFromRepo(repo);
				if (relative != null)
				{
					return relative;
				}
			}
			return null;
		}

		private File getFileRelativeToWorkingDir(IWorkbenchPart part)
		{
			if (part == null)
			{
				return null;
			}
			IPath workingDir = null;
			if (part instanceof TerminalView)
			{
				workingDir = ((TerminalView) part).getWorkingDirectory();
			}
			else if (part instanceof TerminalEditor)
			{
				workingDir = ((TerminalEditor) part).getWorkingDirectory();
			}
			if (workingDir == null)
			{
				return null;
			}

			// FIXME Use GitRepositoryManager.getUnattachedExisting(URI) to handle files not in workspace

			// is the working dir in the workspace?
			IContainer container = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(workingDir);
			if (container == null)
			{
				return null;
			}
			IProject project = container.getProject();
			// Grab related repo for the working dir
			GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(project);
			// If filepath starts with .., need to make it relative to working dir of the terminal
			if (filepath.startsWith("..") && repo != null) //$NON-NLS-1$
			{
				File relative = workingDir.append(filepath.substring(2)).toFile();
				if (relative.exists())
				{
					return relative;
				}
			}
			return getFileFromRepo(repo);
		}

		private File getFileFromRepo(GitRepository repo)
		{
			// TODO Keep track of the repos I've tried, don't try same one twice. Also try all the leftover ones at end
			// if we still haven't found file!
			if (repo == null)
			{
				return null;
			}
			IPath wd = repo.workingDirectory();
			if (reposTried.contains(wd))
			{
				return null;
			}
			reposTried.add(wd);
			File file = wd.append(filepath).toFile();
			if (file.exists())
			{
				return file;
			}

			return null;
		}
	}

}
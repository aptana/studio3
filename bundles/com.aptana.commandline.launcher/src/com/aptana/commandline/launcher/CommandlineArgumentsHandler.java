/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.commandline.launcher;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IProjectContext;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.FileUtil;

/**
 * Process command line arguments.
 * 
 * @author schitale
 */
public class CommandlineArgumentsHandler
{
	/**
	 * Process the command line arguments. Currently treats every argument as a file name and opens it in a editor.
	 * 
	 * @param arguments
	 */
	public static List<File> processCommandLineArgs(final String[] arguments)
	{
		if (arguments == null || arguments.length == 0)
		{
			return Collections.emptyList();
		}

		final List<File> files = FileUtil.gatherFilesFromCommandLineArguments(arguments);

		WorkbenchJob workbenchJob = new WorkbenchJob("Processing command line args.") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				SubMonitor sub = SubMonitor.convert(monitor, arguments.length);
				for (File f : files)
				{
					processArgument(f, sub.newChild(1));
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(workbenchJob);
		workbenchJob.setPriority(WorkbenchJob.INTERACTIVE);
		workbenchJob.schedule();

		return files;
	}

	protected static void processArgument(File file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 1);

		if (!file.exists())
		{
			return;
		}

		// Process an existing file.
		if (file.isFile())
		{
			processFile(file, sub.newChild(1));
		}
		// If we're opening on a directory, then wrap it up as a project if we can...
		else if (file.isDirectory())
		{
			processDirectory(file, sub.newChild(1));
		}
	}

	protected static void processDirectory(File file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 130);

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// first look for project description files
		File dotProject = new File(file, IProjectDescription.DESCRIPTION_FILE_NAME);
		String projectName = null;
		IProjectDescription description = null;
		if (dotProject.isFile())
		{
			// existing project!
			try
			{
				description = workspace.loadProjectDescription(Path.fromOSString(dotProject.getAbsolutePath()));
				projectName = description.getName();
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(CommandlineLauncherPlugin.getDefault(), e); //that's the best try!
			}
		}

		if (description == null)
		{
			// new, must create a project
			projectName = dotProject.getParentFile().getName();
			description = workspace.newProjectDescription(projectName);
			IPath locationPath = Path.fromOSString(dotProject.getParent());

			// If it is under the root use the default location
			if (Platform.getLocation().isPrefixOf(locationPath))
			{
				description.setLocation(null);
			}
			else
			{
				description.setLocation(locationPath);
			}
		}
		sub.worked(15);
		try
		{
			boolean forceAsActive = false;
			final IProject project = workspace.getRoot().getProject(projectName);
			if (!project.exists())
			{
				project.create(description, sub.newChild(30));
				forceAsActive = true;
			}
			sub.setWorkRemaining(85);
			if (project.isOpen())
			{
				forceAsActive = true;
			}
			else
			{
				project.open(IResource.BACKGROUND_REFRESH, sub.newChild(70));
			}
			sub.setWorkRemaining(15);
			if (forceAsActive)
			{
				setActiveProject(project);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logWarning(CommandlineLauncherPlugin.getDefault(), e);
		}
	}

	protected static void setActiveProject(final IProject project)
	{
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null)
				{
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null)
				{
					return;
				}
				IViewReference[] refs = page.getViewReferences();
				for (IViewReference ref : refs)
				{
					IWorkbenchPart view = ref.getPart(false);
					if (view instanceof IProjectContext)
					{
						IProjectContext pc = (IProjectContext) view;
						pc.setActiveProject(project);
						return;
					}
				}
			}
		});
	}

	protected static void processFile(File file, IProgressMonitor monitor)
	{
		IPath path = Path.fromOSString(file.getAbsolutePath());
		IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
		IEditorDescriptor editorDescriptor = null;
		if (fileForLocation == null)
		{
			IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(file.getName());
			editorDescriptor = editorRegistry.getDefaultEditor(file.getName(), contentType);
		}
		else
		{
			editorDescriptor = editorRegistry.getDefaultEditor(file.getName());
		}
		String editorId = (editorDescriptor == null ? "com.aptana.editor.text" : editorDescriptor.getId()); //$NON-NLS-1$
		try
		{
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file.toURI(),
					editorId, true);
		}
		catch (PartInitException e)
		{
			IdeLog.logWarning(CommandlineLauncherPlugin.getDefault(), e);
		}
	}
}

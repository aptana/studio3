/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.internal.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessUtil;

public class OpenInFinderHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return null;
		}
		Object context = event.getApplicationContext();
		if (context instanceof EvaluationContext)
		{
			EvaluationContext evContext = (EvaluationContext) event.getApplicationContext();
			Object input = evContext.getVariable(ISources.SHOW_IN_INPUT);
			if (input instanceof IFileEditorInput)
			{
				IFileEditorInput fei = (IFileEditorInput) input;
				open(fei.getFile().getLocationURI());
			}
			else if (input instanceof IURIEditorInput)
			{
				IURIEditorInput uriInput = (IURIEditorInput) input;
				open(uriInput.getURI());
			}
			else
			{
				@SuppressWarnings("unchecked")
				List<Object> selectedFiles = (List<Object>) evContext.getDefaultVariable();
				for (Object selected : selectedFiles)
				{
					IResource resource = null;
					if (selected instanceof IResource)
					{
						resource = (IResource) selected;
					}
					else if (selected instanceof IAdaptable)
					{
						resource = (IResource) ((IAdaptable) selected).getAdapter(IResource.class);
					}
					if (resource != null)
					{
						open(resource.getLocationURI());
					}
					else if (selected instanceof IFileStore)
					{
						IFileStore fileStore = (IFileStore) selected;
						open(fileStore.toURI());
					}
				}
			}
		}
		return null;
	}

	private boolean open(URI uri)
	{
		if (uri == null)
		{
			return false;
		}
		if (!"file".equalsIgnoreCase(uri.getScheme())) //$NON-NLS-1$
		{
			return false;
		}
		File file = new File(uri);
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return openInFinder(file);
		}
		else if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			return openInWindowsExplorer(file);
		}
		return openOnLinux(file);
	}

	private boolean openOnLinux(File file)
	{
		// Can only handle directories
		if (file.isFile())
		{
			file = file.getParentFile();
		}

		// TODO Do we also need to try 'gnome-open' or 'dolphin' if nautilus fails?
		IStatus result = ProcessUtil.runInBackground("nautilus", null, //$NON-NLS-1$
				file.getAbsolutePath());
		if (result == null)
		{
			return false;
		}
		return result.isOK();
	}

	private boolean openInWindowsExplorer(File file)
	{
		// This works for Windows XP Pro! Can't run under ProcessBuilder or it does some quoting/mangling of args that
		// breaks this!
		String explorer = PlatformUtil.expandEnvironmentStrings("%SystemRoot%\\explorer.exe"); //$NON-NLS-1$
		try
		{
			Process p = Runtime.getRuntime().exec("\"" + explorer + "\" /select,\"" + file.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return p.exitValue() == 0;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	private boolean openInFinder(File file)
	{
		String subcommand = "open"; //$NON-NLS-1$
		String path = file.getAbsolutePath();
		if (file.isFile())
		{
			subcommand = "reveal"; //$NON-NLS-1$
		}
		String appleScript = "tell application \"Finder\" to " + subcommand + " (POSIX file \"" + path + "\")\ntell application \"Finder\" to activate"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		IStatus result = ProcessUtil.runInBackground("osascript", null, "-e", appleScript); //$NON-NLS-1$ //$NON-NLS-2$
		if (result != null && result.isOK())
		{
			return true;
		}
		// TODO Log output if failed?
		return false;
	}

}

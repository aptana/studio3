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
package com.aptana.ui.internal.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
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
		// TODO Do we also need to try 'gnome-open' or 'dolphin' if nautilus fails?
		Map<Integer, String> result = ProcessUtil.runInBackground("nautilus", null, "\"" //$NON-NLS-1$ //$NON-NLS-2$
				+ file.getAbsolutePath() + "\""); //$NON-NLS-1$
		if (result == null || result.isEmpty())
		{
			return false;
		}
		return result.keySet().iterator().next() == 0;
	}

	private boolean openInWindowsExplorer(File file)
	{
		// This works for Windows XP Pro! Can't run under ProcessBuilder or it does some quoting/mangling of args that
		// breaks this!
		String explorer = PlatformUtil.expandEnvironmentStrings("%SystemRoot%\\explorer.exe"); //$NON-NLS-1$
		try
		{
			Process p = Runtime.getRuntime().exec("\"" + explorer + "\" /select,\"" + file.getAbsolutePath() + "\"");
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
		Map<Integer, String> result = ProcessUtil.runInBackground("osascript", null, "-e", appleScript); //$NON-NLS-1$ //$NON-NLS-2$
		if (result != null && result.keySet().iterator().next() == 0)
		{
			return true;
		}
		// TODO Log output if failed?
		return false;
	}

}

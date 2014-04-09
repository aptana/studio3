/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;

/**
 * This is a special class that listens to editors and makes sure that the tab name is unique. If two tabs share same
 * filename we append additional text to disambiguate them.
 * 
 * @author cwilliams
 */
class FilenameDifferentiator extends UIJob implements IPartListener
{

	/**
	 * Separates original filename from disambiguating name(s)
	 */
	private static final String SEPARATOR = " | "; //$NON-NLS-1$
	private Map<String, Set<IEditorPart>> baseNames;

	public FilenameDifferentiator()
	{
		super("Install filename differentiator"); //$NON-NLS-1$
		EclipseUtil.setSystemForJob(this);
		baseNames = new HashMap<String, Set<IEditorPart>>();
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		// TODO Listen for window open/closes and page add/removal; then register for every window, for every page
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore
		}

		if (workbench != null)
		{
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window != null)
			{
				window.getActivePage().addPartListener(this);
			}
		}
		return Status.OK_STATUS;
	}

	public void partActivated(IWorkbenchPart part)
	{
		disambiguate(part);
	}

	public void partBroughtToTop(IWorkbenchPart part)
	{
		// do nothing
	}

	public void partClosed(IWorkbenchPart part)
	{
		if (!(part instanceof IEditorPart))
		{
			return;
		}
		String title = getBaseName(part);
		synchronized (baseNames)
		{
			Set<IEditorPart> parts = baseNames.get(title);
			if (parts != null)
			{
				// Remove from map!
				parts.remove(part);
				if (parts.isEmpty())
				{
					baseNames.remove(title);
				}
				else if (parts.size() == 1)
				{
					// If parts is now of size == 1, we can revert the leftover editor title to the basename!
					setTitle(parts.iterator().next(), title);
				}
			}
		}
	}

	public void partDeactivated(IWorkbenchPart part)
	{
		// do nothing
	}

	public void partOpened(IWorkbenchPart part)
	{
		disambiguate(part);
	}

	private void disambiguate(IWorkbenchPart part)
	{
		if (!(part instanceof IEditorPart))
		{
			return;
		}
		// First we add to list of names
		String title = getBaseName(part);
		Set<IEditorPart> list;
		synchronized (baseNames)
		{
			list = baseNames.get(title);
			if (list == null)
			{
				list = new HashSet<IEditorPart>();
				baseNames.put(title, list);
			}
			list.add((IEditorPart) part);
		}
		// Now we need to disambiguate between all the entries in the list!
		if (list.size() > 1)
		{
			Map<IEditorPart, String> newTitles = getUnambiguousTitles(list);
			for (Map.Entry<IEditorPart, String> entry : newTitles.entrySet())
			{
				setTitle(entry.getKey(), entry.getValue());
			}
		}
	}

	private void setTitle(IEditorPart key, String value)
	{
		try
		{
			Method m = WorkbenchPart.class.getDeclaredMethod("setPartName", String.class); //$NON-NLS-1$
			m.setAccessible(true);
			m.invoke(key, value);
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	private String getBaseName(IWorkbenchPart part)
	{
		String title = part.getTitle();
		return title.split(Pattern.quote(SEPARATOR))[0];
	}

	private Map<IEditorPart, String> getUnambiguousTitles(Collection<IEditorPart> list)
	{
		Map<IEditorPart, IPath> map = new HashMap<IEditorPart, IPath>();
		int min = Integer.MAX_VALUE;
		IPath path;
		for (IEditorPart part : list)
		{
			path = getPath(part);
			if (path == null)
			{
				continue;
			}
			min = Math.min(path.segmentCount(), min);
			map.put(part, path);
		}

		// Need to disambiguate the titles!
		Map<IEditorPart, String> returnMap = new HashMap<IEditorPart, String>();
		Set<String> curSegments = new HashSet<String>();
		for (int i = 2; i <= min; i++)
		{
			returnMap.clear();
			curSegments.clear();
			for (Map.Entry<IEditorPart, IPath> entry : map.entrySet())
			{
				path = entry.getValue();
				String segment = path.segment(path.segmentCount() - i);
				if (curSegments.contains(segment))
				{
					break;
				}
				curSegments.add(segment);
				String title = path.lastSegment() + SEPARATOR + segment;
				returnMap.put(entry.getKey(), title);
			}
			// They're all unique, return them all!
			if (curSegments.size() == map.size())
			{
				return returnMap;
			}
		}

		// Something failed! What do we do now?
		return Collections.emptyMap();
	}

	private IPath getPath(IEditorPart otherEditor)
	{
		IEditorInput input = otherEditor.getEditorInput();
		try
		{
			if (input instanceof IPathEditorInput)
			{
				return ((IPathEditorInput) input).getPath();
			}

			URI uri = (URI) input.getAdapter(URI.class);
			if (uri != null)
			{
				return new Path(uri.getHost() + Path.SEPARATOR + uri.getPath());
			}
			if (input instanceof IURIEditorInput)
			{
				return URIUtil.toPath(((IURIEditorInput) input).getURI());
			}
		}
		catch (Exception e)
		{
		}
		return null;
	}

	public void dispose()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore
		}

		if (workbench != null)
		{
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window != null)
			{
				IWorkbenchPage page = window.getActivePage();
				if (page != null)
				{
					page.removePartListener(this);
				}
			}
		}
		if (baseNames != null)
		{
			baseNames.clear();
			baseNames = null;
		}
	}
}

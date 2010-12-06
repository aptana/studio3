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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.progress.UIJob;

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
	private HashMap<String, Set<IEditorPart>> baseNames;

	public FilenameDifferentiator()
	{
		super("Install filename differentiator"); //$NON-NLS-1$
		setSystem(true);
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
			CommonEditorPlugin.logError(e);
		}

	}

	protected String getBaseName(IWorkbenchPart part)
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
		if (input instanceof IPathEditorInput)
		{
			return ((IPathEditorInput) input).getPath();
		}
		
		URI uri = (URI) input.getAdapter(URI.class);
		if (uri != null) {
			return new Path(uri.getHost() + Path.SEPARATOR + uri.getPath());
		}
		if (input instanceof IURIEditorInput)
		{
			return URIUtil.toPath(((IURIEditorInput) input).getURI());
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
				window.getActivePage().removePartListener(this);
			}
		}
		baseNames.clear();
		baseNames = null;
	}
}

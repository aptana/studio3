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
package com.aptana.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.explorer.internal.ui.SingleProjectView;
import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;
import com.aptana.scripting.model.WorkingDirectoryType;

public class ExplorerContextContributor implements ContextContributor
{
	private static final String TM_SELECTED_FILES = "TM_SELECTED_FILES"; //$NON-NLS-1$
	private static final String PROJECT_PROPERTY_NAME = "project"; //$NON-NLS-1$
	private static final String PROJECT_RUBY_CLASS = "Project"; //$NON-NLS-1$

	/**
	 * ExplorerContextContributor
	 */
	public ExplorerContextContributor()
	{
	}

	/**
	 * getActiveProject
	 * 
	 * @return
	 */
	private IProject getActiveProject()
	{
		// First try and get the active project for the instance of the App Explorer open in the active window
		final IProject[] projects = new IProject[1];
		try
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
					if (refs == null)
					{
						return;
					}
					for (IViewReference ref : refs)
					{
						if (ref == null || !ref.getId().equals(IExplorerUIConstants.VIEW_ID))
						{
							continue;
						}
						SingleProjectView view = (SingleProjectView) ref.getPart(false);
						if (view == null)
						{
							continue;
						}
						IProject activeProject = view.getActiveProject();
						if (activeProject != null)
						{
							projects[0] = activeProject;
							return;
						}
					}
				}
			});
		}
		catch (IllegalStateException e)
		{
			// workbench hasn't been created yet. Non-UI unit test launch?
		}
		if (projects[0] != null)
		{
			return projects[0];
		}

		// Fall back to using project stored in prefs.
		IPreferencesService preferencesService = Platform.getPreferencesService();
		String activeProjectName = preferencesService.getString(ExplorerPlugin.PLUGIN_ID,
				IPreferenceConstants.ACTIVE_PROJECT, null, null);
		IProject result = null;

		if (activeProjectName != null)
		{
			result = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	public void modifyContext(CommandElement command, CommandContext context)
	{
		IProject project = this.getActiveProject();

		if (project != null && command != null)
		{
			// This contributor is responsible for setting the path for this working dir type
			if (command.getWorkingDirectoryType().equals(WorkingDirectoryType.CURRENT_PROJECT))
			{
				command.setWorkingDirectoryPath(project.getLocation());
			}

			Ruby runtime = command.getRuntime();

			if (runtime != null)
			{
				IRubyObject rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RUBLE_MODULE,
						PROJECT_RUBY_CLASS, JavaEmbedUtils.javaToRuby(runtime, project));

				context.put(PROJECT_PROPERTY_NAME, rubyInstance);
			}
			else
			{
				context.put(PROJECT_PROPERTY_NAME, null);
			}
			// Add TM_SELECTED_FILES!
			addSelectedFiles(context);
		}
	}

	private void addSelectedFiles(CommandContext context)
	{
		CommonNavigator nav = getAppExplorer();
		if (nav == null)
			return;

		StringBuilder builder = new StringBuilder();
		ISelection sel = nav.getCommonViewer().getSelection();
		if (sel instanceof IStructuredSelection)
		{
			IStructuredSelection struct = (IStructuredSelection) sel;
			for (Object selected : struct.toArray())
			{
				// TODO Should we handle IAdaptables that can be adapted to IResources?
				if (selected instanceof IResource)
				{
					IPath location = ((IResource) selected).getLocation();
					if (location != null)
					{
						builder.append("'").append(location.toOSString()).append("' "); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
		if (builder.length() > 0)
		{
			builder.deleteCharAt(builder.length() - 1);
			context.put(TM_SELECTED_FILES, builder.toString());
		}
	}

	private CommonNavigator getAppExplorer()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null)
		{
			IViewReference[] refs = window.getActivePage().getViewReferences();

			for (IViewReference ref : refs)
			{
				if (ref.getId().equals(IExplorerUIConstants.VIEW_ID))

				{
					IViewPart part = ref.getView(false);

					if (part instanceof CommonNavigator)
					{
						return (CommonNavigator) part;
					}
				}
			}
		}

		return null;
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal.startup;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.browser.PortalBrowserEditor;
import com.aptana.portal.ui.internal.Portal;

/**
 * Aptana portal browser startup.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PortalStartup implements IStartup
{
	private static final String TITANIUM_MOBILE_NATURE = "com.appcelerator.titanium.mobile.nature"; //$NON-NLS-1$

	public void earlyStartup()
	{
		Job job = new UIJob("Launching Aptana Developer Toolbox...") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				Portal portal = Portal.getInstance();
				if (portal.shouldOpenPortal())
				{
					portal.openPortal(null, PortalBrowserEditor.WEB_BROWSER_EDITOR_ID);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule(500);

		// Register for a new project creation.

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new ProjectCreateListener(), IResourceChangeEvent.POST_CHANGE);
	}

	private class ProjectCreateListener implements IResourceChangeListener
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			if (event == null || event.getDelta() == null)
			{
				return;
			}
			try
			{
				event.getDelta().accept(new IResourceDeltaVisitor()
				{
					public boolean visit(IResourceDelta delta) throws CoreException
					{
						if (delta.getKind() == IResourceDelta.ADDED)
						{
							final IResource resource = delta.getResource();
							if (resource instanceof IProject && resource.isAccessible()
									&& ((IProject) resource).getNature(TITANIUM_MOBILE_NATURE) != null)
							{
								IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PortalUIPlugin.PLUGIN_ID);
								prefs.put(IPortalPreferences.RECENTLY_CREATED_PROJECT, resource.getName());
								try
								{
									prefs.flush();
								}
								catch (BackingStoreException e)
								{
									IdeLog.logWarning(PortalUIPlugin.getDefault(), e);
								}
								return false;
							}
						}
						return true;
					}
				});
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(PortalUIPlugin.getDefault(), e);
			}
		}
	}
}

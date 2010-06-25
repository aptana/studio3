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

package com.aptana.ide.core.io;

import java.util.WeakHashMap;

import org.eclipse.core.internal.resources.DelayedSnapshotJob;
import org.eclipse.core.internal.resources.SaveManager;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.io.auth.AuthenticationManager;
import com.aptana.ide.core.io.auth.IAuthenticationManager;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;
import com.aptana.ide.core.io.internal.DeleteResourceShortcutListener;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings({"restriction", "deprecation"})
public class CoreIOPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.core.io"; //$NON-NLS-1$

	// The shared instance
	private static CoreIOPlugin plugin;

	private WeakHashMap<Object, ConnectionContext> connectionContexts = new WeakHashMap<Object, ConnectionContext>();

	private IConnectionPointListener listener = new IConnectionPointListener()
	{

		public void connectionPointChanged(ConnectionPointEvent event)
		{
			// saves the connections on any change instead of waiting for the
			// shutdown in case of workbench crash
			SaveManager saveManager = ((Workspace) ResourcesPlugin.getWorkspace()).getSaveManager();
			(new DelayedSnapshotJob(saveManager)).schedule();
		}
	};

	/**
	 * The constructor
	 */
	public CoreIOPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(this, new WorkspaceSaveParticipant());
		if (lastState != null)
		{
			IPath location = lastState.lookup(new Path(ConnectionPointManager.STATE_FILENAME));
			if (location != null)
			{
				ConnectionPointManager.getInstance().loadState(getStateLocation().append(location));
			}
		}

		ResourcesPlugin.getWorkspace().addResourceChangeListener(new DeleteResourceShortcutListener(),
				IResourceChangeEvent.POST_CHANGE);
		getConnectionPointManager().addConnectionPointListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
			getConnectionPointManager().removeConnectionPointListener(listener);
			connectionContexts.clear();
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CoreIOPlugin getDefault()
	{
		return plugin;
	}

	public static void log(Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg)
	{
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e)
	{
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	/**
	 * Returns the Connection Manager instance
	 * 
	 * @return
	 */
	public static IConnectionPointManager getConnectionPointManager()
	{
		return ConnectionPointManager.getInstance();
	}

	public static IAuthenticationManager getAuthenticationManager()
	{
		return AuthenticationManager.getInstance();
	}

	public static void setConnectionContext(Object key, ConnectionContext context)
	{
		getDefault().connectionContexts.put(key, context);
	}

	public static void clearConnectionContext(Object key)
	{
		ConnectionContext context = getDefault().connectionContexts.get(key);
		if (context != null) {
			context.clear();
		}
		getDefault().connectionContexts.remove(key);
	}

	public static ConnectionContext getConnectionContext(Object key)
	{
		return getDefault().connectionContexts.get(key);
	}

	private class WorkspaceSaveParticipant implements ISaveParticipant
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException
		{
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException
		{
			IPath savePath = new Path(ConnectionPointManager.STATE_FILENAME).addFileExtension(Integer.toString(context
					.getSaveNumber()));
			ConnectionPointManager.getInstance().saveState(getStateLocation().append(savePath));
			context.map(new Path(ConnectionPointManager.STATE_FILENAME), savePath);
			context.needSaveNumber();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context)
		{
			IPath prevSavePath = new Path(ConnectionPointManager.STATE_FILENAME).addFileExtension(Integer
					.toString(context.getPreviousSaveNumber()));
			getStateLocation().append(prevSavePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context)
		{
			IPath savePath = new Path(ConnectionPointManager.STATE_FILENAME).addFileExtension(Integer.toString(context
					.getSaveNumber()));
			getStateLocation().append(savePath).toFile().delete();
		}
	}
}

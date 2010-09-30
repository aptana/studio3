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
package com.aptana.ide.syncing.core;

import java.io.File;

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

import com.aptana.core.CorePlugin;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("deprecation")
public class SyncingPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.syncing.core"; //$NON-NLS-1$

	// The shared instance
	private static SyncingPlugin plugin;

	private IConnectionPointListener connectionListener = new IConnectionPointListener()
	{

		public void connectionPointChanged(ConnectionPointEvent event)
		{
			IConnectionPoint connection = event.getConnectionPoint();
			IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
			ConnectionPointType type = manager.getType(connection);
			if (type == null)
			{
				return;
			}

			switch (event.getKind())
			{
				case ConnectionPointEvent.POST_ADD:
					ISiteConnection[] sites = getSiteConnectionManager().getSiteConnections();
					IConnectionPoint destination;
					String id = connection.getId();
					for (ISiteConnection site : sites)
					{
						destination = site.getDestination();
						if (destination != null && destination.getId().equals(id))
						{
							// the destination is changed to a new type
							site.setDestination(connection);
						}
					}
			}
		}
	};

	/**
	 * The constructor
	 */
	public SyncingPlugin()
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
			IPath location = lastState.lookup(new Path(SiteConnectionManager.STATE_FILENAME));
			if (location != null)
			{
				SiteConnectionManager.getInstance().loadState(getStateLocation().append(location));
			}
			location = lastState.lookup(new Path(DefaultSiteConnection.STATE_FILENAME));
			if (location != null)
			{
				DefaultSiteConnection.getInstance().loadState(getStateLocation().append(location));
			}
		}

		// For 1.5 compatibility
		lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(CorePlugin.getDefault(), new ISaveParticipant()
		{

			public void doneSaving(ISaveContext context)
			{
			}

			public void prepareToSave(ISaveContext context) throws CoreException
			{
			}

			public void rollback(ISaveContext context)
			{
			}

			public void saving(ISaveContext context) throws CoreException
			{
			}
		});
		if (lastState != null)
		{
			IPath location = lastState.lookup(new Path("save")); //$NON-NLS-1$
			if (location != null)
			{
				IPath absoluteLocation = CorePlugin.getDefault().getStateLocation().append(location);
				// only loads it once
				SiteConnectionManager.getInstance().loadState(absoluteLocation);
				File file = absoluteLocation.toFile();
				if (!file.renameTo(new File(absoluteLocation.toOSString() + ".bak"))) { //$NON-NLS-1$
					file.delete();
				}
			}
		}
		
		CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(connectionListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
		ResourcesPlugin.getWorkspace().removeSaveParticipant(CorePlugin.getDefault());
		CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(connectionListener);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SyncingPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the site Connection Manager instance
	 * 
	 * @return
	 */
	public static ISiteConnectionManager getSiteConnectionManager()
	{
		return SiteConnectionManager.getInstance();
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	public static void logError(String msg, Exception e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
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
			int saveNum = context.getSaveNumber();
			IPath savePath = new Path(SiteConnectionManager.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			SiteConnectionManager.getInstance().saveState(getStateLocation().append(savePath));
			context.map(new Path(SiteConnectionManager.STATE_FILENAME), savePath);

			savePath = new Path(DefaultSiteConnection.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			DefaultSiteConnection.getInstance().saveState(getStateLocation().append(savePath));
			context.map(new Path(DefaultSiteConnection.STATE_FILENAME), savePath);

			context.needSaveNumber();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context)
		{
			int prevNum = context.getPreviousSaveNumber();
			IPath prevSavePath = new Path(SiteConnectionManager.STATE_FILENAME).addFileExtension(Integer
					.toString(prevNum));
			getStateLocation().append(prevSavePath).toFile().delete();

			prevSavePath = new Path(DefaultSiteConnection.STATE_FILENAME).addFileExtension(Integer.toString(prevNum));
			getStateLocation().append(prevSavePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context)
		{
			int saveNum = context.getSaveNumber();
			IPath savePath = new Path(SiteConnectionManager.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			getStateLocation().append(savePath).toFile().delete();

			savePath = new Path(DefaultSiteConnection.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			getStateLocation().append(savePath).toFile().delete();
		}
	}
}

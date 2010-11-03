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
package com.aptana.preview;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.preview.server.ServerConfigurationManager;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("deprecation")
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.preview"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(this, new WorkspaceSaveParticipant());
		if (lastState != null) {
			IPath location = lastState.lookup(new Path(ServerConfigurationManager.STATE_FILENAME));
			if (location != null) {
				ServerConfigurationManager.getInstance().loadState(getStateLocation().append(location));
			}
		}
		PreviewManager.getInstance().init();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
		PreviewManager.getInstance().dispose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg) {
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e) {
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	private class WorkspaceSaveParticipant implements ISaveParticipant {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse
		 * .core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
			IPath prevSavePath = new Path(ServerConfigurationManager.STATE_FILENAME).addFileExtension(Integer
					.toString(context.getPreviousSaveNumber()));
			getStateLocation().append(prevSavePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse
		 * .core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.
		 * core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			IPath savePath = new Path(ServerConfigurationManager.STATE_FILENAME).addFileExtension(Integer
					.toString(context.getSaveNumber()));
			getStateLocation().append(savePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core
		 * .resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException {
			IPath savePath = new Path(ServerConfigurationManager.STATE_FILENAME).addFileExtension(Integer
					.toString(context.getSaveNumber()));
			ServerConfigurationManager.getInstance().saveState(getStateLocation().append(savePath));
			context.map(new Path(ServerConfigurationManager.STATE_FILENAME), savePath);
			context.needSaveNumber();
		}
	}

}

package com.aptana.webserver.core;

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

/**
 * 
 * @author Max Stepanov
 *
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.webserver.core"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	@SuppressWarnings("deprecation")
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	@SuppressWarnings("deprecation")
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
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

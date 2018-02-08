package com.aptana.js.debug.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.aptana.debug.core.DebugOptionsManager;

/**
 * @author Max Stepanov
 */
public class JSDebugPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.js.debug.core"; //$NON-NLS-1$

	// The shared instance
	private static JSDebugPlugin plugin;

	private DebugOptionsManager debugOptionsManager;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		debugOptionsManager = new DebugOptionsManager(IJSDebugConstants.ID_DEBUG_MODEL);
		debugOptionsManager.startup();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception {
		debugOptionsManager.shutdown();
		debugOptionsManager = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JSDebugPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the debugOptionsManager
	 */
	public DebugOptionsManager getDebugOptionsManager() {
		return debugOptionsManager;
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

}

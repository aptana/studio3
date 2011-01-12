package com.aptana.js.debug.core;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com.aptana.debug.core.DebugOptionsManager;
import com.aptana.debug.core.IEditorOpenAdapter;

/**
 * 
 * @author Max Stepanov
 *
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

	/**
	 * Forces to open source element in default editor
	 * 
	 * @param sourceElement
	 */
	public static void openInEditor(Object sourceElement) {
		IEditorOpenAdapter adapter = (IEditorOpenAdapter) getDefault().getContributedAdapter(IEditorOpenAdapter.class);
		if (adapter != null) {
			adapter.openInEditor(sourceElement);
		}
	}

	private Object getContributedAdapter(Class<?> clazz) {
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName())) {
			adapter = manager.getAdapter(this, clazz.getName());
			if (adapter == null) {
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}

}

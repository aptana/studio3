package com.aptana.editor.findbar;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * This class controls the plug-in life cycle
 */
public class FindBarPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.findbar"; //$NON-NLS-1$

	// The shared instance
	private static FindBarPlugin plugin;
	
	/**
	 * The constructor
	 */
	public FindBarPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static FindBarPlugin getDefault() {
		return plugin;
	}
	
	public static final String CLOSE = "/icons/close.png"; //$NON-NLS-1$
	public static final String PREVIOUS = "/icons/previous.png"; //$NON-NLS-1$
	public static final String NEXT = "/icons/next.png"; //$NON-NLS-1$
	public static final String SIGMA = "/icons/sigma.png"; //$NON-NLS-1$
	public static final String FINDREPLACE = "/icons/findreplace.png"; //$NON-NLS-1$
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(CLOSE, imageDescriptorFromPlugin(PLUGIN_ID, CLOSE));
		reg.put(PREVIOUS, imageDescriptorFromPlugin(PLUGIN_ID, PREVIOUS));
		reg.put(NEXT, imageDescriptorFromPlugin(PLUGIN_ID, NEXT));
		reg.put(SIGMA, imageDescriptorFromPlugin(PLUGIN_ID, SIGMA));
		reg.put(FINDREPLACE, imageDescriptorFromPlugin(PLUGIN_ID, FINDREPLACE));
	}
	
	public Image getImage(String imageID) {
		return getImageRegistry().get(imageID);
	}

}

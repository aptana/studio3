package com.aptana.index.core;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IndexActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.index.core"; //$NON-NLS-1$

	// The shared instance
	private static IndexActivator plugin;
	
	private ResourceIndexer resourceChangeListener;
	
	ISaveParticipant saveParticipant = new ISaveParticipant()
	{
		
		public void saving(ISaveContext context) throws CoreException
		{
			if (context.getKind() == ISaveContext.FULL_SAVE) {
				context.needDelta();
			}
		}
		
		public void rollback(ISaveContext context)
		{
			
		}
		
		public void prepareToSave(ISaveContext context) throws CoreException
		{
			
		}
		
		public void doneSaving(ISaveContext context)
		{
			
		}
	};
	
	/**
	 * The constructor
	 */
	public IndexActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IndexManager.getInstance();
		resourceChangeListener = new ResourceIndexer();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);

		// Register save participant to process any deltas that occured since last save
		ISavedState savedState = ResourcesPlugin.getWorkspace().addSaveParticipant(this, saveParticipant);
		if (savedState != null) {
			try {
				resourceChangeListener.processIResourceChangeEventPOST_BUILD.set(savedState);
				savedState.processResourceChangeEvents(resourceChangeListener);
			} finally {
				resourceChangeListener.processIResourceChangeEventPOST_BUILD.set(null);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// Clean up
		ResourcesPlugin.getWorkspace().removeSaveParticipant(this);

		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IndexActivator getDefault() {
		return plugin;
	}

}

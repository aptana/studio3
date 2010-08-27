package com.aptana.index.core;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("deprecation")
public class IndexActivator extends Plugin
{

	public static final String PLUGIN_ID = "com.aptana.index.core"; //$NON-NLS-1$
	private static IndexActivator plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IndexActivator getDefault()
	{
		return plugin;
	}

	/**
	 * logError
	 * 
	 * @param e
	 */
	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	/**
	 * logError
	 * 
	 * @param msg
	 * @param e
	 */
	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	ISaveParticipant saveParticipant = new ISaveParticipant()
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
			if (context.getKind() == ISaveContext.FULL_SAVE)
			{
				context.needDelta();
			}
		}
	};

	/**
	 * The constructor
	 */
	public IndexActivator()
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

		Job job = new Job("Start Resource Indexer") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				ResourceIndexer resourceChangeListener = new ResourceIndexer();
				final IWorkspace workspace = ResourcesPlugin.getWorkspace();
				workspace.addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.PRE_DELETE
						| IResourceChangeEvent.POST_CHANGE);

				try
				{
					// Register save participant to process any deltas that occurred since last save
					ISavedState savedState = workspace.addSaveParticipant(plugin, saveParticipant);
					if (savedState != null)
					{
						try
						{
							resourceChangeListener.processIResourceChangeEventPOST_BUILD.set(savedState);
							savedState.processResourceChangeEvents(resourceChangeListener);
						}
						finally
						{
							resourceChangeListener.processIResourceChangeEventPOST_BUILD.set(null);
						}
					}
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			// Clean up
			ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}
}

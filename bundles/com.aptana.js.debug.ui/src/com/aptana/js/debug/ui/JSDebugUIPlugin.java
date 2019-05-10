/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor

package com.aptana.js.debug.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.core.util.EclipseUtil;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.js.debug.ui.internal.DebugUIImages;
import com.aptana.js.debug.ui.internal.LaunchConfigurationsHelper;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class JSDebugUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.js.debug.ui"; //$NON-NLS-1$

	// The shared instance
	private static JSDebugUIPlugin plugin;

	private IDebugModelPresentation fUtilPresentation;
	private IDebugEventSetListener debugEventListener;

	/**
	 * The constructor
	 */
	public JSDebugUIPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		if (debugEventListener == null)
		{
			debugEventListener = new IDebugEventSetListener()
			{
				public void handleDebugEvents(DebugEvent[] events)
				{
					for (DebugEvent event : events)
					{
						if ((event.getSource() instanceof IJSDebugTarget) && (event.getKind() == DebugEvent.TERMINATE))
						{
							WorkbenchJob job = new WorkbenchJob("Close Temporary Debug Editors") { //$NON-NLS-1$
								public IStatus runInUIThread(IProgressMonitor monitor)
								{
									closeDebugEditors();
									return Status.OK_STATUS;
								}

							};
							job.setPriority(Job.INTERACTIVE);
							EclipseUtil.setSystemForJob(job);
							job.schedule();
						}
					}
				}
			};
		}
		DebugPlugin.getDefault().addDebugEventListener(debugEventListener);
		LaunchConfigurationsHelper.doCheckDefaultLaunchConfigurations();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		DebugPlugin.getDefault().removeDebugEventListener(debugEventListener);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JSDebugUIPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#createImageRegistry()
	 */
	protected ImageRegistry createImageRegistry()
	{
		return DebugUIImages.getImageRegistry();
	}

	/**
	 * getModelPresentation
	 * 
	 * @return IDebugModelPresentation
	 */
	public IDebugModelPresentation getModelPresentation()
	{
		if (fUtilPresentation == null)
		{
			fUtilPresentation = DebugUITools.newDebugModelPresentation(IJSDebugConstants.ID_DEBUG_MODEL);
		}
		return fUtilPresentation;
	}

	private void closeDebugEditors()
	{
		IWorkbenchPage page = UIUtils.getActivePage();
		if (page != null)
		{
			IEditorReference[] editorRefs = page.getEditorReferences();
			List<IEditorReference> closeEditors = new ArrayList<IEditorReference>();
			for (IEditorReference ref : editorRefs)
			{
				try
				{
					IEditorInput input = ref.getEditorInput();
					UniformResourceStorage storage = (UniformResourceStorage) input
							.getAdapter(UniformResourceStorage.class);
					if (storage != null)
					{
						URI uri = storage.getURI();
						if ("dbgsource".equals(uri.getScheme())) { //$NON-NLS-1$
							closeEditors.add(ref);
						}
					}
				}
				catch (PartInitException e)
				{
					IdeLog.logError(getDefault(), e);
				}
			}
			if (!closeEditors.isEmpty())
			{
				page.closeEditors((IEditorReference[]) closeEditors.toArray(new IEditorReference[closeEditors.size()]),
						false);
			}
		}
	}
}

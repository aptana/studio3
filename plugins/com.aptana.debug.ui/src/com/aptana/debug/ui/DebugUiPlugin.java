/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.ui;

import java.net.URI;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.osgi.framework.BundleContext;

import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.debug.internal.ui.DebugUIImages;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugUiPlugin extends AbstractUIPlugin
{
	/**
	 * ID
	 */
	public static final String PLUGIN_ID = "com.aptana.debug.ui"; //$NON-NLS-1$

	// The shared instance.
	private static DebugUiPlugin plugin;

	private IDebugModelPresentation fUtilPresentation;
	private IDebugEventSetListener debugEventListener;

	/**
	 * The constructor.
	 */
	public DebugUiPlugin()
	{
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		if (debugEventListener == null)
		{
			debugEventListener = new IDebugEventSetListener() {
				public void handleDebugEvents(DebugEvent[] events)
				{
					for (int i = 0; i < events.length; ++i)
					{
						DebugEvent event = events[i];
						if ((event.getSource() instanceof IJSDebugTarget)
								&& (event.getKind() == DebugEvent.TERMINATE))
						{
							WorkbenchJob job = new WorkbenchJob("Close Temporary Debug Editors") { //$NON-NLS-1$
								public IStatus runInUIThread(IProgressMonitor monitor)
								{
									closeDebugEditors();
									return Status.OK_STATUS;
								}
								
							};
							job.setPriority(Job.INTERACTIVE);
							job.setSystem(true);
							job.schedule();
						}
					}
				}
			};
		}
		DebugPlugin.getDefault().addDebugEventListener(debugEventListener);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception
	{
		DebugPlugin.getDefault().removeDebugEventListener(debugEventListener);
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return DebugUiPlugin
	 */
	public static DebugUiPlugin getDefault()
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

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg) {
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Convenience method which returns the unique identifier of this plugin.
	 * 
	 * @return String
	 */
	public static String getUniqueIdentifier()
	{
		return PLUGIN_ID;
	}

	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null)
		{
			return window.getShell();
		}
		return null;
	}

	/**
	 * getActivePage
	 * 
	 * @return IWorkbenchPage
	 */
	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null)
		{
			return w.getActivePage();
		}
		return null;
	}

	/**
	 * Returns the standard display to be used. The method first checks, if the thread calling this method has an
	 * associated display. If so, this display is returned. Otherwise the method returns the default display.
	 * 
	 * @return Display
	 */
	public static Display getStandardDisplay()
	{
		Display display;
		display = Display.getCurrent();
		if (display == null)
		{
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Utility method with conventions
	 * 
	 * @param message
	 * @param t
	 */
	public static void errorDialog(String message, Throwable t)
	{
		log(t);
		Shell shell = getActiveWorkbenchShell();
		if (shell != null)
		{
			IStatus status = new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR,
					"Error logged from Aptana Debug UI: ", t); //$NON-NLS-1$	
			ErrorDialog.openError(shell, "Error", message, status); //$NON-NLS-1$
		}
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
		IWorkbenchPage page = getActivePage();
		if(page != null)
		{
			IEditorReference[] editorRefs = page.getEditorReferences();
			ArrayList<IEditorReference> closeEditors = new ArrayList<IEditorReference>();
			for (int i = 0; i < editorRefs.length; ++i)
			{
				try
				{
					IEditorInput input = editorRefs[i].getEditorInput();
					UniformResourceStorage storage = (UniformResourceStorage) input.getAdapter(UniformResourceStorage.class);
					if (storage != null)
					{
						URI uri = storage.getURI();
						if ("dbgsource".equals(uri.getScheme())) //$NON-NLS-1$
						{
							closeEditors.add(editorRefs[i]);
						}
					}
				}
				catch (PartInitException e)
				{
					log(e.getStatus());
				}
			}
			if (!closeEditors.isEmpty())
			{
				page.closeEditors((IEditorReference[]) closeEditors.toArray(new IEditorReference[closeEditors.size()]), false);
			}
		}
		
	}
}

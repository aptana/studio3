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
package com.aptana.editor.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.editor.common.internal.scripting.ContentTypeTranslation;
import com.aptana.editor.common.internal.scripting.DocumentScopeManager;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.index.core.IndexActivator;
import com.aptana.usage.EventLogger;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonEditorPlugin extends AbstractUIPlugin
{

	public static final String SNIPPET = "/icons/snippet.png"; //$NON-NLS-1$
	public static final String COMMAND = "/icons/command.png"; //$NON-NLS-1$
	public static final String IBEAM_BLACK = "/icons/ibeam-black.gif"; //$NON-NLS-1$
	public static final String IBEAM_WHITE = "/icons/ibeam-white.gif"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.common"; //$NON-NLS-1$

	private static final String TEMPLATES = PLUGIN_ID + ".templates"; //$NON-NLS-1$

	private static final String OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.aptana.editor.common.commands.toggleOutline"; //$NON-NLS-1$
	private static final String COMMAND_STATE = "org.eclipse.ui.commands.toggleState"; //$NON-NLS-1$

	// The shared instance
	private static CommonEditorPlugin plugin;

	private Map<ContextTypeRegistry, ContributionTemplateStore> fTemplateStoreMap;
	private FilenameDifferentiator differentiator;

	private final IPartListener fPartListener = new IPartListener()
	{

		public void partActivated(IWorkbenchPart part)
		{
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editorPart = (IEditorPart) part;
				final String id = editorPart.getEditorSite().getId();
				Job job = new Job("Recording editor close") //$NON-NLS-1$
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						EventLogger.getInstance().logEvent("editor.closed", id); //$NON-NLS-1$
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
				job.setPriority(Job.SHORT);
			}
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editorPart = (IEditorPart) part;
				final String id = editorPart.getEditorSite().getId();
				Job job = new Job("Recording editor open") //$NON-NLS-1$
				{
					@Override
					protected IStatus run(IProgressMonitor monitor)
					{
						EventLogger.getInstance().logEvent("editor.opened", id); //$NON-NLS-1$
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
				job.setPriority(Job.SHORT);
			}
		}
	};

	private final IPerspectiveListener fPerspectiveListener = new IPerspectiveListener()
	{

		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
		{
			setCommandState(findView(page, OUTLINE_VIEW_ID) != null);
		}

		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
		{
			if (changeId.equals(IWorkbenchPage.CHANGE_VIEW_HIDE))
			{
				if (findView(page, OUTLINE_VIEW_ID) == null)
				{
					setCommandState(false);
				}
			}
			else if (changeId.equals(IWorkbenchPage.CHANGE_VIEW_SHOW))
			{
				if (findView(page, OUTLINE_VIEW_ID) != null)
				{
					setCommandState(true);
				}
			}
		}

		private void setCommandState(boolean state)
		{
			ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
			Command command = service.getCommand(COMMAND_ID);
			State commandState = command.getState(COMMAND_STATE);
			if (((Boolean) commandState.getValue()) != state)
			{
				commandState.setValue(state);
				service.refreshElements(COMMAND_ID, null);
			}
		}

		protected IViewReference findView(IWorkbenchPage page, String viewId)
		{
			IViewReference refs[] = page.getViewReferences();
			for (int i = 0; i < refs.length; i++)
			{
				IViewReference ref = refs[i];
				if (viewId.equals(ref.getId()))
				{
					return ref;
				}
			}
			return null;
		}
	};

	private final IWindowListener fWindowListener = new IWindowListener()
	{

		public void windowActivated(IWorkbenchWindow window)
		{
		}

		public void windowClosed(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.removePartListener(fPartListener);
			}
			window.removePerspectiveListener(fPerspectiveListener);
		}

		public void windowDeactivated(IWorkbenchWindow window)
		{
		}

		public void windowOpened(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.addPartListener(fPartListener);
			}
			window.addPerspectiveListener(fPerspectiveListener);
		}
	};

	/**
	 * The constructor
	 */
	public CommonEditorPlugin()
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

		// Activate indexing
		IndexActivator.getDefault();

		differentiator = new FilenameDifferentiator();
		differentiator.schedule();

		addPartListener();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			differentiator.dispose();

			removePartListener();
		}
		finally
		{
			differentiator = null;
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CommonEditorPlugin getDefault()
	{
		return plugin;
	}

	public static void logError(Exception e)
	{
		if (e instanceof CoreException)
			logError((CoreException) e);
		else
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	public static void trace(String string)
	{
		if (getDefault() != null && getDefault().isDebugging())
			getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, string));
	}

	public static void logError(String string, Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, string, e));
	}

	public static void logWarning(String message)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, null));
	}

	public static void logInfo(String message)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message, null));
	}

	public static Image getImage(String path)
	{
		ImageRegistry registry = plugin.getImageRegistry();
		Image image = registry.get(path);
		if (image == null)
		{
			ImageDescriptor id = getImageDescriptor(path);
			if (id == null)
			{
				return null;
			}
			registry.put(path, id);
			image = registry.get(path);
		}
		return image;
	}

	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		reg.put(SNIPPET, imageDescriptorFromPlugin(PLUGIN_ID, SNIPPET));
		reg.put(COMMAND, imageDescriptorFromPlugin(PLUGIN_ID, COMMAND));
		reg.put(IBEAM_BLACK, imageDescriptorFromPlugin(PLUGIN_ID, IBEAM_BLACK));
		reg.put(IBEAM_WHITE, imageDescriptorFromPlugin(PLUGIN_ID, IBEAM_WHITE));
	}

	public Image getImageFromImageRegistry(String imageID)
	{
		return getImageRegistry().get(imageID);
	}

	public ContributionTemplateStore getTemplateStore(ContextTypeRegistry contextTypeRegistry)
	{
		if (fTemplateStoreMap == null)
		{
			fTemplateStoreMap = new HashMap<ContextTypeRegistry, ContributionTemplateStore>();
		}
		ContributionTemplateStore store = fTemplateStoreMap.get(contextTypeRegistry);
		if (store == null)
		{
			store = new ContributionTemplateStore(contextTypeRegistry, getPreferenceStore(), TEMPLATES);
			try
			{
				store.load();
				fTemplateStoreMap.put(contextTypeRegistry, store);
			}
			catch (IOException e)
			{
				logError(e.getMessage(), e);
			}
		}
		return store;
	}

	public IDocumentScopeManager getDocumentScopeManager()
	{
		return DocumentScopeManager.getInstance();
	}

	public IContentTypeTranslator getContentTypeTranslator()
	{
		return ContentTypeTranslation.getDefault();
	}

	private void addPartListener()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
		if (workbench != null)
		{
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			IPartService partService;
			for (IWorkbenchWindow window : windows)
			{
				partService = window.getPartService();
				if (partService != null)
				{
					partService.addPartListener(fPartListener);
				}
				window.addPerspectiveListener(fPerspectiveListener);
			}

			// Listen on any future windows
			PlatformUI.getWorkbench().addWindowListener(fWindowListener);
		}
	}

	private void removePartListener()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
		if (workbench != null)
		{
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			IPartService partService;
			for (IWorkbenchWindow window : windows)
			{
				partService = window.getPartService();
				if (partService != null)
				{
					partService.removePartListener(fPartListener);
				}
				window.removePerspectiveListener(fPerspectiveListener);
			}
			PlatformUI.getWorkbench().removeWindowListener(fWindowListener);
		}
	}
}

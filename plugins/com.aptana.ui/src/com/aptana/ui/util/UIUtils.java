/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.util;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.help.ui.internal.views.HelpView;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.ui.UIPlugin;
import com.aptana.ui.dialogs.GenericInfoPopupDialog;
import com.aptana.ui.internal.WebPerspectiveFactory;

/**
 * @author Max Stepanov
 * @author cwilliams
 */
@SuppressWarnings("restriction")
public final class UIUtils
{

	private static final String HELP_VIEW_ID = "org.eclipse.help.ui.HelpView"; //$NON-NLS-1$

	/**
	 * By default, show tooltips for 3 seconds.
	 */
	public static final int DEFAULT_TOOLTIP_TIME = 3000;

	private static final String INTERNAL_HELP_BROWSER_ID = "internal_help_browser"; //$NON-NLS-1$

	/**
	 * 
	 */
	private UIUtils()
	{
	}

	/**
	 * Gets the display for the workbench
	 * 
	 * @return the display
	 */
	public static Display getDisplay()
	{
		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * Gets the active shell for the workbench
	 * 
	 * @return the active shell
	 */
	public static Shell getActiveShell()
	{
		Shell shell = getDisplay().getActiveShell();
		if (shell == null)
		{
			IWorkbenchWindow window = getActiveWorkbenchWindow();
			if (window != null)
			{
				shell = window.getShell();
			}
		}
		return shell;
	}

	/**
	 * Returns the editor part representing the current active editor.
	 * 
	 * @return the active editor
	 */
	public static IEditorPart getActiveEditor()
	{
		IWorkbenchPage workbenchPage = getActivePage();
		if (workbenchPage == null)
		{
			return null;
		}
		return workbenchPage.getActiveEditor();
	}

	/**
	 * Returns the active part in the current workbench window.
	 * 
	 * @return the active part
	 */
	public static IWorkbenchPart getActivePart()
	{
		IWorkbenchPage workbenchPage = getActivePage();
		if (workbenchPage == null)
		{
			return null;
		}
		return workbenchPage.getActivePart();
	}

	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow workbench = getActiveWorkbenchWindow();
		if (workbench == null)
		{
			return null;
		}
		return workbench.getActivePage();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		try
		{
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		}
		catch (IllegalStateException e)
		{
			// Workbench has not been created yet
			return null;
		}
	}

	/**
	 * Returns the active perspective id if there is one
	 * 
	 * @return the active perspective id; <code>null</code> in case it could not be resolved.
	 */
	public static String getActivePerspectiveId()
	{
		IPerspectiveDescriptor perspective = getActivePerspectiveDescriptor();
		if (perspective != null)
		{
			return perspective.getId();
		}
		return null;
	}

	/**
	 * Returns the active perspective descriptor if there is one.
	 * 
	 * @return the active perspective descriptor; <code>null</code> in case it could not be resolved.
	 */
	public static IPerspectiveDescriptor getActivePerspectiveDescriptor()
	{
		IWorkbenchPage activePage = getActivePage();
		if (activePage != null)
		{
			return activePage.getPerspective();
		}
		return null;
	}

	public static IEditorPart[] getDirtyEditors()
	{
		IWorkbenchPage page = UIUtils.getActivePage();
		if (page == null)
		{
			return null;
		}
		return page.getDirtyEditors();
	}

	/**
	 * Returns the URI for the specific editor input.
	 * 
	 * @param input
	 *            the editor input
	 * @return the URI, or null if none could be determined
	 */
	public static URI getURI(IEditorInput input)
	{
		if (input instanceof IFileEditorInput)
		{
			return ((IFileEditorInput) input).getFile().getLocationURI();
		}
		if (input instanceof IURIEditorInput)
		{
			return ((IURIEditorInput) input).getURI();
		}
		if (input instanceof IPathEditorInput)
		{
			return URIUtil.toURI(((IPathEditorInput) input).getPath());
		}
		return null;
	}

	public static IResource getSelectedResource()
	{
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench().getService(
				IEvaluationService.class);
		if (evaluationService != null)
		{
			return getSelectedResource(evaluationService.getCurrentState());
		}
		return null;
	}

	public static IProject getSelectedProjectInExplorer()
	{
		IProject selectedProject = getSelectedProject(WebPerspectiveFactory.APP_EXPLORER_ID);
		if (selectedProject == null)
		{
			selectedProject = getSelectedProject(IPageLayout.ID_PROJECT_EXPLORER);
		}
		return selectedProject;
	}

	private static IProject getSelectedProject(String viewID)
	{
		ISelectionService service = UIUtils.getActiveWorkbenchWindow().getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service.getSelection(viewID);
		if (structured instanceof IStructuredSelection)
		{
			Object selectedObject = ((IStructuredSelection) structured).getFirstElement();
			if (selectedObject instanceof IAdaptable)
			{
				IResource resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
				if (resource != null)
				{
					return resource.getProject();
				}
			}
		}
		return null;
	}

	public static IResource getSelectedResource(IEvaluationContext evaluationContext)
	{
		if (evaluationContext == null)
		{
			return null;
		}

		Object variable = evaluationContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
		if (variable instanceof IStructuredSelection)
		{
			Object selectedObject = ((IStructuredSelection) variable).getFirstElement();
			if (selectedObject instanceof IAdaptable)
			{
				IResource resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
				if (resource != null)
				{
					return resource;
				}
			}
		}
		else
		{
			// checks the active editor
			variable = evaluationContext.getVariable(ISources.ACTIVE_EDITOR_NAME);
			if (variable instanceof IEditorPart)
			{
				IEditorInput editorInput = ((IEditorPart) variable).getEditorInput();
				if (editorInput instanceof IFileEditorInput)
				{
					return ((IFileEditorInput) editorInput).getFile();
				}
			}
		}
		return null;
	}

	public static IProject getSelectedProject()
	{
		IProject project = null;
		IResource selectedResource = getSelectedResource();
		if (selectedResource != null)
		{
			project = selectedResource.getProject();
		}

		return project;
	}

	/**
	 * Finds a view with the given ID
	 * 
	 * @param viewID
	 *            the view ID
	 * @return the view part
	 * @throws PartInitException
	 */
	public static IViewPart findView(String viewID)
	{
		IWorkbenchPage page = getActivePage();
		if (page != null)
		{
			return page.findView(viewID);
		}
		return null;
	}

	/**
	 * Shows the view specified
	 * 
	 * @param viewID
	 * @return whether the view was shown
	 */
	public static boolean showView(String viewID)
	{
		return showView(viewID, IWorkbenchPage.VIEW_ACTIVATE);
	}

	/**
	 * Shows the view specified
	 * 
	 * @param viewID
	 * @return whether the view was shown
	 */
	public static boolean showView(String viewID, int mode)
	{
		IWorkbenchPage activePage = getActivePage();
		if (activePage != null)
		{
			try
			{
				return activePage.showView(viewID, null, mode) != null;
			}
			catch (PartInitException e)
			{
				return false;
			}
		}

		return false;
	}

	public static void showErrorMessage(String title, String message)
	{
		showErrorMessage((title != null) ? title : Messages.UIUtils_Error, message, null);
	}

	public static void showErrorMessage(String message, Throwable exception)
	{
		showErrorMessage(Messages.UIUtils_Error, message, exception);
	}

	private static void showErrorMessage(final String title, final String message, final Throwable exception)
	{
		if (Display.getCurrent() == null || exception != null)
		{
			UIJob job = new UIJob(message)
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (exception == null)
					{
						showErrorDialog(title, message);
						return Status.OK_STATUS;
					}
					return new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, null, exception);
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.setUser(true);
			job.schedule();
		}
		else
		{
			showErrorDialog(title, message);
		}
	}

	/**
	 * Schedules a message dialog to be displayed safely in the UI thread
	 * 
	 * @param kind
	 *            MessageDialog constants indicating the kind
	 * @param title
	 *            MessageDialog title
	 * @param message
	 *            MessageDialog message
	 * @param runnable
	 *            Something that gets run if the message dialog return code is Window.OK
	 */
	public static void showMessageDialogFromBgThread(final int kind, final String title, final String message,
			final ISafeRunnable runnable)
	{
		showMessageDialogFromBgThread(new SafeMessageDialogRunnable()
		{

			public void run() throws Exception
			{
				if (runnable != null)
				{
					runnable.run();
				}
			}

			public int openMessageDialog()
			{
				if (kind == MessageDialog.INFORMATION)
				{
					// for info messages, we show the toast popup
					IWorkbenchWindow window = UIUtils.getActiveWorkbenchWindow();
					if (window == null)
					{
						return Window.CANCEL;
					}
					GenericInfoPopupDialog dialog = new GenericInfoPopupDialog(window.getShell(), title, message);
					return dialog.open();
				}
				return MessageDialog.open(kind, UIUtils.getActiveWorkbenchWindow().getShell(), title, message, SWT.NONE) ? Window.OK
						: Window.CANCEL;
			}
		}, Window.OK);
	}

	/**
	 * Schedules a message dialog to be displayed safely in the UI thread
	 * 
	 * @param runnable
	 *            Something that gets run if the message dialog return code is Window.OK
	 */
	public static void showMessageDialogFromBgThread(final SafeMessageDialogRunnable runnable)
	{
		showMessageDialogFromBgThread(runnable, Window.OK);
	}

	/**
	 * Schedules a message dialog to be displayed safely in the UI thread
	 * 
	 * @param runnable
	 *            Something that gets run if the message dialog return code is Window.OK
	 * @param runnableCondition
	 *            The return code from SafeMessageDialogRunnable.openMessageDialog() that would trigger
	 *            SafeMessageDialogRunnable.run()
	 */
	public static void showMessageDialogFromBgThread(final SafeMessageDialogRunnable runnable,
			final int runnableCondition)
	{
		UIJob job = new UIJob("Modal Message Dialog Job") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// If the system dialog is shown, then the active shell would be null
				if (Display.getDefault().getActiveShell() == null)
				{
					if (!monitor.isCanceled())
					{
						schedule(1000);
					}
				}
				else if (!monitor.isCanceled())
				{
					if (runnable.openMessageDialog() == runnableCondition)
					{
						try
						{
							runnable.run();
						}
						catch (Exception e)
						{
							IdeLog.logError(UIPlugin.getDefault(), e);
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	private static void showErrorDialog(String title, String message)
	{
		MessageDialog.openError(getActiveWorkbenchWindow().getShell(), title, message);
	}

	public static boolean showPromptDialog(final String title, final String message)
	{
		if (Display.getCurrent() == null)
		{
			UIJob job = new UIJob(title)
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (showPromptDialogUI(title, message))
					{
						return Status.OK_STATUS;
					}
					return Status.CANCEL_STATUS;
				}
			};
			job.setPriority(Job.INTERACTIVE);
			job.setUser(true);
			job.schedule();
			try
			{
				job.join();
			}
			catch (InterruptedException e)
			{
			}
			return job.getResult() == Status.OK_STATUS;
		}
		else
		{
			return showPromptDialogUI(title, message);
		}
	}

	private static boolean showPromptDialogUI(String title, String message)
	{
		return MessageDialog.openQuestion(getActiveWorkbenchWindow().getShell(), title, message);
	}

	public static Image getImage(AbstractUIPlugin plugin, String path)
	{
		ImageRegistry registry = plugin.getImageRegistry();
		Image image = registry.get(path);
		if (image == null)
		{
			ImageDescriptor id = getImageDescriptor(plugin.getBundle().getSymbolicName(), path);
			if (id == null)
			{
				return null;
			}
			registry.put(path, id);
			image = registry.get(path);
		}
		return image;
	}

	public static ImageDescriptor getImageDescriptor(String pluginId, String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, path);
	}

	public static boolean getCoolBarVisibility()
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null)
		{
			IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
			if (activeWorkbenchWindow instanceof WorkbenchWindow)
			{
				return ((WorkbenchWindow) activeWorkbenchWindow).getCoolBarVisible();
			}
		}
		return true;
	}

	public static void setCoolBarVisibility(boolean visible)
	{
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow instanceof WorkbenchWindow)
		{
			WorkbenchWindow workbenchWindow = (WorkbenchWindow) activeWorkbenchWindow;
			workbenchWindow.setCoolBarVisible(visible);
			workbenchWindow.setPerspectiveBarVisible(visible);

			// Try to force a refresh of the text on the action
			IWorkbenchPart activePart = getActivePart();
			if (activePart != null)
			{
				ICommandService cmdService = (ICommandService) activePart.getSite().getService(ICommandService.class);
				cmdService.refreshElements("org.eclipse.ui.ToggleCoolbarAction", null); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Opens the internal HelpView and address it to the given doc url.
	 * 
	 * @param url
	 */
	public static void openHelp(String url)
	{
		IWorkbenchPage page = getActivePage();
		if (page != null)
		{
			try
			{
				IViewPart part = page.showView(HELP_VIEW_ID);
				if (part != null)
				{
					HelpView view = (HelpView) part;
					view.showHelp(url);
				}
			}
			catch (PartInitException e)
			{
				IdeLog.logError(UIPlugin.getDefault(), e);
			}
		}
		else
		{
			IdeLog.logWarning(UIPlugin.getDefault(), "Could not open the help view. Active page was null."); //$NON-NLS-1$
		}
	}

	/**
	 * Opens the internal help in the Studio's internal browser.
	 * 
	 * @param url
	 * @return A boolean value indicating a successful operations or not.
	 */
	public static boolean openHelpInBrowser(String url)
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null)
		{
			IWorkbenchHelpSystem helpSystem = workbench.getHelpSystem();
			URL resolvedURL = helpSystem.resolve(url, true);
			if (resolvedURL != null)
			{
				return openInBroswer(resolvedURL, true, IWorkbenchBrowserSupport.AS_EDITOR
						| IWorkbenchBrowserSupport.STATUS);
			}
			else
			{
				IdeLog.logError(UIPlugin.getDefault(), "Unable to resolve the Help URL for " + url); //$NON-NLS-1$
				return false;
			}
		}
		return false;
	}

	/**
	 * Open a URL in a browser.
	 * 
	 * @param url
	 * @param internal
	 *            In case true, the system will try to open the internal browser if it's available.
	 * @param style
	 *            the Browser's style, in case an internal browser is requested.
	 * @return A boolean value indicating a successful operations or not.
	 */
	public static boolean openInBroswer(URL url, boolean internal, int style)
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null)
		{
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
			try
			{
				if (internal && support.isInternalWebBrowserAvailable())
				{

					support.createBrowser(style, INTERNAL_HELP_BROWSER_ID, null, null).openURL(url);

				}
				else
				{
					support.getExternalBrowser().openURL(url);
				}
			}
			catch (PartInitException e)
			{
				IdeLog.logError(UIPlugin.getDefault(), "Error opening the help", e); //$NON-NLS-1$
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Runs the passed runnable in the UI thread. If the current thread is an UI-thread, runs it now, otherwise it's run
	 * asynchronously in the UI thread.
	 */
	public static void runInUIThread(Runnable runnable)
	{
		Display display = Display.getCurrent();
		if (display == null)
		{
			Display.getDefault().asyncExec(runnable);
		}
		else
		{
			runnable.run();
		}
	}

	/**
	 * Based on the given available width to display the text, it strips of the middle section of the text and ellipsify
	 * it.
	 * 
	 * @param text
	 * @param width
	 * @return
	 */
	public static String shortenText(String text, int width)
	{
		GC gc = new GC(UIUtils.getDisplay());
		// chop string in half and drop a few characters
		int middle = text.length() / 2;
		String beginning = text.substring(0, middle - 1);
		String end = text.substring(middle + 2, text.length());
		// Now repeatedly chop off one char from each end until we fit
		// TODO Chop each side separately? it'd take more loops, but text would fit tighter when uneven
		// lengths work better..
		while (gc.stringExtent(beginning + "..." + end).x > width) //$NON-NLS-1$
		{
			if (beginning.length() > 0)
			{
				beginning = beginning.substring(0, beginning.length() - 1);
			}
			else
			{
				break;
			}
			if (end.length() > 0)
			{
				end = end.substring(1);
			}
			else
			{
				break;
			}
		}
		text = beginning + "..." + end; //$NON-NLS-1$
		return text;
	}

	/**
	 * Throws an AssertionError if the current thread is not the UI thread.
	 */
	public static void assertUIThread()
	{
		Assert.isTrue(Display.getCurrent() != null, "Function must be called from UI-thread."); //$NON-NLS-1$
	}

	/**
	 * Prompts to restart studio and if user accepts, then Studio is restarted immediately.
	 * 
	 * @param title
	 * @param message
	 */
	public static void restartStudio(final String title, final String message)
	{
		showMessageDialogFromBgThread(new SafeMessageDialogRunnable()
		{
			@Override
			public int openMessageDialog()
			{
				boolean restart = MessageDialog.openQuestion(UIUtils.getActiveShell(), title, message);
				if (restart)
				{
					PlatformUI.getWorkbench().restart();
				}
				return 0;
			}
		});
	}

	/**
	 * Prompts to shutdown studio and if user accepts, then Studio is shutdown immediately.
	 * 
	 * @param title
	 * @param message
	 * @param productId
	 */
	public static void closeStudio(String title, String message, String productId)
	{
		MessageDialog.openError(UIUtils.getActiveShell(), title, message);
		// not to use EclipseUtil.isStandalone() directly since it checks the existance of Aptana Studio RCP instead
		// TODO fix EclipseUtil.isStandalone() to maybe read from a preference key on what plugin id to check.
		if (EclipseUtil.getPluginVersion(productId) != null && !EclipseUtil.isTesting())
		{
			PlatformUI.getWorkbench().close();
		}
	}
}

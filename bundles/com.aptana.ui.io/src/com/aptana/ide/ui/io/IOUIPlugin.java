/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor

package com.aptana.ide.ui.io;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenResourceAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.io.efs.SyncUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;
import com.aptana.ide.ui.io.internal.UniformFileStoreEditorInput;
import com.aptana.ide.ui.io.navigator.FileSystemElementComparer;
import com.aptana.ide.ui.io.navigator.RemoteNavigatorView;
import com.aptana.ide.ui.io.navigator.actions.Messages;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class IOUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.ui.io"; //$NON-NLS-1$

	// The shared instance
	private static IOUIPlugin plugin;

	private Map<IEditorInput, Job> saveRemoteJobs;

	private IConnectionPointListener connectionListener;

	private IPreferenceChangeListener themeChangeListener;

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
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof ProjectExplorer)
			{
				handleProjectExplorerListeners(part);
			}
			else if (part instanceof IEditorPart)
			{
				attachSaveListener((IEditorPart) part);
			}
		}
	};

	private void handleProjectExplorerListeners(final IWorkbenchPart part)
	{
		CommonViewer viewer = ((ProjectExplorer) part).getCommonViewer();
		viewer.setComparer(new FileSystemElementComparer());
		final Tree tree = viewer.getTree();
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object element = selection.getFirstElement();

				if (selection.size() == 1 && (element instanceof IResource)
						&& ((IResource) element).getType() == IResource.PROJECT)
				{
					OpenResourceAction openResourceAction = new OpenResourceAction(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
					openResourceAction.selectionChanged((IStructuredSelection) event.getViewer().getSelection());
					if (openResourceAction.isEnabled())
					{
						openResourceAction.run();
					}
				}

			}
		});
		tree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseDown(MouseEvent e)
			{
				if (tree.getItem(new Point(e.x, e.y)) == null)
				{
					tree.deselectAll();
					tree.notifyListeners(SWT.Selection, new Event());
				}
			}
		});
	}

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
		}
	};

	/**
	 * The constructor
	 */
	public IOUIPlugin()
	{
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		Job job = new Job(MessageFormat.format("Initializing {0} Plugin", PLUGIN_ID)) //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				connectionListener = new IConnectionPointListener()
				{

					public void connectionPointChanged(ConnectionPointEvent event)
					{
						IConnectionPoint connection = event.getConnectionPoint();
						IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
						ConnectionPointType type = manager.getType(connection);
						if (type == null)
						{
							return;
						}

						switch (event.getKind())
						{
							case ConnectionPointEvent.POST_ADD:
								refreshNavigatorViewAndSelect(
										manager.getConnectionPointCategory(type.getCategory().getId()), connection);
								break;
							case ConnectionPointEvent.POST_DELETE:
								refreshNavigatorView(manager.getConnectionPointCategory(type.getCategory().getId()));
								break;
							case ConnectionPointEvent.POST_CHANGE:
								refreshNavigatorView(connection);
						}
					}

				};
				CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(connectionListener);

				themeChangeListener = new IPreferenceChangeListener()
				{
					public void preferenceChange(PreferenceChangeEvent event)
					{
						if (event.getKey().equals(IThemeManager.THEME_CHANGED))
						{
							ImageUtils.themeChanged();
						}
					}
				};
				InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(themeChangeListener);

				saveRemoteJobs = new HashMap<IEditorInput, Job>();
				addPartListener();

				return Status.OK_STATUS;
			}

		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		if (connectionListener != null)
		{
			CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(connectionListener);
			connectionListener = null;
		}
		if (themeChangeListener != null)
		{
			InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(themeChangeListener);
			themeChangeListener = null;
		}
		removePartListener();
		if (saveRemoteJobs != null)
		{
			Collection<Job> jobs = saveRemoteJobs.values();
			for (Job job : jobs)
			{
				job.cancel();
			}
			saveRemoteJobs.clear();
			saveRemoteJobs = null;
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IOUIPlugin getDefault()
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
	 * Returns an image for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image object
	 */
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

	public static void refreshNavigatorView(Object element)
	{
		refreshNavigatorViewAndSelect(element, null);
	}

	public static void refreshNavigatorViewAndSelect(final Object element, final Object selection)
	{
		UIUtils.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					IViewPart view = findView(IPageLayout.ID_PROJECT_EXPLORER);
					refreshNavigatorInternal(view, element, selection);

					view = findView(RemoteNavigatorView.ID);
					if (view != null)
					{
						// if the content of the remote category changed, refresh the root of Remote view
						if (element instanceof IConnectionPointCategory
								&& ((IConnectionPointCategory) element).isRemote())
						{
							Object input = ((CommonNavigator) view).getCommonViewer().getInput();
							refreshNavigatorInternal(view, input, selection);
						}
						else
						{
							refreshNavigatorInternal(view, element, selection);
						}
					}
				}
				catch (PartInitException e)
				{
				}
			}
		});
	}

	private static void refreshNavigatorInternal(IViewPart viewPart, Object element, Object selection)
	{
		if (viewPart == null)
		{
			return;
		}
		if (viewPart instanceof CommonNavigator)
		{
			CommonViewer viewer = ((CommonNavigator) viewPart).getCommonViewer();
			if (element == null)
			{
				// full refresh
				System.err.println("FIXME: full refresh for " + viewer.getClass().getSimpleName()); //$NON-NLS-1$ // $codepro.audit.disable debuggingCode
				viewer.refresh();
			}
			else
			{
				Widget widget = viewer.testFindItem(element);
				if (widget != null)
				{
					Object data = widget.getData();
					if (data != null)
					{
						viewer.refresh(data);
					}
				}
			}
		}

		if (selection != null && viewPart instanceof CommonNavigator)
		{
			// ensures the category's new content are loaded
			CommonViewer viewer = ((CommonNavigator) viewPart).getCommonViewer();
			viewer.expandToLevel(element, 1);
			viewer.setSelection(new StructuredSelection(selection));
		}
	}

	private static IViewPart findView(String viewID) throws PartInitException
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				return page.findView(viewID);
			}
		}
		return null;
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
				final IWorkbenchWindow workbenchWindow = window;
				UIUtils.getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						IViewPart projectExplorer = null;
						if ((projectExplorer = workbenchWindow.getActivePage()
								.findView(IPageLayout.ID_PROJECT_EXPLORER)) != null)
						{
							// If the project explorer view is already opened at this time, then
							// we need to explicitly add the listener to it.
							handleProjectExplorerListeners(projectExplorer);
						}
					}
				});
				partService = window.getPartService();
				if (partService != null)
				{
					partService.addPartListener(fPartListener);
				}
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
			}
			PlatformUI.getWorkbench().removeWindowListener(fWindowListener);
		}
	}

	/**
	 * Watches the local file for changes and saves it back to the original remote file when the editor is saved.
	 * 
	 * @param editorPart
	 *            the editor part the file is opened on
	 */
	private void attachSaveListener(final IEditorPart editorPart)
	{
		final IEditorInput editorInput = editorPart.getEditorInput();
		if (!(editorInput instanceof UniformFileStoreEditorInput)
				|| !((UniformFileStoreEditorInput) editorInput).isRemote())
		{
			// the original is a local file; no need to re-save it
			return;
		}

		editorPart.addPropertyListener(new IPropertyListener()
		{

			public void propertyChanged(Object source, int propId)
			{
				if (propId == EditorPart.PROP_DIRTY && source instanceof EditorPart)
				{
					EditorPart ed = (EditorPart) source;
					if (ed.isDirty())
					{
						return;
					}

					Job job = saveRemoteJobs.get(editorInput);
					if (job != null)
					{
						// a job saving the remote file is already running
						return;
					}
					job = new Job(Messages.EditorUtils_MSG_RemotelySaving + ed.getPartName())
					{

						protected IStatus run(final IProgressMonitor monitor)
						{
							UniformFileStoreEditorInput input = (UniformFileStoreEditorInput) editorInput;
							final IFileStore localCacheFile = input.getLocalFileStore();
							IFileStore originalFile = input.getFileStore();
							IFileInfo originalFileInfo = input.getFileInfo();
							try
							{
								IFileInfo currentFileInfo = originalFile.fetchInfo(EFS.NONE, monitor);
								if (currentFileInfo.exists()
										&& (currentFileInfo.getLastModified() != originalFileInfo.getLastModified() || currentFileInfo
												.getLength() != originalFileInfo.getLength()))
								{
									if (!UIUtils.showPromptDialog(Messages.EditorUtils_OverwritePrompt_Title,
											MessageFormat.format(Messages.EditorUtils_OverwritePrompt_Message,
													originalFile.getName())))
									{
										return Status.CANCEL_STATUS;
									}
								}
								SyncUtils.copy(localCacheFile, null, originalFile, EFS.NONE, monitor);
							}
							catch (final CoreException e)
							{
								// save failed; offers user to save the file locally instead
								final String filename = originalFile.getName();
								UIUtils.getDisplay().asyncExec(new Runnable()
								{

									public void run()
									{
										if (MessageDialog.openConfirm(UIUtils.getActiveShell(), MessageFormat.format(
												Messages.IOUIPlugin_ErrorSavingRemoteFile_Title, filename),
												MessageFormat.format(Messages.IOUIPlugin_ErrorSavingRemoteFile_Message,
														e.getLocalizedMessage())))
										{
											FileDialog dialog = new FileDialog(UIUtils.getActiveShell(), SWT.SAVE);
											dialog.setFileName(filename);
											String filepath = dialog.open();
											if (filepath != null)
											{
												IFileStore localFileStore = EFS.getLocalFileSystem().fromLocalFile(
														new File(filepath));
												try
												{
													SyncUtils.copy(localCacheFile, null, localFileStore, EFS.NONE,
															monitor);
												}
												catch (CoreException e1)
												{
													UIUtils.showErrorMessage(MessageFormat.format(
															Messages.IOUIPlugin_ErrorSavingRemoteFile_Title, filename),
															e);
												}
											}
										}
									}
								});
							}
							finally
							{
								// update cached remote file info
								try
								{
									input.setFileInfo(originalFile.fetchInfo(EFS.NONE, monitor));
								}
								catch (CoreException e)
								{
									IdeLog.logWarning(IOUIPlugin.getDefault(), e);
								}
							}
							return Status.OK_STATUS;
						}
					};
					saveRemoteJobs.put(editorInput, job);
					job.addJobChangeListener(new JobChangeAdapter()
					{

						public void done(IJobChangeEvent event)
						{
							saveRemoteJobs.remove(editorInput);
						};
					});
					job.schedule();
				}
			}
		});
	}
}

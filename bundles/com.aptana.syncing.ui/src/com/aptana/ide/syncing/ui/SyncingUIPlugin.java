/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.core.io.events.ConnectionPointEvent;
import com.aptana.ide.core.io.events.IConnectionPointListener;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.core.events.ISiteConnectionListener;
import com.aptana.ide.syncing.core.events.SiteConnectionEvent;
import com.aptana.ide.syncing.ui.actions.DownloadAction;
import com.aptana.ide.syncing.ui.actions.Sync;
import com.aptana.ide.syncing.ui.editors.EditorUtils;
import com.aptana.ide.syncing.ui.navigator.ProjectSitesManager;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ide.syncing.ui.preferences.SyncPreferenceUtil;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.IUniformFileStoreEditorInput;
import com.aptana.ui.util.UIUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class SyncingUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.syncing.ui"; //$NON-NLS-1$

	// The shared instance
	private static SyncingUIPlugin plugin;

	private ISiteConnectionListener connectionListener = new ISiteConnectionListener()
	{

		public void siteConnectionChanged(SiteConnectionEvent event)
		{
			ISiteConnection siteConnection = event.getSiteConnection();
			switch (event.getKind())
			{
				case SiteConnectionEvent.POST_ADD:
					// opens the corresponding connection editor
					// EditorUtils.openConnectionEditor(siteConnection);
					break;
				case SiteConnectionEvent.POST_DELETE:
					// closes the corresponding connection editor
					EditorUtils.closeConnectionEditor(siteConnection);
					break;
			}

			refreshProjectSiteConnection(siteConnection);
		}
	};

	private IConnectionPointListener connectionPointListener = new IConnectionPointListener()
	{

		public void connectionPointChanged(ConnectionPointEvent event)
		{
			IConnectionPoint connectionPoint = event.getConnectionPoint();
			switch (event.getKind())
			{
				case ConnectionPointEvent.POST_ADD:
					ISiteConnection[] sites = SyncingPlugin.getSiteConnectionManager().getSiteConnections();
					IConnectionPoint source,
					destination;
					String id = connectionPoint.getId();
					for (ISiteConnection site : sites)
					{
						source = site.getSource();
						if (source != null && source.getId().equals(id))
						{
							// the source is changed to a new type
							site.setSource(connectionPoint);
						}
						destination = site.getDestination();
						if (destination != null && destination.getId().equals(id))
						{
							// the destination is changed to a new type
							site.setDestination(connectionPoint);
							refreshProjectSiteConnection(site);
						}
					}
					break;
				case ConnectionPointEvent.POST_DELETE:
					// check if any site connection has the deleted connection point as the destination
					ISiteConnection[] siteConnections = SyncingPlugin.getSiteConnectionManager().getSiteConnections();
					for (ISiteConnection siteConnection : siteConnections)
					{
						if (siteConnection.getDestination() == connectionPoint)
						{
							refreshProjectSiteConnection(siteConnection);
						}
					}
					break;
			}
		}
	};

	private static void refreshProjectSiteConnection(ISiteConnection siteConnection)
	{
		IConnectionPoint source = siteConnection.getSource();
		if (source != null)
		{
			IContainer container = (IContainer) source.getAdapter(IContainer.class);
			if (container != null)
			{
				IOUIPlugin.refreshNavigatorView(ProjectSitesManager.getInstance().getProjectSites(
						container.getProject()));
			}
		}
	}

	private IExecutionListener fExecutionListener = new IExecutionListener()
	{

		private static final String COMMAND_SAVE = "org.eclipse.ui.file.save"; //$NON-NLS-1$
		private static final String COMMAND_SAVE_ALL = "org.eclipse.ui.file.saveAll"; //$NON-NLS-1$

		private List<IFileEditorInput> editorInputsToUpload = new ArrayList<IFileEditorInput>();
		private List<IUniformFileStoreEditorInput> editorInputsToDownload = new ArrayList<IUniformFileStoreEditorInput>();

		public void notHandled(String commandId, NotHandledException exception)
		{
		}

		public void postExecuteFailure(String commandId, ExecutionException exception)
		{
		}

		public void postExecuteSuccess(String commandId, Object returnValue)
		{
			// if we see a save command
			if (COMMAND_SAVE.equals(commandId) || COMMAND_SAVE_ALL.equals(commandId))
			{
				for (IFileEditorInput input : editorInputsToUpload)
				{
					// for upload, checks if the file belongs to a project auto-synced to a remote connection
					IProject project = ((IFileEditorInput) input).getFile().getProject();
					if (SyncPreferenceUtil.isAutoSync(project)
							&& SyncPreferenceUtil.getAutoSyncDirection(project) != SyncDirection.DOWNLOAD)
					{
						Sync.uploadEditor(input);
					}
				}

				for (IUniformFileStoreEditorInput input : editorInputsToDownload)
				{
					// for download, finds all the projects that connect to the remote connection that contains the file
					// and checks if each has auto-sync turned on
					ISiteConnection[] sites = SiteConnectionUtils.findSitesWithDestination(input);
					for (ISiteConnection site : sites)
					{
						IConnectionPoint source = site.getSource();
						if (source instanceof WorkspaceConnectionPoint)
						{
							IProject project = ((WorkspaceConnectionPoint) source).getResource().getProject();
							if (SyncPreferenceUtil.isAutoSync(project)
									&& SyncPreferenceUtil.getAutoSyncDirection(project) != SyncDirection.UPLOAD)
							{
								DownloadAction action = new DownloadAction();
								action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage().getActivePart());
								action.setSelection(new StructuredSelection(input), false);
								action.run(null);
							}
						}
					}
				}
			}
		}

		public void preExecute(String commandId, ExecutionEvent event)
		{
			IEditorPart[] editorsToTransfer = null;
			editorInputsToUpload.clear();
			editorInputsToDownload.clear();

			if (COMMAND_SAVE.equals(commandId))
			{
				editorsToTransfer = new IEditorPart[] { UIUtils.getActiveEditor() };
			}
			else if (COMMAND_SAVE_ALL.equals(commandId))
			{
				editorsToTransfer = UIUtils.getDirtyEditors();
			}
			if (editorsToTransfer != null)
			{
				for (IEditorPart editor : editorsToTransfer)
				{
					IEditorInput input = editor.getEditorInput();
					if (input instanceof IFileEditorInput)
					{
						// it is a project file, so belongs to the upload list
						editorInputsToUpload.add((IFileEditorInput) input);
					}
					else if (input instanceof IUniformFileStoreEditorInput)
					{
						IUniformFileStoreEditorInput editorInput = (IUniformFileStoreEditorInput) input;
						if (editorInput.isRemote())
						{
							// it is a remote file, so belongs to the download list
							editorInputsToDownload.add(editorInput);
						}
					}
				}
			}
		}
	};

	/**
	 * The constructor
	 */
	public SyncingUIPlugin()
	{
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		SyncingPlugin.getSiteConnectionManager().addListener(connectionListener);
		CoreIOPlugin.getConnectionPointManager().addConnectionPointListener(connectionPointListener);
		addCommandSaveListener();
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		SyncingPlugin.getSiteConnectionManager().removeListener(connectionListener);
		CoreIOPlugin.getConnectionPointManager().removeConnectionPointListener(connectionPointListener);
		removeCommandSaveListener();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SyncingUIPlugin getDefault()
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

	private void addCommandSaveListener()
	{
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
		if (commandService != null)
		{
			commandService.addExecutionListener(fExecutionListener);
		}
	}

	private void removeCommandSaveListener()
	{
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
		if (commandService != null)
		{
			commandService.removeExecutionListener(fExecutionListener);
		}
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.ui.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.core.CoreStrings;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.util.StringUtil;
import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.DefaultSiteConnection;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.ftp.internal.FTPPropertyDialogProvider;

/**
 * @author Max Stepanov
 * @author Michael Xia
 */
@SuppressWarnings("restriction")
public class SiteConnectionPropertiesWidget extends Composite implements ModifyListener
{

	public static interface Client
	{
		public void setErrorMessage(String message);
	}

	private Client client;
	private ISiteConnection siteConnection;
	private Text nameText;
	private TargetEditor sourceEditor;
	private TargetEditor destinationEditor;
	private boolean changed;

	/**
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            SWT style bits
	 * @param client
	 *            a callback client to receive necessary notifications
	 */
	public SiteConnectionPropertiesWidget(Composite parent, int style, Client client)
	{
		super(parent, style);
		this.client = client;
		setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		/* row 1 - name */
		Label label = new Label(this, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SiteConnectionPropertiesWidget_LBL_Name));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		nameText = new Text(this, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());
		nameText.addModifyListener(this);

		/* row 2 - source */
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		group.setText(Messages.SiteConnectionPropertiesWidget_LBL_Source);

		sourceEditor = new TargetEditor(Messages.SiteConnectionPropertiesWidget_LBL_Source);
		// currently not allowing remote site to be a source
		sourceEditor.createTargets(group, false);

		/* row 3 - destination */
		group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		group.setText(Messages.SiteConnectionPropertiesWidget_LBL_Destination);

		destinationEditor = new TargetEditor(Messages.SiteConnectionPropertiesWidget_LBL_Destination);
		destinationEditor.createTargets(group, true);
	}

	public void setSource(ISiteConnection source)
	{
		if (this.siteConnection == source && source != null)
		{
			return;
		}
		this.siteConnection = source;
		if (source == null)
		{
			((GridData) getLayoutData()).exclude = true;
			setVisible(false);
			getParent().layout();
			validateAll();
			changed = false;
			return;
		}
		if (!isVisible())
		{
			((GridData) getLayoutData()).exclude = false;
			setVisible(true);
			getParent().layout();
		}
		nameText.setText(source.getName());
		nameText.setEnabled(source != DefaultSiteConnection.getInstance());
		sourceEditor.setTarget(source.getSource());
		destinationEditor.setTarget(source.getDestination());
		validateAll();
		changed = source.getSource() == null
				|| (source != DefaultSiteConnection.getInstance() && source.getDestination() == null);
	}

	public ISiteConnection getSource()
	{
		return siteConnection;
	}

	public boolean isChanged()
	{
		return siteConnection != null && changed;
	}

	public boolean applyChanges()
	{
		if (!validateAll())
		{
			return false;
		}
		siteConnection.setName(nameText.getText());

		IConnectionPoint connectionPoint = sourceEditor.getTarget();
		if (connectionPoint != null)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(connectionPoint);
		}
		siteConnection.setSource(connectionPoint);
		sourceEditor.clearNewConnections(false);

		connectionPoint = destinationEditor.getTarget();
		if (connectionPoint != null)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(connectionPoint);
		}
		siteConnection.setDestination(connectionPoint);
		destinationEditor.clearNewConnections(false);

		changed = false;
		return true;
	}

	public void cancelChanges()
	{
		sourceEditor.clearNewConnections(true);
		destinationEditor.clearNewConnections(true);
	}

	public void modifyText(ModifyEvent e)
	{
		if (validateAll())
		{
			changed = true;
		}
	}

	private boolean validateAll()
	{
		String message = null;
		if (siteConnection != null)
		{
			String name = nameText.getText().trim();
			if (name.length() == 0)
			{
				message = Messages.SiteConnectionPropertiesWidget_ERR_EmptyName;
			}
			else if (siteConnection != DefaultSiteConnection.getInstance() && name.equals(DefaultSiteConnection.NAME))
			{
				message = MessageFormat.format(Messages.SiteConnectionPropertiesWidget_ERR_DuplicateNames, name);
			}
			else if ((siteConnection == null || !name.equalsIgnoreCase(siteConnection.getName()))
					&& !SiteConnectionUtils.isSiteNameUnique(name))
			{
				message = MessageFormat.format(Messages.SiteConnectionPropertiesWidget_ERR_DuplicateNames, name);
			}
			if (message == null)
			{
				message = sourceEditor.validate();
			}
			if (message == null)
			{
				message = destinationEditor.validate();
			}
		}
		client.setErrorMessage(message);
		return message == null;
	}

	private IConnectionPoint createNewRemoteConnection()
	{
		Dialog dlg = new FTPPropertyDialogProvider().createPropertyDialog(new SameShellProvider(this));
		if (dlg instanceof IPropertyDialog)
		{
			((IPropertyDialog) dlg).setPropertySource(CoreIOPlugin.getConnectionPointManager().getType(
					FTPConnectionPoint.TYPE));
		}
		if (dlg.open() == Window.OK)
		{
			Object result = null;
			if (dlg instanceof IPropertyDialog)
			{
				result = ((IPropertyDialog) dlg).getPropertySource();
			}
			if (result instanceof IConnectionPoint)
			{
				return (IConnectionPoint) result;
			}
		}
		return null;
	}

	private IContainer browseWorkspace(IContainer container, IPath path)
	{
		FileFolderSelectionDialog dlg = new FileFolderSelectionDialog(getShell(), false, IResource.FOLDER);
		IFileStore input = EFSUtils.getFileStore(container);
		dlg.setInput(input);
		dlg.setInitialSelection(input.getFileStore(path));
		if (dlg.open() == Window.OK)
		{
			if (dlg.getFirstResult() instanceof IAdaptable)
			{
				return (IContainer) ((IAdaptable) dlg.getFirstResult()).getAdapter(IResource.class);
			}
		}
		return null;
	}

	private IPath browseFilesystem(IPath path)
	{
		DirectoryDialog dlg = new DirectoryDialog(getShell());
		dlg.setFilterPath(path.toOSString());
		String result = dlg.open();
		if (result != null)
		{
			return Path.fromOSString(result);
		}
		return null;
	}

	private class TargetEditor implements SelectionListener, ISelectionChangedListener
	{

		private static final int REMOTE = 1;
		private static final int PROJECT = 2;
		private static final int FILESYSTEM = 3;

		private String name;
		private Composite mainComp;
		private Button remoteRadio;
		private ComboViewer remotesViewer;
		private Button remoteNewButton;
		private Button projectRadio;
		private ComboViewer projectViewer;
		private Text projectFolderText;
		private Button projectBrowseButton;
		private Button filesystemRadio;
		private Text filesystemFolderText;
		private Button filesystemBrowseButton;

		private Label defaultDescriptionLabel;
		private boolean isDefault = false;

		private List<IConnectionPoint> newRemoteConnections;

		TargetEditor(String name)
		{
			this.name = name;
			newRemoteConnections = new ArrayList<IConnectionPoint>();
		}

		/**
		 * @param remove
		 *            true if the newly created connections should be discarded, false if we are only resetting the list
		 */
		public void clearNewConnections(boolean remove)
		{
			if (remove)
			{
				for (IConnectionPoint connection : newRemoteConnections)
				{
					CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(connection);
				}
			}
			newRemoteConnections.clear();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			Object source = e.getSource();
			if (source == remoteNewButton)
			{
				IConnectionPoint result = createNewRemoteConnection();
				if (result != null)
				{
					newRemoteConnections.add(result);
					updateRemotesViewer();
					setType(REMOTE);
					remotesViewer.setSelection(new StructuredSelection(result));
					changed = true;
				}
				validateAll();
			}
			else if (source == projectBrowseButton)
			{
				IContainer container = (IContainer) ((IStructuredSelection) projectViewer.getSelection())
						.getFirstElement();
				if (container == null)
				{
					container = ResourcesPlugin.getWorkspace().getRoot();
				}
				IContainer result = browseWorkspace(container, Path.fromPortableString(projectFolderText.getText()));
				if (result != null)
				{
					projectViewer.setSelection(new StructuredSelection(result.getProject()), true);
					projectFolderText.setText(result.getProjectRelativePath().toPortableString());
					changed = true;
				}
				validateAll();
			}
			else if (source == filesystemBrowseButton)
			{
				IPath path = browseFilesystem(Path.fromPortableString(filesystemFolderText.getText()));
				if (path != null)
				{
					filesystemFolderText.setText(path.toPortableString());
					changed = true;
				}
				validateAll();
			}
			else
			{
				setEnabled(getType());
				if (validateAll())
				{
					changed = true;
				}
			}
		}

		public void selectionChanged(SelectionChangedEvent event)
		{
			Object source = event.getSource();
			if (source == projectViewer)
			{
				projectFolderText.setText(Path.ROOT.toPortableString());
				setEnabled(getType());
			}

			if (validateAll())
			{
				changed = true;
			}
		}

		private void createTargets(Composite parent, boolean showRemote)
		{
			parent.setLayout(GridLayoutFactory.swtDefaults().create());
			mainComp = new Composite(parent, SWT.NONE);
			mainComp.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(4).create());
			mainComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

			/* row 1 - remote */
			remoteRadio = new Button(mainComp, SWT.RADIO);
			remoteRadio.setText(Messages.SiteConnectionPropertiesWidget_LBL_Remote);
			remoteRadio.setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote).create());
			remoteRadio.addSelectionListener(this);

			remotesViewer = new ComboViewer(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY);
			remotesViewer.getControl().setLayoutData(
					GridDataFactory.swtDefaults().exclude(!showRemote).align(SWT.FILL, SWT.CENTER).span(2, 1)
							.grab(true, false).create());
			remotesViewer.setContentProvider(ArrayContentProvider.getInstance());
			remotesViewer.addSelectionChangedListener(this);
			updateRemotesViewer();

			remoteNewButton = new Button(mainComp, SWT.PUSH);
			remoteNewButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
			remoteNewButton.setLayoutData(GridDataFactory.swtDefaults().exclude(!showRemote).create());
			remoteNewButton.addSelectionListener(this);

			/* row 2 - project */
			projectRadio = new Button(mainComp, SWT.RADIO);
			projectRadio.setText(Messages.SiteConnectionPropertiesWidget_LBL_Project);
			projectRadio.setLayoutData(GridDataFactory.swtDefaults().create());
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			projectRadio.setEnabled(projects.length > 0);
			projectRadio.addSelectionListener(this);

			projectViewer = new ComboViewer(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY);
			projectViewer.getControl().setLayoutData(
					GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).create());
			projectViewer.setContentProvider(ArrayContentProvider.getInstance());
			if (projects.length == 0)
			{
				projectViewer.setLabelProvider(new LabelProvider());
				Object[] input = new Object[] { Messages.SiteConnectionPropertiesWidget_NoProject };
				projectViewer.setInput(input);
				projectViewer.setSelection(new StructuredSelection(input[0]), true);
			}
			else
			{
				projectViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
				Arrays.sort(projects, new Comparator<IProject>()
				{

					public int compare(IProject o1, IProject o2)
					{
						return o1.getName().compareTo(o2.getName());
					}
				});
				projectViewer.setInput(projects);
				projectViewer.setSelection(new StructuredSelection(projects[0]), true);
			}
			projectViewer.addSelectionChangedListener(this);

			/* row 3 - project folder */
			new Label(mainComp, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());

			Composite folder = new Composite(mainComp, SWT.NONE);
			folder.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
			folder.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false)
					.create());

			Label label = new Label(folder, SWT.NONE);
			label.setText(StringUtil.makeFormLabel(Messages.SiteConnectionPropertiesWidget_LBL_Folder));
			label.setLayoutData(GridDataFactory.swtDefaults().create());

			projectFolderText = new Text(folder, SWT.BORDER);
			projectFolderText.setText(Path.ROOT.toPortableString());
			projectFolderText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
					.create());
			projectFolderText.addModifyListener(SiteConnectionPropertiesWidget.this);

			projectBrowseButton = new Button(mainComp, SWT.PUSH);
			projectBrowseButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
			projectBrowseButton.setLayoutData(GridDataFactory.swtDefaults().create());
			projectBrowseButton.addSelectionListener(this);

			/* row 4 - filesystem */
			filesystemRadio = new Button(mainComp, SWT.RADIO);
			filesystemRadio.setText(Messages.SiteConnectionPropertiesWidget_LBL_Filesystem);
			filesystemRadio.setLayoutData(GridDataFactory.swtDefaults().create());
			filesystemRadio.addSelectionListener(this);

			filesystemFolderText = new Text(mainComp, SWT.BORDER);
			filesystemFolderText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1)
					.grab(true, false).create());
			filesystemFolderText.addModifyListener(SiteConnectionPropertiesWidget.this);

			filesystemBrowseButton = new Button(mainComp, SWT.PUSH);
			filesystemBrowseButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
			filesystemBrowseButton.setLayoutData(GridDataFactory.swtDefaults().create());
			filesystemBrowseButton.addSelectionListener(this);

			/* description text for the default connection */
			defaultDescriptionLabel = new Label(parent, SWT.WRAP);
			defaultDescriptionLabel.setText(Messages.SiteConnectionPropertiesWidget_LBL_DefaultDescription);
			defaultDescriptionLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).exclude(true)
					.create());
		}

		private void updateRemotesViewer()
		{
			IConnectionPoint[] sites = ConnectionPointUtils.getRemoteConnectionPoints();
			if (sites.length == 0)
			{
				remoteRadio.setEnabled(false);
				remotesViewer.setLabelProvider(new LabelProvider());
				Object[] input = new Object[] { Messages.SiteConnectionPropertiesWidget_NoRemoteSite };
				remotesViewer.setInput(input);
				remotesViewer.setSelection(new StructuredSelection(input[0]), true);
			}
			else
			{
				remoteRadio.setEnabled(true);
				remoteRadio.setSelection(true);
				remotesViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
				Arrays.sort(sites, new Comparator<IConnectionPoint>()
				{

					public int compare(IConnectionPoint o1, IConnectionPoint o2)
					{
						return o1.getName().compareTo(o2.getName());
					}
				});
				remotesViewer.setInput(sites);
				remotesViewer.setSelection(new StructuredSelection(sites[0]), true);
				Control control = remotesViewer.getControl();
				control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
			}
		}

		private void setType(int type)
		{
			remoteRadio.setSelection(false);
			projectRadio.setSelection(false);
			filesystemRadio.setSelection(false);
			switch (type)
			{
				case REMOTE:
					remoteRadio.setSelection(true);
					break;
				case PROJECT:
					projectRadio.setSelection(true);
					break;
				case FILESYSTEM:
					filesystemRadio.setSelection(true);
					break;
			}
			setEnabled(type);
		}

		private int getType()
		{
			if (remoteRadio.getSelection())
			{
				return REMOTE;
			}
			else if (projectRadio.getSelection())
			{
				return PROJECT;
			}
			else if (filesystemRadio.getSelection())
			{
				return FILESYSTEM;
			}
			return -1;
		}

		private void setEnabled(int type)
		{
			setEnabled(type, true);
			switch (type)
			{
				case REMOTE:
					setEnabled(PROJECT, false);
					setEnabled(FILESYSTEM, false);
					break;
				case PROJECT:
					setEnabled(REMOTE, false);
					setEnabled(FILESYSTEM, false);
					break;
				case FILESYSTEM:
					setEnabled(REMOTE, false);
					setEnabled(PROJECT, false);
					break;
				default:
					setEnabled(REMOTE, false);
					setEnabled(PROJECT, false);
					setEnabled(FILESYSTEM, false);
					break;
			}
		}

		private void setEnabled(int type, boolean enabled)
		{
			switch (type)
			{
				case REMOTE:
					if (remoteRadio.getEnabled())
					{
						remotesViewer.getControl().setEnabled(true);
						remoteNewButton.setEnabled(enabled);
					}
					else
					{
						Control control = remotesViewer.getControl();
						control.setEnabled(false);
						control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
					}
					break;
				case PROJECT:
					if (!projectRadio.getEnabled())
					{
						Control control = projectViewer.getControl();
						control.setEnabled(false);
						control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
						projectFolderText.setEnabled(false);
						projectBrowseButton.setEnabled(false);
						return;
					}

					projectViewer.getControl().setEnabled(enabled);
					IProject project = (IProject) ((IStructuredSelection) projectViewer.getSelection())
							.getFirstElement();
					boolean hasFolders = false;
					if (project != null)
					{
						try
						{
							IResource[] resources = project.members();
							for (IResource resource : resources)
							{
								if (resource instanceof IContainer)
								{
									hasFolders = true;
									break;
								}
							}
						}
						catch (CoreException e)
						{
						}
					}
					projectFolderText.setEnabled(enabled && hasFolders);
					projectBrowseButton.setEnabled(enabled && hasFolders);
					break;
				case FILESYSTEM:
					filesystemFolderText.setEnabled(enabled);
					filesystemBrowseButton.setEnabled(enabled);
					break;
			}
		}

		private void setTarget(IConnectionPoint connectionPoint)
		{
			isDefault = (siteConnection == DefaultSiteConnection.getInstance() && connectionPoint == siteConnection
					.getDestination());
			mainComp.setVisible(!isDefault);
			((GridData) mainComp.getLayoutData()).exclude = isDefault;
			defaultDescriptionLabel.setVisible(isDefault);
			((GridData) defaultDescriptionLabel.getLayoutData()).exclude = !isDefault;
			layout(true, true);

			if (ConnectionPointUtils.isRemote(connectionPoint))
			{
				setType(REMOTE);
				remotesViewer.setSelection(new StructuredSelection(connectionPoint), true);
			}
			else if (ConnectionPointUtils.isWorkspace(connectionPoint))
			{
				setType(PROJECT);
				IResource resource = (IResource) connectionPoint.getAdapter(IResource.class);
				if (resource != null)
				{
					projectViewer.setSelection(new StructuredSelection(resource.getProject()), true);
					IPath path = resource.getProjectRelativePath();
					if (path.isEmpty())
					{
						path = Path.ROOT;
					}
					projectFolderText.setText(path.toPortableString());
				}
			}
			else if (ConnectionPointUtils.isLocal(connectionPoint))
			{
				setType(FILESYSTEM);
				File file = (File) connectionPoint.getAdapter(File.class);
				if (file != null)
				{
					filesystemFolderText.setText(Path.fromOSString(file.getAbsolutePath()).toPortableString());
				}
			}
		}

		private IConnectionPoint getTarget()
		{
			if (isDefault)
			{
				return null;
			}
			if (remoteRadio.getSelection())
			{
				return (IConnectionPoint) ((IStructuredSelection) remotesViewer.getSelection()).getFirstElement();
			}
			else if (projectRadio.getSelection())
			{
				IProject project = (IProject) ((IStructuredSelection) projectViewer.getSelection()).getFirstElement();
				IPath path = Path.fromPortableString(projectFolderText.getText());
				IContainer container = (IContainer) project.findMember(path);
				return ConnectionPointUtils.findOrCreateWorkspaceConnectionPoint(container);
			}
			else if (filesystemRadio.getSelection())
			{
				IPath path = Path.fromPortableString(filesystemFolderText.getText());
				return ConnectionPointUtils.findOrCreateLocalConnectionPoint(path);
			}
			return null;
		}

		private String validate()
		{
			if (isDefault)
			{
				return null;
			}
			if (remoteRadio.getSelection())
			{
				Object element = ((IStructuredSelection) remotesViewer.getSelection()).getFirstElement();
				if (!(element instanceof IConnectionPoint))
				{
					return Messages.SiteConnectionPropertiesWidget_ERR_NoRemote;
				}
			}
			else if (projectRadio.getSelection())
			{
				Object element = ((IStructuredSelection) projectViewer.getSelection()).getFirstElement();
				if (!(element instanceof IProject))
				{
					return Messages.SiteConnectionPropertiesWidget_ERR_NoProject;
				}
				IProject project = (IProject) element;
				IPath path = Path.fromPortableString(projectFolderText.getText());
				if (!(project.findMember(path) instanceof IContainer))
				{
					return Messages.SiteConnectionPropertiesWidget_ERR_InvalidProjectFolder;
				}
			}
			else if (filesystemRadio.getSelection())
			{
				IPath path = Path.fromPortableString(filesystemFolderText.getText());
				if (!path.toFile().isDirectory())
				{
					return Messages.SiteConnectionPropertiesWidget_ERR_InvalidFilesystemFolder;
				}
			}
			else
			{
				return MessageFormat.format(Messages.SiteConnectionPropertiesWidget_ERR_NoType, name.toLowerCase());
			}
			return null;
		}
	}
}

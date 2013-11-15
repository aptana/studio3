/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.core.CoreStrings;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.util.FileUtil;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ide.ui.io.actions.CopyFilesOperation;
import com.aptana.ide.ui.io.navigator.FileTreeContentProvider;
import com.aptana.ide.ui.io.navigator.FileTreeNameSorter;
import com.aptana.ide.ui.io.navigator.actions.FileSystemDeleteAction;
import com.aptana.ide.ui.io.navigator.actions.FileSystemRenameAction;
import com.aptana.ide.ui.io.navigator.actions.OpenFileAction;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionPointComposite implements SelectionListener, ISelectionChangedListener, IDoubleClickListener,
		TransferDragSourceListener, DropTargetListener
{

	public static interface Client
	{
		public void transfer(ConnectionPointComposite source);
	}

	private static final String[] COLUMN_NAMES = { Messages.ConnectionPointComposite_Column_Filename,
			Messages.ConnectionPointComposite_Column_Size, Messages.ConnectionPointComposite_Column_LastModified };

	private Composite fMain;
	private Link fEndPointLink;
	private ToolItem fRefreshItem;
	private ToolItem fHomeItem;
	private Link fPathLink;

	private TreeViewer fTreeViewer;
	private MenuItem fOpenItem;
	private MenuItem fTransferItem;
	private MenuItem fDeleteItem;
	private MenuItem fRenameItem;
	private MenuItem fRefreshMenuItem;
	private MenuItem fPropertiesItem;

	private String fName;
	private IConnectionPoint fConnectionPoint;
	private List<IAdaptable> fEndPointData;
	private Client fClient;

	public ConnectionPointComposite(Composite parent, String name, Client client)
	{
		fName = name;
		fClient = client;
		fEndPointData = new ArrayList<IAdaptable>();

		fMain = createControl(parent);
	}

	public Control getControl()
	{
		return fMain;
	}

	public IAdaptable getCurrentInput()
	{
		// the root input is always IAdaptable
		return (IAdaptable) fTreeViewer.getInput();
	}

	public IAdaptable[] getSelectedElements()
	{
		ISelection selection = fTreeViewer.getSelection();
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
		{
			return new IAdaptable[0];
		}
		Object[] elements = ((IStructuredSelection) selection).toArray();
		// the selection should be all IAdaptable objects, but just to make sure
		List<IAdaptable> list = new ArrayList<IAdaptable>();
		for (Object element : elements)
		{
			if (element instanceof IAdaptable)
			{
				list.add((IAdaptable) element);
			}
		}
		return list.toArray(new IAdaptable[list.size()]);
	}

	public void setFocus()
	{
		fMain.setFocus();
	}

	public void setConnectionPoint(IConnectionPoint connection)
	{
		fConnectionPoint = connection;

		fEndPointData.clear();
		if (fConnectionPoint == null)
		{
			fEndPointLink.setText(""); //$NON-NLS-1$
		}
		else
		{
			String label = connection.getName();
			String tooltip = label;
			if (connection instanceof IBaseRemoteConnectionPoint)
			{
				IPath path = ((IBaseRemoteConnectionPoint) connection).getPath();
				if (path.segmentCount() > 0)
				{
					tooltip = MessageFormat.format("{0} ({1})", new Object[] { connection.getName(), //$NON-NLS-1$
							path.toPortableString() });
				}
			}
			fEndPointLink.setText(MessageFormat.format("<a>{0}</a>", label)); //$NON-NLS-1$
			fEndPointLink.setToolTipText(tooltip);
			fEndPointData.add(fConnectionPoint);
		}
		setPath(""); //$NON-NLS-1$
		fMain.layout(true, true);

		fTreeViewer.setInput(connection);
	}

	/**
	 * Adds a focus listener to the tree control to allow users the ability to listen for focus events.
	 * 
	 * @param listener
	 */
	public void addTreeFocusListener(FocusListener listener)
	{
		fTreeViewer.getControl().addFocusListener(listener);
	}

	public void refresh()
	{
		Object input = fTreeViewer.getInput();
		IResource resource = null;
		if (input instanceof IAdaptable)
		{
			resource = (IResource) ((IAdaptable) input).getAdapter(IResource.class);
		}
		if (resource != null)
		{
			try
			{
				resource.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			catch (CoreException e)
			{
			}
		}
		updateContent(fEndPointData.get(fEndPointData.size() - 1));
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();

		if (source == fRefreshItem)
		{
			refresh();
		}
		else if (source == fHomeItem)
		{
			gotoHome();
		}
		else if (source == fOpenItem)
		{
			open(fTreeViewer.getSelection());
		}
		else if (source == fTransferItem)
		{
			if (fClient != null)
			{
				fClient.transfer(this);
			}
		}
		else if (source == fDeleteItem)
		{
			delete(fTreeViewer.getSelection());
		}
		else if (source == fRenameItem)
		{
			rename();
		}
		else if (source == fRefreshMenuItem)
		{
			refresh(fTreeViewer.getSelection());
		}
		else if (source == fPropertiesItem)
		{
			openPropertyPage(fTreeViewer.getSelection());
		}
		else if (source == fPathLink)
		{
			// e.text has the index; needs to increment by 1 since 0 for
			// fEndPointData is the root
			updateContent(fEndPointData.get(Integer.parseInt(e.text) + 1));
		}
		else if (source == fEndPointLink)
		{
			gotoHome();
		}
	}

	public void selectionChanged(SelectionChangedEvent event)
	{
		updateMenuStates();
	}

	public void doubleClick(DoubleClickEvent event)
	{
		if (fClient == null)
		{
			open(event.getSelection());
		}
		else
		{
			Object object = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (object instanceof IAdaptable)
			{
				IAdaptable adaptable = (IAdaptable) object;
				if (Utils.isDirectory((IAdaptable) object))
				{
					// goes into the folder
					updateContent(adaptable);
				}
				else
				{
					fClient.transfer(this);
				}
			}
		}
	}

	public Transfer getTransfer()
	{
		return LocalSelectionTransfer.getTransfer();
	}

	public void dragFinished(DragSourceEvent event)
	{
		LocalSelectionTransfer.getTransfer().setSelection(null);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
	}

	public void dragSetData(DragSourceEvent event)
	{
		event.data = fTreeViewer.getSelection();
	}

	public void dragStart(DragSourceEvent event)
	{
		LocalSelectionTransfer.getTransfer().setSelection(fTreeViewer.getSelection());
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
	}

	public void dragEnter(DropTargetEvent event)
	{
		if (event.detail == DND.DROP_DEFAULT)
		{
			if ((event.operations & DND.DROP_COPY) == 0)
			{
				event.detail = DND.DROP_NONE;
			}
			else
			{
				event.detail = DND.DROP_COPY;
			}
		}
	}

	public void dragLeave(DropTargetEvent event)
	{
	}

	public void dragOperationChanged(DropTargetEvent event)
	{
	}

	public void dragOver(DropTargetEvent event)
	{
	}

	public void drop(DropTargetEvent event)
	{
		IFileStore targetStore = null;
		if (event.item == null)
		{
			targetStore = Utils.getFileStore((IAdaptable) fTreeViewer.getInput());
		}
		else
		{
			TreeItem target = (TreeItem) event.item;
			targetStore = getFolderStore((IAdaptable) target.getData());
		}
		if (targetStore == null)
		{
			return;
		}

		if (event.data instanceof ITreeSelection)
		{
			ITreeSelection selection = (ITreeSelection) event.data;
			TreePath[] paths = selection.getPaths();
			if (paths.length > 0)
			{
				List<IAdaptable> elements = new ArrayList<IAdaptable>();
				for (TreePath path : paths)
				{
					boolean alreadyIn = false;
					for (TreePath path2 : paths)
					{
						if (!path.equals(path2) && path.startsWith(path2, null))
						{
							alreadyIn = true;
							break;
						}
					}
					if (!alreadyIn)
					{
						elements.add((IAdaptable) path.getLastSegment());
					}
				}

				CopyFilesOperation operation = new CopyFilesOperation(getControl().getShell());
				operation.copyFiles(elements.toArray(new IAdaptable[elements.size()]), targetStore,
						new JobChangeAdapter()
						{

							@Override
							public void done(IJobChangeEvent event)
							{
								IOUIPlugin.refreshNavigatorView(fTreeViewer.getInput());
								UIUtils.getDisplay().asyncExec(new Runnable()
								{

									public void run()
									{
										refresh();
									}
								});
							}
						});
			}
		}
	}

	public void dropAccept(DropTargetEvent event)
	{
	}

	protected Composite createControl(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		main.setLayout(layout);

		Composite top = createTopComposite(main);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite path = createPathComposite(main);
		path.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		TreeViewer treeViewer = createTreeViewer(main);
		treeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return main;
	}

	private Composite createTopComposite(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);

		Label label = new Label(main, SWT.NONE);
		label.setText(fName + ":"); //$NON-NLS-1$

		fEndPointLink = new Link(main, SWT.NONE);
		fEndPointLink.addSelectionListener(this);

		ToolBar toolbar = new ToolBar(main, SWT.FLAT);
		fHomeItem = new ToolItem(toolbar, SWT.PUSH);
		fHomeItem.setImage(SyncingUIPlugin.getImage("icons/full/obj16/home.png")); //$NON-NLS-1$
		fHomeItem.setToolTipText(Messages.ConnectionPointComposite_TTP_Home);
		fHomeItem.addSelectionListener(this);

		return main;
	}

	private Composite createPathComposite(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);

		fPathLink = new Link(main, SWT.NONE);
		fPathLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// uses a bold font for path
		final Font font = new Font(fPathLink.getDisplay(), SWTUtils.boldFont(fPathLink.getFont()));
		fPathLink.setFont(font);
		fPathLink.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				font.dispose();
			}
		});
		fPathLink.addSelectionListener(this);

		ToolBar toolbar = new ToolBar(main, SWT.FLAT);
		fRefreshItem = new ToolItem(toolbar, SWT.PUSH);
		fRefreshItem.setImage(SyncingUIPlugin.getImage("icons/full/obj16/refresh.gif")); //$NON-NLS-1$
		fRefreshItem.setToolTipText(Messages.ConnectionPointComposite_TTP_Refresh);
		fRefreshItem.addSelectionListener(this);

		return main;
	}

	private TreeViewer createTreeViewer(Composite parent)
	{
		fTreeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		Tree tree = fTreeViewer.getTree();
		tree.setHeaderVisible(true);

		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setWidth(300);
		column.setText(COLUMN_NAMES[0]);

		column = new TreeColumn(tree, SWT.LEFT);
		column.setWidth(50);
		column.setText(COLUMN_NAMES[1]);

		column = new TreeColumn(tree, SWT.LEFT);
		column.setWidth(150);
		column.setText(COLUMN_NAMES[2]);

		fTreeViewer.setContentProvider(new FileTreeContentProvider());
		fTreeViewer.setLabelProvider(new ConnectionPointLabelProvider());
		fTreeViewer.setComparator(new FileTreeNameSorter());
		fTreeViewer.addSelectionChangedListener(this);
		fTreeViewer.addDoubleClickListener(this);

		fTreeViewer.addDragSupport(DND.DROP_COPY | DND.DROP_DEFAULT,
				new Transfer[] { LocalSelectionTransfer.getTransfer() }, this);
		fTreeViewer.addDropSupport(DND.DROP_COPY | DND.DROP_DEFAULT,
				new Transfer[] { LocalSelectionTransfer.getTransfer() }, this);

		// builds the context menu
		tree.setMenu(createMenu(tree));

		updateMenuStates();

		return fTreeViewer;
	}

	private Menu createMenu(Control parent)
	{
		Menu menu = new Menu(parent);
		fOpenItem = new MenuItem(menu, SWT.PUSH);
		fOpenItem.setText(CoreStrings.OPEN);
		fOpenItem.setAccelerator(SWT.F3);
		fOpenItem.addSelectionListener(this);

		fTransferItem = new MenuItem(menu, SWT.PUSH);
		fTransferItem.setText(Messages.ConnectionPointComposite_LBL_Transfer);
		fTransferItem.addSelectionListener(this);

		new MenuItem(menu, SWT.SEPARATOR);
		fDeleteItem = new MenuItem(menu, SWT.PUSH);
		fDeleteItem.setText(CoreStrings.DELETE);
		fDeleteItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		fDeleteItem.setAccelerator(SWT.DEL);
		fDeleteItem.addSelectionListener(this);

		fRenameItem = new MenuItem(menu, SWT.PUSH);
		fRenameItem.setText(CoreStrings.RENAME);
		fRenameItem.setAccelerator(SWT.F2);
		fRenameItem.addSelectionListener(this);

		new MenuItem(menu, SWT.SEPARATOR);
		fRefreshMenuItem = new MenuItem(menu, SWT.PUSH);
		fRefreshMenuItem.setText(CoreStrings.REFRESH);
		fRefreshMenuItem.setImage(SyncingUIPlugin.getImage("/icons/full/obj16/refresh.gif")); //$NON-NLS-1$
		fRefreshMenuItem.setAccelerator(SWT.F5);
		fRefreshMenuItem.addSelectionListener(this);

		new MenuItem(menu, SWT.SEPARATOR);
		fPropertiesItem = new MenuItem(menu, SWT.PUSH);
		fPropertiesItem.setText(CoreStrings.PROPERTIES);
		fPropertiesItem.setAccelerator(SWT.ALT | '\r');
		fPropertiesItem.addSelectionListener(this);

		return menu;
	}

	private void gotoHome()
	{
		updateContent(fConnectionPoint);
	}

	private void open(ISelection selection)
	{
		Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof IAdaptable)
		{
			IAdaptable adaptable = (IAdaptable) object;
			if (Utils.isDirectory((IAdaptable) object))
			{
				// goes into the folder
				updateContent(adaptable);
			}
			else
			{
				// opens the file in the editor
				OpenFileAction action = new OpenFileAction();
				action.updateSelection((IStructuredSelection) selection);
				action.run();
			}
		}
	}

	private void delete(ISelection selection)
	{
		final FileSystemDeleteAction action = new FileSystemDeleteAction(getControl().getShell(), fTreeViewer.getTree());
		action.updateSelection((IStructuredSelection) selection);
		action.addJobListener(new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						refresh();
					}
				});
				action.removeJobListener(this);
			}
		});
		action.run();
	}

	private void rename()
	{
		FileSystemRenameAction action = new FileSystemRenameAction(getControl().getShell(), fTreeViewer.getTree());
		action.run();
		refresh();
	}

	private void refresh(ISelection selection)
	{
		if (selection.isEmpty())
		{
			// refreshes the root
			refresh();
		}
		else
		{
			Object[] elements = ((IStructuredSelection) selection).toArray();
			IResource resource;
			for (Object element : elements)
			{
				resource = null;
				if (element instanceof IAdaptable)
				{
					resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
				}
				if (resource != null)
				{
					try
					{
						resource.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					catch (CoreException e)
					{
					}
				}
				fTreeViewer.refresh(element);
			}
		}
	}

	private void openPropertyPage(ISelection selection)
	{
		IAdaptable element = (IAdaptable) ((IStructuredSelection) selection).getFirstElement();
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(getControl().getShell(), element, null, null,
				null);
		dialog.open();
	}

	private void setComboData(IAdaptable data)
	{
		fEndPointData.clear();

		if (data instanceof IContainer)
		{
			// a workspace project/folder
			IContainer container = (IContainer) data;
			IContainer root = (IContainer) fConnectionPoint.getAdapter(IResource.class);

			String path = getRelativePath(root, container);
			if (path != null)
			{
				String[] segments = (new Path(path)).segments();
				IContainer segmentPath = root;
				for (String segment : segments)
				{
					segmentPath = (IContainer) segmentPath.findMember(segment);
					fEndPointData.add(segmentPath);
				}
			}
		}
		else
		{
			// a filesystem or remote path
			IFileStore fileStore = Utils.getFileStore(data);
			if (fileStore != null)
			{
				IFileStore homeFileStore = Utils.getFileStore(fConnectionPoint);
				while (fileStore.getParent() != null && !fileStore.equals(homeFileStore))
				{
					fEndPointData.add(0, fileStore);
					fileStore = fileStore.getParent();
				}
			}
		}
		fEndPointData.add(0, fConnectionPoint);
	}

	private void setPath(String path)
	{
		StringBuilder linkPath = new StringBuilder();
		path = path.replace('\\', '/');
		String separator = "/"; //$NON-NLS-1$
		if (path.startsWith(separator))
		{
			// removes the leading separator
			path = path.substring(1);
		}
		String displayedPath = FileUtil.compressLeadingPath(path, 60);
		if (displayedPath.equals(path))
		{
			String[] folders = path.split(separator);
			int i;
			for (i = 0; i < folders.length - 1; ++i)
			{
				linkPath.append(MessageFormat.format("<a href=\"{0}\">{1}</a>", i, folders[i])); //$NON-NLS-1$
				linkPath.append(separator);
			}
			if (folders.length > 0)
			{
				// no need for a link on the last directory since we are in it
				linkPath.append(folders[i]);
			}
		}
		else
		{
			// deals with the compression
			linkPath.append("...").append(separator); //$NON-NLS-1$
			// strips out the leading '.../'
			String endPath = displayedPath.substring(4);
			String[] endFolders = endPath.split(separator);
			int startIndex = path.split(separator).length - endFolders.length;
			int i;
			for (i = 0; i < endFolders.length - 1; ++i)
			{
				linkPath.append(MessageFormat.format("<a href=\"{0}\">{1}</a>", startIndex + i, endFolders[i])); //$NON-NLS-1$
				linkPath.append(separator);
			}
			if (endFolders.length > 0)
			{
				// no need for a link on the last directory since we are in it
				linkPath.append(endFolders[i]);
			}
		}
		fPathLink.setText(Messages.ConnectionPointComposite_LBL_Path + linkPath.toString());
	}

	private void updateContent(IAdaptable rootElement)
	{
		setComboData(rootElement);

		if (rootElement instanceof IContainer)
		{
			setPath(getRelativePath((IContainer) fConnectionPoint.getAdapter(IResource.class), (IContainer) rootElement));
		}
		else
		{
			IFileStore fileStore = Utils.getFileStore(rootElement);
			if (fileStore != null)
			{
				String path = fileStore.toString();
				IFileStore homeFileStore = Utils.getFileStore(fConnectionPoint);
				if (homeFileStore != null)
				{
					String homePath = homeFileStore.toString();
					int index = path.indexOf(homePath);
					if (index > -1)
					{
						path = path.substring(index + homePath.length());
					}
				}
				setPath(path);
			}
		}
		fTreeViewer.setInput(rootElement);
	}

	private void updateMenuStates()
	{
		ISelection selection = fTreeViewer.getSelection();
		boolean hasSelection = !selection.isEmpty() && (selection instanceof IStructuredSelection);
		boolean singleSelection = hasSelection && ((IStructuredSelection) selection).size() == 1;
		fOpenItem.setEnabled(hasSelection);
		fTransferItem.setEnabled(hasSelection);
		fDeleteItem.setEnabled(hasSelection);
		fRenameItem.setEnabled(hasSelection && singleSelection);
		fPropertiesItem.setEnabled(hasSelection && singleSelection);
	}

	private static IFileStore getFolderStore(IAdaptable destination)
	{
		IFileStore store = Utils.getFileStore(destination);
		IFileInfo info = Utils.getFileInfo(destination, IExtendedFileStore.EXISTENCE);
		if (store != null && info != null && !info.isDirectory())
		{
			store = store.getParent();
		}
		return store;
	}

	/**
	 * @param root
	 *            the root container
	 * @param element
	 *            a container under the root
	 * @return the relative path string of the element from the root
	 */
	private static String getRelativePath(IContainer root, IContainer element)
	{
		String rootPath = root.getFullPath().toString();
		String elementPath = element.getFullPath().toString();
		int index = elementPath.indexOf(rootPath);
		if (index == -1)
		{
			return null;
		}
		return elementPath.substring(index + rootPath.length());
	}
}

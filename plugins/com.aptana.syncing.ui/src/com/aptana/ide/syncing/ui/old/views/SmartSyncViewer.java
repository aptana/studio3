/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import java.io.File;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.syncing.core.old.ISyncResource;
import com.aptana.ide.syncing.core.old.SyncFile;
import com.aptana.ide.syncing.core.old.SyncFolder;
import com.aptana.ide.syncing.core.old.SyncState;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ui.io.compare.FileStoreCompareEditorInput;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
public class SmartSyncViewer
{

	private TreeViewer fViewer;
	private SmartSyncContentProvider fContentProvider;
	private SmartSyncLabelProvider fLabelProvider;
	private MenuItem fShowDiffs;
	private TreeColumn fColumnEnd1;
	private TreeColumn fColumnEnd2;
	private TreeColumn fLocalTimeColumn;
	private TreeColumn fRemoteTimeColumn;
	private Color fInitBackground;
	private Color fProgressBackground;

	private SyncFolder fRoot;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param endpoint1
	 *            the first end point
	 * @param endpoint2
	 *            the second end point
	 */
	public SmartSyncViewer(Composite parent, String endpoint1, String endpoint2)
	{
		fViewer = createContents(parent);
		setEndpoints(endpoint1, endpoint2);
	}

	/**
	 * Returns the tree widget for the viewer.
	 * 
	 * @return the tree widget
	 */
	public Tree getTree()
	{
		return fViewer.getTree();
	}

	/**
	 * Returns a list of current resources being displayed
	 * 
	 * @return an array of ISyncResource objects
	 */
	public ISyncResource[] getCurrentResources()
	{
		return fContentProvider.getCurrentResources();
	}

	/**
	 * Sets the input for the viewer.
	 * 
	 * @param root
	 *            the root of syncing folder
	 */
	public void setInput(SyncFolder root)
	{
		fRoot = root;
		fViewer.setInput(root);
	}

	/**
	 * Sets the cell modifier for the viewer.
	 * 
	 * @param modifier
	 *            the cell modifier
	 */
	public void setCellModifier(ICellModifier modifier)
	{
		fViewer.setCellModifier(modifier);
	}

	/**
	 * Sets the two end points for the syncing.
	 * 
	 * @param end1
	 *            the first end point
	 * @param end2
	 *            the second end point
	 */
	public void setEndpoints(String end1, String end2)
	{
		fColumnEnd1.setText(end1);
		fColumnEnd1.setToolTipText(end1);
		fColumnEnd2.setText(end2);
		fColumnEnd2.setToolTipText(end2);
	}

	/**
	 * Sets the direction in which syncing should occur.
	 * 
	 * @param direction
	 *            the direction for the syncing (UPLOAD, DOWNLOAD, BOTH, FORCE_UPLOAD, or FORCE_DOWNLOAD)
	 */
	public void setSyncDirection(int direction)
	{
		fContentProvider.setSyncDirection(direction);
		fLabelProvider.setSyncDirection(direction);
	}

	/**
	 * Sets the type of content presentation.
	 * 
	 * @param type
	 *            the type of presentation (FLAT_VIEW or TREE_VIEW)
	 */
	public void setPresentationType(int type)
	{
		fContentProvider.setPresentationType(type);
		fLabelProvider.setPresentationType(type);
		refreshAndExpandTo(2);
	}

	/**
	 * Sets the indication of if deleting remote files is selected.
	 * 
	 * @param delete
	 *            true if deleting remote files is selected, false otherwise
	 */
	public void setDeleteRemoteFiles(boolean delete)
	{
		fContentProvider.setDeleteRemoteFiles(delete);
		fLabelProvider.setDeleteRemoteFiles(delete);
		refresh();
	}

	/**
	 * Sets the indication of if deleting local files is selected.
	 * 
	 * @param delete
	 *            true if deleting local files is selected, false otherwise
	 */
	public void setDeleteLocalFiles(boolean delete)
	{
		fContentProvider.setDeleteLocalFiles(delete);
		fLabelProvider.setDeleteLocalFiles(delete);
		refresh();
	}

	public void setShowDatesSelected(boolean show)
	{
		if (show)
		{
			fLocalTimeColumn = new TreeColumn(getTree(), SWT.LEFT);
			fLocalTimeColumn.setText(Messages.SmartSyncDialog_LocalTime);
			fLocalTimeColumn.setToolTipText(Messages.SmartSyncViewer_LocalTimeTooltip);
			fLocalTimeColumn.setWidth(150);
			fRemoteTimeColumn = new TreeColumn(getTree(), SWT.LEFT);
			fRemoteTimeColumn.setText(Messages.SmartSyncDialog_RemoteTime);
			fRemoteTimeColumn.setToolTipText(Messages.SmartSyncViewer_RemoteTimeTooltip);
			fRemoteTimeColumn.setWidth(150);
		}
		else if (fLocalTimeColumn != null && !fLocalTimeColumn.isDisposed())
		{
			fLocalTimeColumn.dispose();
			fRemoteTimeColumn.dispose();
		}
		refresh();
	}

	public void setVisible(boolean visible)
	{
		getTree().setVisible(visible);
		if (visible)
		{
			setWidth(getTree().getParent().getSize().x);
		}
	}

	/**
	 * Shows progress bar on the specific item in the viewer.
	 * 
	 * @param item
	 *            the sync item
	 */
	public void showProgress(VirtualFileSyncPair item)
	{
		final ISyncResource resource = fRoot.find(item);
		if (resource == null || resource.getSyncState() == SyncState.ClientItemDeleted
				|| resource.getSyncState() == SyncState.ServerItemDeleted)
		{
			// resource no longer exists
			return;
		}
		final TreeItem treeItem = (TreeItem) fViewer.testFindItem(resource);
		if (treeItem == null)
		{
			// the item is not shown in the viewer
			return;
		}

		// adds the listener to draw the progress circle
		getTree().addListener(SWT.EraseItem, new Listener()
		{

			public void handleEvent(Event event)
			{
				if (event.item == treeItem && event.index == 0)
				{
					int state = resource.getTransferState();
					if (state != ISyncResource.SYNCING)
					{
						// only draws the progress when the item is being
						// synced
						return;
					}

					Rectangle bounds = treeItem.getImageBounds(0);
					int width = Math.min(bounds.width, bounds.height);
					int height = width;

					GC gc = event.gc;
					// clears the image area
					gc.setBackground(treeItem.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					gc.fillRectangle(bounds.x, bounds.y, bounds.width, bounds.height);

					// draws the outer circle
					gc.setBackground(fInitBackground);
					gc.drawOval(bounds.x + 1, bounds.y + 1, width - 4, height - 4);

					// calculates the percentage of bytes transferred
					double ratio = Math.min(1.0, ((double) resource.getTransferredBytes()) / getTransferSize(resource));
					// draws the progress
					gc.setBackground(fProgressBackground);
					gc.fillArc(bounds.x + 1, bounds.y + 1, width - 3, height - 3, 90, (int) (-ratio * 360));
				}
			}

		});

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				if (getTree().isDisposed())
				{
					return;
				}
				Rectangle bounds = treeItem.getBounds(0);
				getTree().redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
			}

		});
	}

	/**
	 * Refreshes the viewer to the latest content.
	 */
	public void refresh()
	{
		fViewer.refresh();
	}

	/**
	 * Refreshes the viewer and expand it to a specified level.
	 * 
	 * @param level
	 *            none-negative level or ALL_LEVELS to expand all levels of the tree
	 */
	public void refreshAndExpandTo(int level)
	{
		refresh();
		fViewer.expandToLevel(level);
		setWidth(getTree().getParent().getSize().x);
	}

	/**
	 * Adds the given filter to the viewer.
	 * 
	 * @param filter
	 *            a viewer filter
	 */
	public void addFilter(ViewerFilter filter)
	{
		fViewer.addFilter(filter);
	}

	/**
	 * Reveals the given element or tree path.
	 * 
	 * @param elementOrTreePath
	 *            the element or tree path to be revealed
	 */
	public void reveal(Object elementOrTreePath)
	{
		fViewer.reveal(elementOrTreePath);
	}

	/**
	 * Updates the given element's presentation when one or more of its properties changes.
	 * 
	 * @param element
	 *            the element
	 * @param properties
	 *            the properties that have changed, or null to indicate unknown
	 */
	public void update(Object element, String[] properties)
	{
		fViewer.update(element, properties);
	}

	private TreeViewer createContents(final Composite parent)
	{
		final TreeViewer viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
		viewer.setContentProvider(fContentProvider = new SmartSyncContentProvider());
		viewer.setLabelProvider(fLabelProvider = new SmartSyncLabelProvider(parent.getDisplay()));

		Tree tree = viewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		viewer.setAutoExpandLevel(2);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		fInitBackground = new Color(tree.getDisplay(), 175, 238, 238);
		fProgressBackground = new Color(tree.getDisplay(), 72, 209, 204);
		tree.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				fInitBackground.dispose();
				fProgressBackground.dispose();
			}

		});
		parent.addControlListener(new ControlAdapter()
		{

			public void controlResized(ControlEvent e)
			{
				setWidth(parent.getSize().x);
			}

		});

		// file column
		TreeColumn file = new TreeColumn(tree, SWT.LEFT);
		file.setWidth(250);
		file.setText(Messages.SmartSyncDialog_ColumnResources);
		file.setToolTipText(Messages.SmartSyncViewer_ColumnResourcesTooltip);

		// the column to specify whether the file should be skipped
		TreeColumn skip = new TreeColumn(tree, SWT.CENTER);
		skip.setWidth(40);
		skip.setText(Messages.SmartSyncDialog_ColumnSkip);
		skip.setToolTipText(Messages.SmartSyncViewer_ColumnSkipTooltip);

		// the state column on what will be done, if any, to the files from first end point
		fColumnEnd1 = new TreeColumn(tree, SWT.CENTER);
		fColumnEnd1.setWidth(125);

		// the state column on what will be done, if any, to the files from second end point
		fColumnEnd2 = new TreeColumn(tree, SWT.CENTER);
		fColumnEnd2.setWidth(125);

		Menu menu = new Menu(tree);
		fShowDiffs = new MenuItem(menu, SWT.PUSH);
		fShowDiffs.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (!viewer.getSelection().isEmpty() && viewer.getSelection() instanceof IStructuredSelection)
				{
					Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
					if (selection instanceof SyncFile)
					{
						SyncFile file = (SyncFile) selection;
						if (file.getSyncState() == SyncState.ClientItemIsNewer
								|| file.getSyncState() == SyncState.ServerItemIsNewer)
						{
							final VirtualFileSyncPair pair = file.getPair();
							FileStoreCompareEditorInput input = new FileStoreCompareEditorInput(
									new CompareConfiguration());
							input.setLeftFileStore(pair.getSourceFile());
							IFileStore destinationFile = pair.getDestinationFile();
							String name = destinationFile.getName();
							if (destinationFile instanceof IExtendedFileStore)
							{
								// this is a remote file, so downloads to a local temp copy first for speed purpose
								try
								{
									File localFile = destinationFile.toLocalFile(EFS.CACHE, null);
									destinationFile = EFS.getLocalFileSystem().getStore(
											Path.fromOSString(localFile.getAbsolutePath()));
								}
								catch (CoreException ce)
								{
									// logs as warning since we will fall back to use the remote file store directly in
									// this case
									IdeLog.logWarning(SyncingUIPlugin.getDefault(), ce);
								}
							}
							input.setRightFileStore(destinationFile, name);
							input.initializeCompareConfiguration();
							CompareUI.openCompareDialog(input);
						}
					}
				}
			}
		});
		fShowDiffs.setImage(SyncingUIPlugin.getImage("icons/full/obj16/compare_view.gif")); //$NON-NLS-1$
		fShowDiffs.setText(Messages.SmartSyncDialog_ShowDiffs);
		fShowDiffs.setEnabled(true);
		tree.setMenu(menu);

		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				if (!viewer.getSelection().isEmpty() && viewer.getSelection() instanceof IStructuredSelection)
				{
					Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
					if (selection instanceof SyncFile)
					{
						SyncFile file = (SyncFile) selection;
						if (file.getSyncState() == SyncState.ClientItemIsNewer
								|| file.getSyncState() == SyncState.ServerItemIsNewer)
						{
							fShowDiffs.setEnabled(true);
							return;
						}
					}
				}
				fShowDiffs.setEnabled(false);
			}

		});
		viewer.setCellEditors(new CellEditor[] { null, new CheckboxCellEditor(), null, null });
		viewer.setColumnProperties(new String[] { Messages.SmartSyncDialog_ColumnName,
				Messages.SmartSyncDialog_ColumnSkip, Messages.SmartSyncDialog_ColumnLocal,
				Messages.SmartSyncDialog_ColumnRemote });

		return viewer;
	}

	private void setWidth(int totalWidth)
	{
		Tree tree = getTree();
		TreeColumn[] columns = tree.getColumns();
		int width = 0;
		for (int i = columns.length - 1; i > 0; --i)
		{
			width += columns[i].getWidth();
		}
		ScrollBar verticalBar = tree.getVerticalBar();
		if (verticalBar.isVisible())
		{
			width += verticalBar.getSize().x;
		}
		columns[0].setWidth(totalWidth - width - (verticalBar.getSize().x + 1));
	}

	/**
	 * Gets the transfer size for the sync resource.
	 * 
	 * @param resource
	 *            the resource
	 * @return the transfer size, or zero if unable to compute
	 */
	private static long getTransferSize(ISyncResource resource)
	{
		if (resource.getPair() == null)
		{
			return 0;
		}
		IFileStore file = null;
		int state = resource.getSyncState();
		if (state == SyncState.ClientItemIsNewer || state == SyncState.ClientItemOnly)
		{
			file = resource.getPair().getSourceFile();
		}
		else if (state == SyncState.ServerItemIsNewer || state == SyncState.ServerItemOnly)
		{
			file = resource.getPair().getDestinationFile();
		}
		if (file == null)
		{
			return 0;
		}
		return file.fetchInfo().getLength();
	}

}

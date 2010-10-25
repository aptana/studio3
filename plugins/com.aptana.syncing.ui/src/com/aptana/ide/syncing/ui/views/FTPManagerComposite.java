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
package com.aptana.ide.syncing.ui.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.DefaultSiteConnection;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.core.events.ISiteConnectionListener;
import com.aptana.ide.syncing.core.events.SiteConnectionEvent;
import com.aptana.ide.syncing.core.old.ConnectionPointSyncPair;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.core.old.handlers.SyncEventHandlerAdapter;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.actions.DownloadAction;
import com.aptana.ide.syncing.ui.actions.UploadAction;
import com.aptana.ide.syncing.ui.dialogs.SiteConnectionsEditorDialog;
import com.aptana.ide.syncing.ui.editors.EditorUtils;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.old.views.SmartSyncDialog;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FTPManagerComposite implements SelectionListener, ISiteConnectionListener, ConnectionPointComposite.Client
{

	public static interface Listener
	{
		public void siteConnectionChanged(ISiteConnection site);
	}

	private Composite fMain;
	private ComboViewer fSitesViewer;
	private Button fEditButton;
	private Button fSaveAsButton;
	private ConnectionPointComposite fSource;
	private ConnectionPointComposite fTarget;
	private Button fTransferSyncButton;
	private Button fTransferRightButton;
	private Button fTransferLeftButton;

	private ISiteConnection fSelectedSite;
	private List<Listener> fListeners;
	private ConnectionPointComposite focusedConnection;

	public FTPManagerComposite(Composite parent)
	{
		fListeners = new ArrayList<Listener>();
		fMain = createControl(parent);
		SyncingPlugin.getSiteConnectionManager().addListener(this);
	}

	public void addListener(Listener listener)
	{
		if (!fListeners.contains(listener))
		{
			fListeners.add(listener);
		}
	}

	public void removeListener(Listener listener)
	{
		fListeners.remove(listener);
	}

	public void dispose()
	{
		fSelectedSite = null;
		fListeners.clear();
		SyncingPlugin.getSiteConnectionManager().removeListener(this);
	}

	public Control getControl()
	{
		return fMain;
	}

	public void setFocus()
	{
		fMain.setFocus();
	}

	public void setSelectedSite(ISiteConnection siteConnection)
	{
		if (siteConnection == fSelectedSite)
		{
			return;
		}
		fSelectedSite = siteConnection;
		if (siteConnection == null)
		{
			fSitesViewer.setSelection(StructuredSelection.EMPTY);
			fSource.setConnectionPoint(null);
			fTarget.setConnectionPoint(null);
		}
		else
		{
			if (siteConnection == DefaultSiteConnection.getInstance())
			{
				fSitesViewer.setInput(new ISiteConnection[] { siteConnection });
			}
			else
			{
				fSitesViewer.setInput(SyncingPlugin.getSiteConnectionManager().getSiteConnections());
			}
			fSitesViewer.setSelection(new StructuredSelection(siteConnection));
			fSource.setConnectionPoint(siteConnection.getSource());
			fTarget.setConnectionPoint(siteConnection.getDestination());
		}
		fireSiteConnectionChanged(fSelectedSite);
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();

		if (source == fEditButton)
		{
			// opens the connection manager with the current connection selected
			SiteConnectionsEditorDialog dlg = new SiteConnectionsEditorDialog(fMain.getShell());
			dlg.setSelection((ISiteConnection) ((IStructuredSelection) fSitesViewer.getSelection()).getFirstElement());
			dlg.open();
		}
		else if (source == fSaveAsButton)
		{
			saveAs();
		}
		else if (source == fTransferSyncButton)
		{
			syncSourceToDestination();
		}
		else if (source == fTransferRightButton)
		{
			transferSourceToDestination();
		}
		else if (source == fTransferLeftButton)
		{
			transferDestinationToSource();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.syncing.core.events.ISiteConnectionListener#siteConnectionChanged(com.aptana.ide.syncing.core.
	 * events.SiteConnectionEvent)
	 */
	public void siteConnectionChanged(final SiteConnectionEvent event)
	{
		switch (event.getKind())
		{
			case SiteConnectionEvent.POST_ADD:
			case SiteConnectionEvent.POST_DELETE:
				if (fMain.isDisposed())
				{
					return;
				}
				fMain.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						// updates the drop-down list
						if (fSelectedSite != DefaultSiteConnection.getInstance())
						{
							ISelection selection = fSitesViewer.getSelection();
							fSitesViewer.setInput(SyncingPlugin.getSiteConnectionManager().getSiteConnections());
							fSitesViewer.setSelection(selection);
						}
					}
				});
				break;
			case SiteConnectionEvent.POST_CHANGE:
				if (fMain.isDisposed())
				{
					return;
				}
				fMain.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						ISiteConnection siteConnection = event.getSiteConnection();
						fSource.setConnectionPoint(siteConnection.getSource());
						fTarget.setConnectionPoint(siteConnection.getDestination());
					}
				});
				break;
		}
	}

	public void transfer(ConnectionPointComposite source)
	{
		if (source == fSource)
		{
			transferSourceToDestination();
		}
		else if (source == fTarget)
		{
			transferDestinationToSource();
		}
	}

	protected Composite createControl(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		main.setLayout(layout);

		Composite top = createSiteInfo(main);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite middle = createSitePresentation(main);
		middle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return main;
	}

	private Composite createSiteInfo(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);

		Label label = new Label(main, SWT.NONE);
		label.setText(Messages.FTPManagerComposite_LBL_Sites);

		fSitesViewer = new ComboViewer(main, SWT.READ_ONLY);
		fSitesViewer.setContentProvider(new ArrayContentProvider());
		fSitesViewer.setLabelProvider(new SitesLabelProvider());
		fSitesViewer.setInput(SyncingPlugin.getSiteConnectionManager().getSiteConnections());
		fSitesViewer.getControl().setLayoutData(GridDataFactory.swtDefaults().hint(250, SWT.DEFAULT).create());
		fSitesViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				setSelectedSite((ISiteConnection) ((IStructuredSelection) event.getSelection()).getFirstElement());
			}
		});

		fEditButton = new Button(main, SWT.PUSH);
		fEditButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		fEditButton.setToolTipText(Messages.FTPManagerComposite_TTP_Edit);
		fEditButton.addSelectionListener(this);

		fSaveAsButton = new Button(main, SWT.PUSH);
		fSaveAsButton.setText(StringUtil.ellipsify(Messages.FTPManagerComposite_LBL_SaveAs));
		fSaveAsButton.setToolTipText(Messages.FTPManagerComposite_TTP_SaveAs);
		fSaveAsButton.addSelectionListener(this);

		return main;
	}

	private Composite createSitePresentation(Composite parent)
	{
		final Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(5, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		main.setLayout(layout);

		// source end point
		fSource = new ConnectionPointComposite(main, Messages.FTPManagerComposite_LBL_Source, this);
		fSource.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		fSource.addTreeFocusListener(new FocusListener()
		{
			
			public void focusLost(FocusEvent e)
			{
				focusedConnection = null;
			}
			
			public void focusGained(FocusEvent e)
			{
				focusedConnection = fSource;
			}
		});

		final Sash leftSash = new Sash(main, SWT.VERTICAL);
		leftSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		// transfer arrows
		final Composite directions = new Composite(main, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		directions.setLayout(layout);
		directions.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));

		fTransferRightButton = new Button(directions, SWT.NONE);
		fTransferRightButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_TOOL_FORWARD));
		fTransferRightButton.setToolTipText(Messages.FTPManagerComposite_TTP_TransferRight);
		fTransferRightButton.setLayoutData(new GridData(SWT.CENTER, SWT.END, true, true));
		fTransferRightButton.addSelectionListener(this);
		fTransferLeftButton = new Button(directions, SWT.NONE);
		fTransferLeftButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK));
		fTransferLeftButton.setToolTipText(Messages.FTPManagerComposite_TTP_TransferLeft);
		fTransferLeftButton.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, true));
		fTransferLeftButton.addSelectionListener(this);

		fTransferSyncButton = new Button(directions, SWT.NONE);
		fTransferSyncButton.setImage(SyncingUIPlugin.getImage("icons/full/obj16/sync_both.gif")); //$NON-NLS-1$
		fTransferSyncButton.setToolTipText(Messages.FTPManagerComposite_TTP_Synchronize);
		fTransferSyncButton.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, true));
		fTransferSyncButton.addSelectionListener(this);

		final Sash rightSash = new Sash(main, SWT.VERTICAL);
		rightSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		// destination end point
		fTarget = new ConnectionPointComposite(main, Messages.FTPManagerComposite_LBL_Target, this);
		fTarget.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		fTarget.addTreeFocusListener(new FocusListener()
		{
			
			public void focusLost(FocusEvent e)
			{
				focusedConnection = null;
			}
			
			public void focusGained(FocusEvent e)
			{
				focusedConnection = fTarget;
			}
		});
		
		leftSash.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent event)
			{
				if (event.detail != SWT.DRAG)
				{
					layout();
				}
			}

			private void layout()
			{
				Rectangle overallBounds = main.getBounds();
				Rectangle leftSashBounds = leftSash.getBounds();
				Rectangle middleBounds = directions.getBounds();
				Rectangle rightSashBounds = rightSash.getBounds();

				fSource.getControl().setBounds(0, 0, leftSashBounds.x, overallBounds.height);
				int x = leftSashBounds.x + leftSashBounds.width;
				directions.setBounds(x, 0, middleBounds.width, overallBounds.height);
				x += middleBounds.width;
				rightSash.setBounds(x, 0, rightSashBounds.width, overallBounds.height);
				x += rightSashBounds.width;
				fTarget.getControl().setBounds(x, 0, overallBounds.width - x, overallBounds.height);
			}
		});

		rightSash.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent event)
			{
				if (event.detail != SWT.DRAG)
				{
					layout();
				}
			}

			private void layout()
			{
				Rectangle overallBounds = main.getBounds();
				Rectangle leftSashBounds = leftSash.getBounds();
				Rectangle middleBounds = directions.getBounds();
				Rectangle rightSashBounds = rightSash.getBounds();

				int x = rightSashBounds.x + rightSashBounds.width;
				fTarget.getControl().setBounds(x, 0, overallBounds.width - x, overallBounds.height);
				x = rightSashBounds.x - middleBounds.width;
				directions.setBounds(x, 0, middleBounds.width, overallBounds.height);
				x -= leftSashBounds.width;
				leftSash.setBounds(x, 0, leftSashBounds.width, overallBounds.height);
				fSource.getControl().setBounds(0, 0, x, overallBounds.height);
			}
		});

		return main;
	}

	private void saveAs()
	{
		// builds the initial value from the current selection
		ISiteConnection selection = (ISiteConnection) ((IStructuredSelection) fSitesViewer.getSelection())
				.getFirstElement();
		String initialValue = ""; //$NON-NLS-1$
		if (selection != null)
		{
			initialValue = "Copy of " + selection.getName(); //$NON-NLS-1$
		}
		InputDialog dialog = new InputDialog(fMain.getShell(), Messages.FTPManagerComposite_NameInput_Title,
				Messages.FTPManagerComposite_NameInput_Message, initialValue, new IInputValidator()
				{

					public String isValid(String newText)
					{
						if (newText.length() == 0)
						{
							return Messages.FTPManagerComposite_ERR_EmptyName;
						}

						for (ISiteConnection i : SyncingPlugin.getSiteConnectionManager().getSiteConnections())
						{
							if (newText.equals(i.getName()))
							{
								return MessageFormat.format(Messages.FTPManagerComposite_ERR_NameExists, newText);
							}
						}
						return null;
					}

				});
		if (dialog.open() != Window.OK)
		{
			return;
		}

		String name = dialog.getValue();
		ISiteConnection newSite = null;
		if (fSelectedSite != null)
		{
			try
			{
				newSite = SyncingPlugin.getSiteConnectionManager().cloneSiteConnection(fSelectedSite);
			}
			catch (CoreException e)
			{
				UIUtils.showErrorMessage(Messages.FTPManagerComposite_ERR_CreateNewSiteFailed, e);
				return;
			}
		}
		else
		{
			newSite = SyncingPlugin.getSiteConnectionManager().createSiteConnection();
		}
		newSite.setName(name);
		SyncingPlugin.getSiteConnectionManager().addSiteConnection(newSite);

		// opens the connection in a new editor
		EditorUtils.openConnectionEditor(newSite);
	}

	private void syncSourceToDestination()
	{
		IConnectionPoint source = fSelectedSite.getSource();
		IConnectionPoint dest = fSelectedSite.getDestination();
		ConnectionPointSyncPair cpsp = new ConnectionPointSyncPair(source, dest);

		SmartSyncDialog dialog;
		try
		{
			if (focusedConnection == null)
			{
				IFileStore sourceStore = SyncUtils.getFileStore(fSource.getCurrentInput());
				IFileStore targetStore = SyncUtils.getFileStore(fTarget.getCurrentInput());
				dialog = new SmartSyncDialog(UIUtils.getActiveShell(), source, dest, sourceStore, targetStore,
						source.getName(), dest.getName());
			}
			else
			{
				IFileStore[] sourceStores = SyncUtils.getFileStores(focusedConnection.getSelectedElements());
				dialog = new SmartSyncDialog(UIUtils.getActiveShell(), cpsp, sourceStores);
			}

			dialog.open();
			dialog.setHandler(new SyncEventHandlerAdapter()
			{
				public void syncDone(VirtualFileSyncPair item)
				{
					IOUIPlugin.refreshNavigatorView(fSource.getCurrentInput());
					IOUIPlugin.refreshNavigatorView(fTarget.getCurrentInput());
					UIUtils.getDisplay().asyncExec(new Runnable()
					{

						public void run()
						{
							fSource.refresh();
							fTarget.refresh();
						}
					});
				}
			});
		}
		catch (CoreException e)
		{
			ErrorDialog.openError(UIUtils.getActiveShell(), Messages.FTPManagerComposite_SyncErrorDialog_Title,
					Messages.FTPManagerComposite_SyncErrorDialog_Message, e.getStatus());
		}
	}

	private void transferSourceToDestination()
	{
		UploadAction action = new UploadAction();
		action.setActivePart(null, UIUtils.getActivePart());
		action.setSelectedSite(fSelectedSite);
		action.setSelection(new StructuredSelection(fSource.getSelectedElements()));
		action.setSourceRoot(SyncUtils.getFileStore(fSource.getCurrentInput()));
		action.setDestinationRoot(SyncUtils.getFileStore(fTarget.getCurrentInput()));
		action.addJobListener(new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				if (event.getResult() == Status.CANCEL_STATUS)
				{
					return;
				}
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						fTarget.refresh();
					}
				});
			}
		});
		action.run(null);
	}

	private void transferDestinationToSource()
	{
		DownloadAction action = new DownloadAction();
		action.setActivePart(null, UIUtils.getActivePart());
		action.setSelectedSite(fSelectedSite);
		action.setSelection(new StructuredSelection(fTarget.getSelectedElements()), false);
		action.setSourceRoot(SyncUtils.getFileStore(fSource.getCurrentInput()));
		action.setDestinationRoot(SyncUtils.getFileStore(fTarget.getCurrentInput()));
		action.addJobListener(new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				if (event.getResult() == Status.CANCEL_STATUS)
				{
					return;
				}
				UIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						fSource.refresh();
					}
				});
			}
		});
		action.run(null);
	}

	private void fireSiteConnectionChanged(ISiteConnection site)
	{
		for (Listener listener : fListeners)
		{
			listener.siteConnectionChanged(site);
		}
	}

	private class SitesLabelProvider extends LabelProvider
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			if (element instanceof ISiteConnection)
			{
				return ((ISiteConnection) element).getName();
			}
			return super.getText(element);
		}
	}
}

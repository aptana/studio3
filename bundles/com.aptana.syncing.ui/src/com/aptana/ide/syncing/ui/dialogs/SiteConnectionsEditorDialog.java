/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.syncing.ui.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.DefaultSiteConnection;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.internal.SiteConnectionPropertiesWidget;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ui.IDialogConstants;
import com.aptana.ui.UIPlugin;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class SiteConnectionsEditorDialog extends TitleAreaDialog implements SiteConnectionPropertiesWidget.Client
{

	private ISiteConnection initialSelection;

	private ListViewer sitesViewer;
	private Button addButton;
	private Button removeButton;
	private SiteConnectionPropertiesWidget sitePropertiesWidget;

	private List<ISiteConnection> sites = new ArrayList<ISiteConnection>();

	/**
	 * @param parentShell
	 */
	public SiteConnectionsEditorDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		setHelpAvailable(false);

		sites.add(DefaultSiteConnection.getInstance());
		sites.addAll(Arrays.asList(SyncingPlugin.getSiteConnectionManager().getSiteConnections()));

		setSelection(DefaultSiteConnection.getInstance());
	}

	public void setCreateNew(String name, IAdaptable source, IAdaptable destination)
	{
		IConnectionPoint sourceConnection = SyncUtils.findOrCreateConnectionPointFor(source);
		IConnectionPoint destinationConnection = SyncUtils.findOrCreateConnectionPointFor(destination);

		ISiteConnection siteConnection = SyncingPlugin.getSiteConnectionManager().createSiteConnection();
		siteConnection.setName(createUniqueSiteName(name));
		siteConnection.setSource(sourceConnection);
		siteConnection.setDestination(destinationConnection);
		sites.add(siteConnection);
		if (sitesViewer != null)
		{
			sitesViewer.refresh();
		}
		setSelection(siteConnection);
	}

	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.SiteConnectionsEditorDialog_DialogTitle);
	}

	protected Control createDialogArea(Composite parent)
	{
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		setTitle(Messages.SiteConnectionsEditorDialog_Title);
		setMessage(Messages.SiteConnectionsEditorDialog_Message);

		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory
				.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN),
						convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING),
						convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING)).create());

		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLayoutData(GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 400).grab(true, true).create());

		/* column 1 - the list of connections */
		Group group = new Group(sashForm, SWT.NONE);
		group.setText(Messages.SiteConnectionsEditorDialog_LBL_ConnectionGroup);
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		sitesViewer = new ListViewer(group, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sitesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		sitesViewer.setContentProvider(ArrayContentProvider.getInstance());
		sitesViewer.setLabelProvider(new SitesLabelProvider());
		sitesViewer.setComparator(new SitesSorter());
		sitesViewer.setInput(sites);

		addButton = new Button(group, SWT.PUSH);
		addButton.setLayoutData(GridDataFactory.swtDefaults().create());
		addButton.setImage(SWTUtils.getImage(UIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$
		addButton.setToolTipText(StringUtil.ellipsify(CoreStrings.ADD));

		removeButton = new Button(group, SWT.PUSH);
		removeButton.setLayoutData(GridDataFactory.swtDefaults().create());
		removeButton.setImage(SWTUtils.getImage(UIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$
		removeButton.setToolTipText(CoreStrings.REMOVE);

		/* column 2 - the details of the selected connection */
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		sitePropertiesWidget = new SiteConnectionPropertiesWidget(composite, SWT.NONE, this);
		sitePropertiesWidget.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		sitePropertiesWidget.setSource(null);

		sashForm.setWeights(new int[] { 30, 70 });

		/* -- */
		sitesViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				ISiteConnection selection = (ISiteConnection) ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				if (selection != sitePropertiesWidget.getSource())
				{
					if (doSelectionChange())
					{
						sitePropertiesWidget.setSource(selection);
					}
					else
					{
						sitesViewer.setSelection(new StructuredSelection(sitePropertiesWidget.getSource()), true);
					}
				}
				removeButton.setEnabled(!event.getSelection().isEmpty()
						&& selection != DefaultSiteConnection.getInstance());
			}
		});

		MenuManager menuManager = new MenuManager();
		createActions(menuManager);
		sitesViewer.getControl().setMenu(menuManager.createContextMenu(sitesViewer.getControl()));

		addButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (doSelectionChange())
				{
					setCreateNew(Messages.SiteConnectionsEditorDialog_LBL_NewConnection, null, null);
				}
				else
				{
					sitesViewer.setSelection(new StructuredSelection(sitePropertiesWidget.getSource()), true);
				}
			}
		});
		removeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (!sitesViewer.getSelection().isEmpty())
				{
					if (MessageDialog.openConfirm(getShell(), Messages.SiteConnectionsEditorDialog_DeleteConfirm_Title,
							Messages.SiteConnectionsEditorDialog_DeleteConfirm_Message))
					{
						ISiteConnection selection = (ISiteConnection) ((IStructuredSelection) sitesViewer
								.getSelection()).getFirstElement();
						int newSelectionIndex = sitesViewer.getList().getSelectionIndex() - 1;

						SyncingPlugin.getSiteConnectionManager().removeSiteConnection(selection);
						sites.remove(selection);
						sitePropertiesWidget.setSource(null);
						sitesViewer.refresh();
						if (newSelectionIndex > -1 && newSelectionIndex < sitesViewer.getList().getItemCount())
						{
							setSelection((newSelectionIndex == 0) ? DefaultSiteConnection.getInstance() : sites
									.get(newSelectionIndex - 1));
						}
					}
				}
			}
		});

		return dialogArea;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		try
		{
			return super.createContents(parent);
		}
		finally
		{
			if (initialSelection != null)
			{
				sitesViewer.setSelection(new StructuredSelection(initialSelection), true);
			}
		}
	}

	protected void createActions(IMenuManager menuManager)
	{
		menuManager.add(new Action(Messages.SiteConnectionsEditorDialog_LBL_Duplicate)
		{
			@Override
			public void run()
			{
				ISiteConnection siteConnection = (ISiteConnection) ((IStructuredSelection) sitesViewer.getSelection())
						.getFirstElement();
				if (siteConnection != null && doSelectionChange())
				{
					try
					{
						ISiteConnection newSite = SyncingPlugin.getSiteConnectionManager().cloneSiteConnection(
								siteConnection);
						newSite.setName(MessageFormat.format("Copy of {0}", siteConnection.getName())); //$NON-NLS-1$
						sites.add(newSite);
						sitesViewer.refresh();
						sitesViewer.setSelection(new StructuredSelection(newSite), true);
					}
					catch (CoreException e)
					{
						UIUtils.showErrorMessage(Messages.SiteConnectionsEditorDialog_ERR_Duplicate, e);
					}
				}
			}
		});
	}

	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.APPLY_ID, IDialogConstants.APPLY_LABEL, false);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected boolean doSelectionChange()
	{
		if (sitePropertiesWidget.isChanged())
		{
			MessageDialog dlg = new MessageDialog(getShell(), Messages.SiteConnectionsEditorDialog_SaveConfirm_Title,
					null, MessageFormat.format(Messages.SiteConnectionsEditorDialog_SaveConfirm_Message,
							sitePropertiesWidget.getSource().getName()), MessageDialog.QUESTION, new String[] {
							IDialogConstants.NO_LABEL, IDialogConstants.YES_LABEL, IDialogConstants.CANCEL_LABEL }, 1);
			switch (dlg.open())
			{
				case 1:
					if (sitePropertiesWidget.applyChanges())
					{
						break;
					}
					else
					{
						// unresolved error exists in the current selection
						MessageDialog.openWarning(getShell(),
								Messages.SiteConnectionsEditorDialog_UnresolvedWarning_Title,
								Messages.SiteConnectionsEditorDialog_UnresolvedWarning_Message);
					}
				case 2:
					return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String newErrorMessage)
	{
		super.setErrorMessage(newErrorMessage);
		boolean hasError = (newErrorMessage != null);
		Button button = getButton(IDialogConstants.APPLY_ID);
		if (button != null)
		{
			button.setEnabled(!hasError);
		}
		button = getButton(IDialogConstants.OK_ID);
		if (button != null)
		{
			button.setEnabled(!hasError);
		}
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == IDialogConstants.APPLY_ID)
		{
			applyPressed();
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void cancelPressed()
	{
		sitePropertiesWidget.cancelChanges();
		super.cancelPressed();
	}

	@Override
	protected void okPressed()
	{
		if (applyPressed())
		{
			super.okPressed();
		}
	}

	protected boolean applyPressed()
	{
		boolean applied = !sitePropertiesWidget.isChanged() || sitePropertiesWidget.applyChanges();
		if (applied)
		{
			sitesViewer.refresh();
		}

		ISiteConnection siteConnection = sitePropertiesWidget.getSource();
		if (siteConnection != null)
		{
			if (siteConnection != DefaultSiteConnection.getInstance())
			{
				SyncingPlugin.getSiteConnectionManager().addSiteConnection(siteConnection);
			}
			SyncingPlugin.getSiteConnectionManager().siteConnectionChanged(siteConnection);
		}

		return applied;
	}

	public void setSelection(ISiteConnection selection)
	{
		this.initialSelection = selection;
		if (sitesViewer != null)
		{
			sitesViewer.setSelection(new StructuredSelection(selection), true);
		}
	}

	private static String createUniqueSiteName(String baseName)
	{
		Pattern pattern = Pattern.compile("^(.*) (\\d+)$"); //$NON-NLS-1$
		Matcher matcher = pattern.matcher(baseName);
		if (matcher.matches())
		{
			baseName = matcher.group(1);
		}
		int lastIndex = Integer.MIN_VALUE;
		for (ISiteConnection i : SyncingPlugin.getSiteConnectionManager().getSiteConnections())
		{
			String siteName = i.getName();
			if (siteName.startsWith(baseName))
			{
				if (siteName.equals(baseName) && lastIndex == Integer.MIN_VALUE)
				{
					lastIndex = 1;
				}
				matcher = pattern.matcher(siteName);
				if (matcher.matches())
				{
					try
					{
						lastIndex = Math.max(lastIndex, Integer.parseInt(matcher.group(2)));
					}
					catch (NumberFormatException e)
					{
					}
				}
			}
		}
		if (lastIndex == Integer.MIN_VALUE)
		{
			return baseName;
		}
		return MessageFormat.format("{0} {1}", baseName, lastIndex + 1); //$NON-NLS-1$
	}

	private static class SitesLabelProvider extends LabelProvider
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

	private static class SitesSorter extends ViewerComparator
	{
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
		 */
		@Override
		public int category(Object element)
		{
			if (element == DefaultSiteConnection.getInstance())
			{
				return 0;
			}
			return 1;
		}
	}
}

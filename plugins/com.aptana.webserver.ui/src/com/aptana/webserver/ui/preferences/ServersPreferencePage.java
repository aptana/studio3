/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ListDialog;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.PropertyDialogsRegistry;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServerManager;
import com.aptana.webserver.core.IServerType;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.WebServerUIPlugin;

/**
 * @author Max Stepanov
 */
public class ServersPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private ListViewer viewer;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		viewer = new ListViewer(composite, SWT.SINGLE | SWT.BORDER);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewer.setContentProvider(new ArrayContentProvider()
		{
			@Override
			public Object[] getElements(Object inputElement)
			{
				if (inputElement instanceof IServerManager)
				{
					inputElement = ((IServerManager) inputElement).getServers(); // $codepro.audit.disable
																					// questionableAssignment
				}
				return super.getElements(inputElement);
			}

		});
		viewer.setLabelProvider(new LabelProvider()
		{
			@Override
			public Image getImage(Object element)
			{
				return null; // TODO: use ImageAssociations
			}

			@Override
			public String getText(Object element)
			{
				if (element instanceof IServer)
				{
					return ((IServer) element).getName();
				}
				return super.getText(element);
			}

		});
		viewer.setInput(WebServerCorePlugin.getDefault().getServerManager());

		Composite buttonContainer = new Composite(composite, SWT.NONE);
		buttonContainer.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		buttonContainer.setLayout(GridLayoutFactory.swtDefaults().create());

		Button newButton = new Button(buttonContainer, SWT.PUSH);
		newButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
		newButton.setLayoutData(GridDataFactory
				.swtDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(Math.max(newButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x,
						convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH)), SWT.DEFAULT).create());

		final Button editButton = new Button(buttonContainer, SWT.PUSH);
		editButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		editButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());

		final Button deleteButton = new Button(buttonContainer, SWT.PUSH);
		deleteButton.setText(CoreStrings.DELETE);
		deleteButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());

		newButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				ListDialog dlg = new ListDialog(getShell());
				dlg.setContentProvider(ArrayContentProvider.getInstance());
				dlg.setLabelProvider(new LabelProvider()
				{
					@Override
					public Image getImage(Object element)
					{
						return null; // TODO: use ImageAssociations
					}

					@Override
					public String getText(Object element)
					{
						if (element instanceof IServerType)
						{
							return ((IServerType) element).getName();
						}
						return super.getText(element);
					}
				});
				dlg.setInput(WebServerCorePlugin.getDefault().getServerManager().getServerTypes());
				dlg.setTitle(Messages.ServersPreferencePage_Title);
				Object[] result;
				if (dlg.open() == Window.OK && (result = dlg.getResult()) != null && result.length == 1)
				{ // $codepro.audit.disable assignmentInCondition
					String typeId = ((IServerType) result[0]).getId();
					try
					{
						IServer newConfiguration = WebServerCorePlugin.getDefault().getServerManager()
								.createServer(typeId);
						if (newConfiguration != null)
						{
							if (editServerConfiguration(newConfiguration))
							{
								WebServerCorePlugin.getDefault().getServerManager().add(newConfiguration);
								viewer.refresh();
							}
						}
					}
					catch (CoreException e)
					{
						IdeLog.logError(WebServerUIPlugin.getDefault(), e);
					}
				}
			}

		});
		editButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IServer selection = (IServer) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (selection != null && editServerConfiguration(selection))
				{
					viewer.refresh();
				}
			}

		});
		deleteButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IServer selection = (IServer) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (selection != null
						&& MessageDialog.openQuestion(getShell(), Messages.ServersPreferencePage_DeletePrompt_Title,
								Messages.ServersPreferencePage_DeletePrompt_Message))
				{
					WebServerCorePlugin.getDefault().getServerManager().remove(selection);
					viewer.refresh();
				}
			}

		});

		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				IServer selection = (IServer) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (selection != null && editServerConfiguration(selection))
				{
					viewer.refresh();
				}
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				boolean hasSelection = !event.getSelection().isEmpty();
				editButton.setEnabled(hasSelection);
				deleteButton.setEnabled(hasSelection);
			}
		});
		viewer.setSelection(StructuredSelection.EMPTY);

		return composite;
	}

	private boolean editServerConfiguration(IServer serverConfiguration)
	{
		try
		{
			Dialog dlg = PropertyDialogsRegistry.getInstance().createPropertyDialog(serverConfiguration,
					new SameShellProvider(getShell()));
			if (dlg != null)
			{
				if (dlg instanceof IPropertyDialog)
				{
					((IPropertyDialog) dlg).setPropertySource(serverConfiguration);
				}
				return dlg.open() == Window.OK;
			}
		}
		catch (CoreException e)
		{
			UIUtils.showErrorMessage("Failed to open server preferences dialog", e); //$NON-NLS-1$
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(WebServerUIPlugin.getDefault().getPreferenceStore());
	}

}

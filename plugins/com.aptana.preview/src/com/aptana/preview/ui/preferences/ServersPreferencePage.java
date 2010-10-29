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

package com.aptana.preview.ui.preferences;

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
import com.aptana.core.util.StringUtil;
import com.aptana.preview.Activator;
import com.aptana.preview.server.AbstractWebServerConfiguration;
import com.aptana.preview.server.ServerConfigurationManager;
import com.aptana.preview.server.ServerConfigurationManager.ConfigurationType;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.PropertyDialogsRegistry;
import com.aptana.ui.UIUtils;

/**
 * @author Max Stepanov
 * 
 */
public class ServersPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private ListViewer viewer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		viewer = new ListViewer(composite, SWT.SINGLE | SWT.BORDER);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		viewer.setContentProvider(new ArrayContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof ServerConfigurationManager) {
					inputElement = ((ServerConfigurationManager) inputElement).getServerConfigurations();
				}
				return super.getElements(inputElement);
			}

		});
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null; // TODO: use ImageAssociations
			}

			@Override
			public String getText(Object element) {
				if (element instanceof AbstractWebServerConfiguration) {
					return ((AbstractWebServerConfiguration) element).getName();
				}
				return super.getText(element);
			}

		});
		viewer.setInput(ServerConfigurationManager.getInstance());

		Composite buttonContainer = new Composite(composite, SWT.NONE);
		buttonContainer.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		buttonContainer.setLayout(GridLayoutFactory.swtDefaults().create());

		Button newButton = new Button(buttonContainer, SWT.PUSH);
		newButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
		newButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(
				Math.max(newButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x,
						convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH)), SWT.DEFAULT).create());

		final Button editButton = new Button(buttonContainer, SWT.PUSH);
		editButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		editButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());

		final Button deleteButton = new Button(buttonContainer, SWT.PUSH);
		deleteButton.setText(CoreStrings.DELETE);
		deleteButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());

		newButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ListDialog dlg = new ListDialog(getShell());
				dlg.setContentProvider(new ArrayContentProvider());
				dlg.setLabelProvider(new LabelProvider() {
					@Override
					public Image getImage(Object element) {
						return null; // TODO: use ImageAssociations
					}

					@Override
					public String getText(Object element) {
						if (element instanceof ConfigurationType) {
							return ((ConfigurationType) element).getName();
						}
						return super.getText(element);
					}
				});
				dlg.setInput(ServerConfigurationManager.getInstance().getConfigurationTypes());
				dlg.setTitle(Messages.ServersPreferencePage_Title);
				Object[] result;
				if (dlg.open() == Window.OK && (result = dlg.getResult()) != null && result.length == 1) {
					String typeId = ((ConfigurationType) result[0]).getId();
					try {
						AbstractWebServerConfiguration newConfiguration = ServerConfigurationManager.getInstance()
								.createServerConfiguration(typeId);
						if (newConfiguration != null) {
							if (editServerConfiguration(newConfiguration)) {
								ServerConfigurationManager.getInstance().addServerConfiguration(newConfiguration);
								viewer.refresh();
							}
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}

		});
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AbstractWebServerConfiguration selection = (AbstractWebServerConfiguration) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selection != null && editServerConfiguration(selection)) {
					viewer.refresh();
				}
			}

		});
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AbstractWebServerConfiguration selection = (AbstractWebServerConfiguration) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selection != null
						&& MessageDialog.openQuestion(getShell(), Messages.ServersPreferencePage_DeletePrompt_Title,
								Messages.ServersPreferencePage_DeletePrompt_Message)) {
					ServerConfigurationManager.getInstance().removeServerConfiguration(selection);
					viewer.refresh();
				}
			}

		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				AbstractWebServerConfiguration selection = (AbstractWebServerConfiguration) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();
				if (selection != null && editServerConfiguration(selection)) {
					viewer.refresh();
				}
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				boolean hasSelection = !event.getSelection().isEmpty();
				editButton.setEnabled(hasSelection);
				deleteButton.setEnabled(hasSelection);
			}
		});
		viewer.setSelection(StructuredSelection.EMPTY);

		return composite;
	}

	private boolean editServerConfiguration(AbstractWebServerConfiguration serverConfiguration) {
		try {
			Dialog dlg = PropertyDialogsRegistry.getInstance().createPropertyDialog(serverConfiguration,
					new SameShellProvider(getShell()));
			if (dlg != null) {
				if (dlg instanceof IPropertyDialog) {
					((IPropertyDialog) dlg).setPropertySource(serverConfiguration);
				}
				return dlg.open() == Window.OK;
			}
		} catch (CoreException e) {
			UIUtils.showErrorMessage("Failed to open server preferences dialog", e); //$NON-NLS-1$
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

}

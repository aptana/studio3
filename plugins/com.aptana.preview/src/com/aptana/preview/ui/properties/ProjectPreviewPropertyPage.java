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
package com.aptana.preview.ui.properties;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.PropertyPage;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.preview.Activator;
import com.aptana.preview.ProjectPreviewUtil;
import com.aptana.preview.server.AbstractWebServerConfiguration;
import com.aptana.preview.server.ServerConfigurationManager;
import com.aptana.preview.server.ServerConfigurationManager.ConfigurationType;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.PropertyDialogsRegistry;
import com.aptana.ui.UIUtils;

/**
 * @author Max Stepanov
 * @author Michael Xia
 */
public class ProjectPreviewPropertyPage extends PropertyPage implements IWorkbenchPropertyPage, SelectionListener
{

	private enum Type
	{
		NONE, SERVER
	};

	private Button fNoSettingRadio;
	private Button fServerRadio;
	private ComboViewer fServersCombo;
	private Button fEditButton;
	private Button fNewButton;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(GridLayoutFactory.fillDefaults().create());

		fNoSettingRadio = new Button(container, SWT.RADIO);
		fNoSettingRadio.setText(Messages.ProjectPreviewPropertyPage_LBL_NoSettings);
		fNoSettingRadio.addSelectionListener(this);

		Composite serverComposite = new Composite(container, SWT.NONE);
		serverComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());
		serverComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());

		fServerRadio = new Button(serverComposite, SWT.RADIO);
		fServerRadio.setText(Messages.ProjectPreviewPropertyPage_Server_Label);
		fServerRadio.addSelectionListener(this);

		fServersCombo = new ComboViewer(serverComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fServersCombo.getControl().setLayoutData(
				GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		fServersCombo.setContentProvider(new ArrayContentProvider());
		fServersCombo.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				if (element instanceof AbstractWebServerConfiguration)
				{
					return ((AbstractWebServerConfiguration) element).getName();
				}
				return super.getText(element);
			}
		});

		fEditButton = new Button(serverComposite, SWT.PUSH);
		fEditButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		fEditButton.addSelectionListener(this);

		fNewButton = new Button(serverComposite, SWT.PUSH);
		fNewButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
		fNewButton.addSelectionListener(this);

		updateServersContent();
		// loads the existing setting for the project
		AbstractWebServerConfiguration server = ProjectPreviewUtil.getServerConfiguration(getProject());
		if (server == null)
		{
			setSelectedType(Type.NONE);
		}
		else
		{
			setSelectedType(Type.SERVER);
			fServersCombo.setSelection(new StructuredSelection(server));
		}

		return container;
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fNoSettingRadio)
		{
			setSelectedType(Type.NONE);
		}
		else if (source == fServerRadio)
		{
			setSelectedType(Type.SERVER);
		}
		else if (source == fEditButton)
		{
			editSelectedServer();
		}
		else if (source == fNewButton)
		{
			createNewServer();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	@Override
	public boolean performOk()
	{
		// saves the settings for the project
		ProjectPreviewUtil.setServerConfiguration(getProject(), getSelectedServer());
		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		setSelectedType(Type.NONE);
		super.performDefaults();
	}

	private void createNewServer()
	{
		ListDialog dialog = new ListDialog(getShell());
		dialog.setContentProvider(new ArrayContentProvider());
		dialog.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				if (element instanceof ConfigurationType)
				{
					return ((ConfigurationType) element).getName();
				}
				return super.getText(element);
			}
		});
		dialog.setInput(ServerConfigurationManager.getInstance().getConfigurationTypes());
		dialog.setTitle(Messages.ProjectPreviewPropertyPage_ChooseServerType);

		Object[] result;
		if (dialog.open() == Window.OK && (result = dialog.getResult()) != null && result.length == 1)
		{
			String typeId = ((ConfigurationType) result[0]).getId();
			try
			{
				AbstractWebServerConfiguration newConfiguration = ServerConfigurationManager.getInstance()
						.createServerConfiguration(typeId);
				if (newConfiguration != null)
				{
					if (editServerConfiguration(newConfiguration))
					{
						ServerConfigurationManager.getInstance().addServerConfiguration(newConfiguration);
						updateServersContent();
						fServersCombo.setSelection(new StructuredSelection(newConfiguration));
						// forces an update of widget enablements
						setSelectedType(Type.SERVER);
					}
				}
			}
			catch (CoreException e)
			{
				Activator.log(Messages.ProjectPreviewPropertyPage_ERR_FailToCreateServer, e);
			}
		}
	}

	private void editSelectedServer()
	{
		Object selection = ((IStructuredSelection) fServersCombo.getSelection()).getFirstElement();
		if (selection instanceof AbstractWebServerConfiguration
				&& editServerConfiguration((AbstractWebServerConfiguration) selection))
		{
			fServersCombo.refresh();
		}
	}

	private boolean editServerConfiguration(AbstractWebServerConfiguration serverConfiguration)
	{
		try
		{
			Dialog dialog = PropertyDialogsRegistry.getInstance().createPropertyDialog(serverConfiguration,
					new SameShellProvider(getShell()));
			if (dialog != null)
			{
				if (dialog instanceof IPropertyDialog)
				{
					((IPropertyDialog) dialog).setPropertySource(serverConfiguration);
				}
				return dialog.open() == Window.OK;
			}
		}
		catch (CoreException e)
		{
			UIUtils.showErrorMessage(Messages.ProjectPreviewPropertyPage_ERR_FailToOpenServerDialog, e);
		}
		return false;
	}

	private IProject getProject()
	{
		return (IProject) getElement().getAdapter(IProject.class);
	}

	private AbstractWebServerConfiguration getSelectedServer()
	{
		if (fNoSettingRadio.getSelection())
		{
			return null;
		}
		Object selection = ((IStructuredSelection) fServersCombo.getSelection()).getFirstElement();
		if (selection instanceof AbstractWebServerConfiguration)
		{
			return (AbstractWebServerConfiguration) selection;
		}
		return null;
	}

	private void setSelectedType(Type type)
	{
		switch (type)
		{
			case NONE:
				fNoSettingRadio.setSelection(true);
				fServerRadio.setSelection(false);
				Control comboControl = fServersCombo.getControl();
				comboControl.setForeground(comboControl.getDisplay()
						.getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
				fEditButton.setEnabled(false);
				fNewButton.setEnabled(false);
				break;
			case SERVER:
				fNoSettingRadio.setSelection(false);
				fServerRadio.setSelection(true);
				List<AbstractWebServerConfiguration> servers = ServerConfigurationManager.getInstance()
						.getServerConfigurations();
				comboControl = fServersCombo.getControl();
				if (servers.size() == 0)
				{
					comboControl.setForeground(comboControl.getDisplay().getSystemColor(
							SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
					fEditButton.setEnabled(false);
				}
				else
				{
					comboControl.setForeground(comboControl.getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
					fEditButton.setEnabled(true);
				}
				fNewButton.setEnabled(true);
				break;
		}
	}

	private void updateServersContent()
	{
		List<AbstractWebServerConfiguration> servers = ServerConfigurationManager.getInstance()
				.getServerConfigurations();
		if (servers.size() == 0)
		{
			Object[] input = new Object[] { Messages.ProjectPreviewPropertyPage_NoPreviewServer };
			fServersCombo.setInput(input);
			fServersCombo.setSelection(new StructuredSelection(input[0]), true);
		}
		else
		{
			fServersCombo.setInput(servers);
			fServersCombo.setSelection(new StructuredSelection(servers.get(0)), true);
		}
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.preview.ui.properties;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.ui.dialogs.ListDialog;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.preview.PreviewPlugin;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.PropertyDialogsRegistry;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServerType;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * A composite where user could choose to use the built-in preview server or specify a custom one.
 * 
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class PreviewSettingComposite extends Composite implements SelectionListener
{

	public static interface Listener
	{
		/**
		 * Notify the listener that the preview setting is modified.
		 */
		public void previewSettingModified();
	}

	private enum Type
	{
		NONE, SERVER
	};

	private Button fNoSettingRadio;
	private Button fServerRadio;
	private ComboViewer fServersCombo;
	private Button fEditButton;
	private Button fNewButton;

	private List<Listener> fListeners;
	private IServer fSelectedServer;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public PreviewSettingComposite(Composite parent)
	{
		super(parent, SWT.NONE);
		setLayout(GridLayoutFactory.fillDefaults().spacing(5, 0).create());
		fListeners = new ArrayList<Listener>();

		fNoSettingRadio = new Button(this, SWT.RADIO);
		fNoSettingRadio.setText(Messages.ProjectPreviewPropertyPage_LBL_NoSettings);
		fNoSettingRadio.addSelectionListener(this);

		Composite serverComposite = new Composite(this, SWT.NONE);
		serverComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());
		serverComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());

		fServerRadio = new Button(serverComposite, SWT.RADIO);
		fServerRadio.setText(Messages.ProjectPreviewPropertyPage_Server_Label);
		fServerRadio.addSelectionListener(this);

		fServersCombo = new ComboViewer(serverComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fServersCombo.getControl().setLayoutData(
				GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		fServersCombo.setContentProvider(ArrayContentProvider.getInstance());
		fServersCombo.setLabelProvider(new LabelProvider()
		{

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

		fEditButton = new Button(serverComposite, SWT.PUSH);
		fEditButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		fEditButton.addSelectionListener(this);

		fNewButton = new Button(serverComposite, SWT.PUSH);
		fNewButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
		fNewButton.addSelectionListener(this);

		updateServersContent();
		setSelectedType(Type.NONE);
	}

	/**
	 * Adds a listener to be notified when the preview setting is modified.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addListener(Listener listener)
	{
		if (!fListeners.contains(listener))
		{
			fListeners.add(listener);
		}
	}

	/**
	 * Removes a listener from being notified when the preview setting is modified.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeListener(Listener listener)
	{
		fListeners.remove(listener);
	}

	/**
	 * @return the currently selected server
	 */
	public IServer getSelectedServer()
	{
		if (fNoSettingRadio.getSelection())
		{
			return null;
		}
		Object selection = ((IStructuredSelection) fServersCombo.getSelection()).getFirstElement();
		if (selection instanceof IServer)
		{
			return (IServer) selection;
		}
		return null;
	}

	/**
	 * Set the composite to select a specific server.
	 * 
	 * @param server
	 *            the selected server
	 */
	public void setSelectedServer(IServer server)
	{
		fSelectedServer = server;
		if (fSelectedServer == null)
		{
			setSelectedType(Type.NONE);
		}
		else
		{
			setSelectedType(Type.SERVER);
			fServersCombo.setSelection(new StructuredSelection(fSelectedServer));
		}
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

		IServer server = getSelectedServer();
		if (fSelectedServer != server)
		{
			fSelectedServer = server;
			firePreviewSettingModified();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
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

				List<IServer> servers = WebServerCorePlugin.getDefault().getServerManager().getServers();
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

	private void createNewServer()
	{
		ListDialog dialog = new ListDialog(getShell());
		dialog.setContentProvider(ArrayContentProvider.getInstance());
		dialog.setLabelProvider(new LabelProvider()
		{

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
		dialog.setInput(WebServerCorePlugin.getDefault().getServerManager().getServerTypes());
		dialog.setTitle(Messages.ProjectPreviewPropertyPage_ChooseServerType);

		Object[] result;
		if (dialog.open() == Window.OK && (result = dialog.getResult()) != null && result.length == 1)
		{
			String typeId = ((IServerType) result[0]).getId();
			try
			{
				IServer newConfiguration = WebServerCorePlugin.getDefault().getServerManager().createServer(typeId);
				if (newConfiguration != null)
				{
					if (editServerConfiguration(newConfiguration))
					{
						WebServerCorePlugin.getDefault().getServerManager().add(newConfiguration);
						updateServersContent();
						fServersCombo.setSelection(new StructuredSelection(newConfiguration));
						// forces an update of widget enablements
						setSelectedType(Type.SERVER);
					}
				}
			}
			catch (CoreException e)
			{
				PreviewPlugin.log(Messages.ProjectPreviewPropertyPage_ERR_FailToCreateServer, e);
			}
		}
	}

	private void editSelectedServer()
	{
		Object selection = ((IStructuredSelection) fServersCombo.getSelection()).getFirstElement();
		if (selection instanceof IServer && editServerConfiguration((IServer) selection))
		{
			fServersCombo.refresh();
		}
	}

	private boolean editServerConfiguration(IServer serverConfiguration)
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

	private void firePreviewSettingModified()
	{
		for (Listener listener : fListeners)
		{
			listener.previewSettingModified();
		}
	}

	private void updateServersContent()
	{
		List<IServer> servers = WebServerCorePlugin.getDefault().getServerManager().getServers();
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

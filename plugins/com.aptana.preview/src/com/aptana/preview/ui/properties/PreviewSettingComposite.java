/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.preview.ui.properties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import com.aptana.core.util.EclipseUtil;
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

	private ComboViewer fServersCombo;
	private Button fEditButton;
	private Button fNewButton;

	private Set<Listener> fListeners;
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
		setLayout(GridLayoutFactory.fillDefaults().spacing(5, 0).numColumns(3).create());
		fListeners = new LinkedHashSet<Listener>();

		fServersCombo = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		fServersCombo.getControl().setLayoutData(
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
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
		fServersCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateStates();
				firePreviewSettingModified();
			}
		});

		fEditButton = new Button(this, SWT.PUSH);
		fEditButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		fEditButton.setLayoutData(GridDataFactory.swtDefaults().hint(getButtonWidthHint(fEditButton), SWT.DEFAULT)
				.create());
		fEditButton.addSelectionListener(this);

		fNewButton = new Button(this, SWT.PUSH);
		fNewButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
		fNewButton.setLayoutData(GridDataFactory.swtDefaults().hint(getButtonWidthHint(fNewButton), SWT.DEFAULT)
				.create());
		fNewButton.addSelectionListener(this);

		updateServersContentJob();
	}

	/**
	 * Adds a listener to be notified when the preview setting is modified.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addListener(Listener listener)
	{
		fListeners.add(listener);
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

	public Control getServersCombo()
	{
		return fServersCombo.getControl();
	}

	public Control getEditButton()
	{
		return fEditButton;
	}

	public Control getNewButton()
	{
		return fNewButton;
	}

	/**
	 * @return the currently selected server
	 */
	public IServer getSelectedServer()
	{
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
		if (fSelectedServer == null || fSelectedServer == getBuiltInServer())
		{
			fServersCombo.setSelection(new StructuredSelection(getBuiltInServer()));
		}
		else
		{
			fServersCombo.setSelection(new StructuredSelection(fSelectedServer));
		}
		updateStates();
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();

		if (source == fEditButton)
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

	private void updateStates()
	{
		Object selectedElement = ((IStructuredSelection) fServersCombo.getSelection()).getFirstElement();
		fEditButton.setEnabled(selectedElement != getBuiltInServer());
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
						updateServersContent(false);
						fServersCombo.setSelection(new StructuredSelection(newConfiguration));
						// forces an update of widget enablements
						updateStates();
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
		Listener[] listeners = fListeners.toArray(new Listener[fListeners.size()]);
		for (Listener listener : listeners)
		{
			listener.previewSettingModified();
		}
	}

	private void updateServersContentJob()
	{
		Job job = new Job("Updating servers content...") //$NON-NLS-1$ // system job
		{
			@Override
			public IStatus run(IProgressMonitor monitor)
			{
				updateServersContent(true);
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/**
	 * Update the servers content.
	 * 
	 * @param async
	 *            An indication that the UI part of this update should be done asynchronously.
	 */
	private void updateServersContent(boolean async)
	{
		final List<IServer> servers = new ArrayList<IServer>();
		servers.add(getBuiltInServer());
		servers.addAll(WebServerCorePlugin.getDefault().getServerManager().getServers());
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				if (!isDisposed())
				{
					ISelection selection = fServersCombo.getSelection();
					fServersCombo.setInput(servers);
					// keeps the selection if there was one
					fServersCombo.setSelection(selection);
					if (fServersCombo.getSelection().isEmpty())
					{
						fServersCombo.setSelection(new StructuredSelection(servers.get(0)));
					}
				}
			}
		};
		if (async)
		{
			UIUtils.getDisplay().asyncExec(runnable);
		}
		else
		{
			UIUtils.getDisplay().syncExec(runnable);
		}
	}

	private static IServer getBuiltInServer()
	{
		return WebServerCorePlugin.getDefault().getBuiltinWebServer();
	}

	private static int getButtonWidthHint(Button button)
	{
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}
}

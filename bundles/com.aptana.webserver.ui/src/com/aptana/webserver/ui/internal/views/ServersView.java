/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISources;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.core.Identifiable;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.ImageAssociations;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServerChangeListener;
import com.aptana.webserver.core.ServerChangeEvent;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.WebServerUIPlugin;

/**
 * ServersView
 */
public class ServersView extends ViewPart implements IServerChangeListener
{

	private TableViewer serverTableViewer;

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout());
		serverTableViewer = createServerTable(parent);
		getSite().setSelectionProvider(serverTableViewer);
	}

	private TableViewer createServerTable(Composite parent)
	{
		TableViewer tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		Table serverTable = tableViewer.getTable();
		serverTable.setHeaderVisible(true);
		serverTable.setLinesVisible(true);
		serverTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn nameCol = new TableColumn(serverTable, SWT.LEFT);
		nameCol.setText(Messages.ServersView_NAME);
		nameCol.setWidth(150);

		TableColumn statusCol = new TableColumn(serverTable, SWT.LEFT);
		statusCol.setText(Messages.ServersView_STATUS);
		statusCol.setWidth(75);

		TableColumn typeColumn = new TableColumn(serverTable, SWT.LEFT);
		typeColumn.setText(Messages.ServersView_TYPE);
		typeColumn.setWidth(125);

		TableColumn hostColumn = new TableColumn(serverTable, SWT.LEFT);
		hostColumn.setText(Messages.GenericServersView_HOST);
		hostColumn.setWidth(150);

		TableColumn portColumn = new TableColumn(serverTable, SWT.LEFT);
		portColumn.setText(Messages.GenericServersView_PORT);
		portColumn.setWidth(50);

		WebServerCorePlugin.getDefault().getServerManager().addServerChangeListener(this);
		tableViewer.setLabelProvider(new ServerLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(WebServerCorePlugin.getDefault().getServerManager().getServers());

		return tableViewer;
	}

	/**
	 * getSelection
	 * 
	 * @return ISelection
	 */
	public ISelection getSelection()
	{
		return serverTableViewer.getSelection();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		serverTableViewer.getTable().setFocus();
	}

	/**
	 * ServerLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ServerLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (columnIndex == 0)
			{
				if (element instanceof Identifiable)
				{
					Identifiable identifiable = (Identifiable) element;
					String id = identifiable.getId();

					Image img = WebServerUIPlugin.getDefault().getImageRegistry().get(id);
					if (img != null)
					{
						return img;
					}

					ImageDescriptor desc = ImageAssociations.getInstance().getImageDescriptor(id);
					if (desc != null)
					{
						WebServerUIPlugin.getDefault().getImageRegistry().put(id, desc);
						return WebServerUIPlugin.getDefault().getImageRegistry().get(id);
					}
				}
				return WebServerUIPlugin.getImage(WebServerUIPlugin.SERVER_ICON);
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			IServer server = (IServer) element;
			switch (columnIndex)
			{
				case 0:
					return server.getName();
				case 1:
					switch (server.getState())
					{
						case STARTED:
							return Messages.ServersView_STATUS_STARTED;
						case STARTING:
							return Messages.ServersView_STATUS_STARTING;
						case STOPPING:
							return Messages.ServersView_STATUS_STOPPING;
						case STOPPED:
							return Messages.ServersView_STATUS_STOPPED;
						case UNKNOWN:
							return Messages.ServersView_STATUS_UNKNOWN;
						case NOT_APPLICABLE:
							return Messages.ServersView_STATUS_NOT_APPLICABLE;
						default:
							return StringUtil.EMPTY;
					}
				case 2:
					return server.getType().getName();
				case 3:
					return server.getHostname();
				case 4:
					int port = server.getPort();
					return (port == -1) ? "?" : Integer.toString(port); //$NON-NLS-1$
				default:
					return StringUtil.EMPTY;
			}
		}
	}

	public void configurationChanged(final ServerChangeEvent event)
	{
		Display display = UIUtils.getDisplay();
		if (!display.isDisposed())
		{
			display.syncExec(new Runnable()
			{
				public void run()
				{
					switch (event.getKind())
					{
						case ADDED:
							serverTableViewer.add(event.getServer());
							break;
						case REMOVED:
							serverTableViewer.remove(event.getServer());
						case UPDATED:
							serverTableViewer.update(event.getServer(), null);
							// force commands evaluating selection for enablement/handling to get re-evaluated
							IEvaluationService service = (IEvaluationService) getViewSite().getService(
									IEvaluationService.class);
							if (service != null)
							{
								service.requestEvaluation(ISources.ACTIVE_CURRENT_SELECTION_NAME);
							}
						default:
							break;
					}
				}
			});
		}
	}

}

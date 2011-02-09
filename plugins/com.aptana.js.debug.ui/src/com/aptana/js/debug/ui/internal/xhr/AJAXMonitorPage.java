/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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

package com.aptana.js.debug.ui.internal.xhr;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.Page;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.model.xhr.IXHRTransfer;

/**
 * @author Max Stepanov
 */
public class AJAXMonitorPage extends Page {

	private Control mainControl;
	private TableViewer tableViewer;
	private Viewer[] requestData = new Viewer[2];
	private Viewer[] responseData = new Viewer[2];

	/**
	 * AJAXMonitorPage
	 */
	public AJAXMonitorPage() {
	}

	/**
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		SashForm hSashForm = new SashForm(parent, SWT.VERTICAL);
		hSashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		hSashForm.setBackgroundMode(SWT.INHERIT_DEFAULT);
		createRequestsTable(hSashForm);

		SashForm vSashForm = new SashForm(hSashForm, SWT.HORIZONTAL);
		vSashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		createDetailsPart(vSashForm, Messages.AJAXMonitorPage_Request, requestData);
		createDetailsPart(vSashForm, Messages.AJAXMonitorPage_Response, responseData);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IXHRTransfer xhr = (IXHRTransfer) ((IStructuredSelection) selection).getFirstElement();
					updateDetails(xhr);
				}
			}
		});
		mainControl = hSashForm;
	}

	private void createRequestsTable(Composite parent) {
		tableViewer = new TableViewer(parent, /* SWT.VIRTUAL | */SWT.SINGLE | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setResizable(false);
		tableLayout.addColumnData(new ColumnPixelData(20));

		column = new TableColumn(table, SWT.LEFT);
		column.setText(Messages.AJAXMonitorPage_URL);
		tableLayout.addColumnData(new ColumnWeightData(60));

		column = new TableColumn(table, SWT.LEFT);
		column.setText(Messages.AJAXMonitorPage_Method);
		tableLayout.addColumnData(new ColumnWeightData(10));

		column = new TableColumn(table, SWT.LEFT);
		column.setText(Messages.AJAXMonitorPage_Sent);
		tableLayout.addColumnData(new ColumnWeightData(10));

		column = new TableColumn(table, SWT.LEFT);
		column.setText(Messages.AJAXMonitorPage_Received);
		tableLayout.addColumnData(new ColumnWeightData(10));

		tableViewer.setContentProvider(new XHRContentProvider());
		tableViewer.setLabelProvider(new XHRLabelProvider());
	}

	private void createDetailsPart(Composite parent, String name, Viewer[] viewers) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth /= 2;
		container.setLayout(layout);

		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(name);

		CTabFolder tab = new CTabFolder(container, SWT.BOTTOM | SWT.FLAT | SWT.BORDER);
		tab.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableViewer viewer = new TableViewer(tab, /* SWT.VIRTUAL | */SWT.SINGLE | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		// CHECKSTYLE:OFF
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText(Messages.AJAXMonitorPage_Name);
		tableLayout.addColumnData(new ColumnWeightData(50));

		column = new TableColumn(table, SWT.LEFT);
		column.setText(Messages.AJAXMonitorPage_Value);
		tableLayout.addColumnData(new ColumnWeightData(50));
		// CHECKSTYLE:ON

		viewer.setContentProvider(new XHRContentProvider());
		viewer.setLabelProvider(new XHRLabelProvider());
		viewers[0] = viewer;

		CTabItem item = new CTabItem(tab, SWT.NONE);
		item.setControl(viewers[0].getControl());
		item.setText(Messages.AJAXMonitorPage_Headers);

		TextViewer textViewer = new TextViewer(tab, SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		item = new CTabItem(tab, SWT.NONE);
		item.setControl(textViewer.getControl());
		item.setText(Messages.AJAXMonitorPage_Body);

		textViewer.setEditable(false);
		textViewer.setDocument(new Document());
		viewers[1] = textViewer;
		tab.setSelection(item);
	}

	private void updateDetails(IXHRTransfer xhr) {
		if (xhr != null) {
			requestData[0].setInput(xhr.getRequestHeaders());
			((IDocument) requestData[1].getInput()).set(xhr.getRequestBody());

			responseData[0].setInput(xhr.getResponseHeaders());
			((IDocument) responseData[1].getInput()).set(xhr.getResponseBody());
		} else {
			requestData[0].setInput(null);
			((IDocument) requestData[1].getInput()).set(StringUtil.EMPTY);

			responseData[0].setInput(null);
			((IDocument) responseData[1].getInput()).set(StringUtil.EMPTY);
		}
	}

	/**
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return mainControl;
	}

	/**
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	/**
	 * getViewer
	 * 
	 * @return Viewer
	 */
	public Viewer getViewer() {
		return tableViewer;
	}
}

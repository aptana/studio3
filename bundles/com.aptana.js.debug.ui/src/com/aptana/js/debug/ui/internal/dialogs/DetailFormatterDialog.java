/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.dialogs;

import java.util.List;

import org.eclipse.debug.internal.ui.actions.StatusInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.util.StringUtil;
import com.aptana.debug.core.DetailFormatter;
import com.aptana.debug.ui.IDebugHelpContextIds;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class DetailFormatterDialog extends StatusDialog {
	private Text typeName;
	private Button enabled;
	private SourceViewer snippetViewer;

	private DetailFormatter formatter;
	private List<String> definedTypes;
	private boolean editTypeName;

	/**
	 * DetailFormatterDialog
	 */
	public DetailFormatterDialog(Shell parent, DetailFormatter detailFormatter, List<String> definedTypes,
			boolean editDialog) {
		this(parent, detailFormatter, definedTypes, true, editDialog);
	}

	/**
	 * DetailFormatterDialog
	 */
	public DetailFormatterDialog(Shell parent, DetailFormatter formatter, List<String> definedTypes,
			boolean editTypeName, boolean editDialog) {
		super(parent);
		this.formatter = formatter;
		this.editTypeName = editTypeName;
		this.definedTypes = definedTypes;
		setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
		setTitle(editDialog ? Messages.DetailFormatterDialog_EditDetailFormatter
				: Messages.DetailFormatterDialog_AddDetailFormatter);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Font font = parent.getFont();

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.DetailFormatterDialog_QualifiedTypeName);
		GridDataFactory.swtDefaults().applyTo(label);
		label.setFont(font);

		Composite container = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(container);
		container.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		typeName = new Text(container, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(typeName);
		typeName.setEditable(editTypeName);
		typeName.setText(formatter.getTypeName());
		typeName.setFont(font);

		Button searchButton = new Button(container, SWT.PUSH);
		GridDataFactory
				.swtDefaults()
				.align(SWT.RIGHT, SWT.CENTER)
				.hint(Math.max(convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
						searchButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).applyTo(searchButton);
		searchButton.setText(StringUtil.ellipsify(Messages.DetailFormatterDialog_SelectNType));

		searchButton.setEnabled(editTypeName);
		searchButton.setFont(font);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.DetailFormatterDialog_DetailFormatterCodeSnippet);
		GridDataFactory.swtDefaults().applyTo(label);
		label.setFont(font);

		snippetViewer = new SourceViewer(composite, null, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true)
				.hint(convertWidthInCharsToPixels(80), convertHeightInCharsToPixels(10))
				.applyTo(snippetViewer.getControl());

		enabled = new Button(composite, SWT.CHECK | SWT.LEFT);
		enabled.setText(Messages.DetailFormatterDialog_Enable);
		GridDataFactory.swtDefaults().applyTo(enabled);
		enabled.setFont(font);

		typeName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkValues();
			}
		});

		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectType();
			}
		});

		snippetViewer.setInput(this);

		snippetViewer.setEditable(true);
		snippetViewer.setDocument(new Document(formatter.getSnippet()));

		snippetViewer.getDocument().addDocumentListener(new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
			}

			public void documentChanged(DocumentEvent event) {
				checkValues();
			}
		});

		if (formatter.getTypeName().length() > 0) {
			snippetViewer.getControl().setFocus();
		}

		enabled.setSelection(formatter.isEnabled());

		checkValues();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IDebugHelpContextIds.EDIT_DETAIL_FORMATTER_DIALOG);

		return composite;
	}

	private void selectType() {
		JSTypeSelectionDialog dlg = new JSTypeSelectionDialog(getShell());
		dlg.setTitle(Messages.DetailFormatterDialog_SelectType);
		dlg.setMessage(Messages.DetailFormatterDialog_SelectTypeToFormatWhenDisplayingDetail);

		dlg.setInitialSelections(new Object[] { typeName.getText() });
		if (dlg.open() == Window.OK) {
			Object[] types = dlg.getResult();
			if (types != null && types.length > 0) {
				typeName.setText((String) types[0]);
			}
		}
	}

	protected void okPressed() {
		formatter.setEnabled(enabled.getSelection());
		formatter.setTypeName(typeName.getText().trim());
		formatter.setSnippet(snippetViewer.getDocument().get());

		super.okPressed();
	}

	private void checkValues() {
		StatusInfo status = new StatusInfo();
		String name = typeName.getText().trim();
		if (name.length() == 0) {
			status.setError(Messages.DetailFormatterDialog_QualifiedTypeNameMustNotBeEmpty);
		} else if (definedTypes != null && definedTypes.contains(name)) {
			status.setError(Messages.DetailFormatterDialog_DetailFormatterIsDefinedForThisType);
		} else if (snippetViewer.getDocument().get().trim().length() == 0) {
			status.setError(Messages.DetailFormatterDialog_CodeSnippetMustNotBeEmpty);
		}
		updateStatus(status);
	}

}

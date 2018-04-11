/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.dialogs.CloakExpressionDialog;

public class RemotePreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private static final String ADD_ICON = "icons/full/etool16/add.png"; //$NON-NLS-1$
	private static final String REMOVE_ICON = "icons/full/etool16/delete.png"; //$NON-NLS-1$

	private Button removeButton;
	private TableViewer tableViewer;
	private Button fReopenButton;

	private Set<String> expressions;

	public RemotePreferencePage()
	{
		expressions = new HashSet<String>();
	}

	public void init(IWorkbench workbench)
	{
		setDescription(Messages.RemotePreferencePage_LBL_Description);
		loadCloakingExpressions();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.swtDefaults().create());

		Composite cloaking = createCloakingComposite(main);
		cloaking.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		fReopenButton = new Button(main, SWT.CHECK);
		fReopenButton.setText(Messages.RemotePreferencePage_LBL_ReopenRemote);
		fReopenButton.setSelection(RemotePreferenceUtil.getReopenRemoteOnStartup());
		fReopenButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		return main;
	}

	@Override
	protected void performDefaults()
	{
		expressions.clear();
		expressions.addAll(Arrays.asList(CloakingUtils.getDefaultCloakedFileTypes()));
		tableViewer.refresh();
		fReopenButton.setSelection(false);

		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		saveCloakingExpressions();
		RemotePreferenceUtil.setReopenRemoteOnStartup(fReopenButton.getSelection());

		return super.performOk();
	}

	private Composite createCloakingComposite(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		Button addButton = new Button(main, SWT.PUSH);
		addButton.setImage(IOUIPlugin.getImage(ADD_ICON));
		addButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				add();
			}
		});

		removeButton = new Button(main, SWT.PUSH);
		removeButton.setImage(IOUIPlugin.getImage(REMOVE_ICON));
		removeButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				remove();
			}
		});

		tableViewer = new TableViewer(main, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setInput(expressions);
		tableViewer.setComparator(new ViewerComparator());
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateButtonState();
			}
		});

		updateButtonState();

		return main;
	}

	private void add()
	{
		CloakExpressionDialog dialog = new CloakExpressionDialog(getShell());
		if (dialog.open() == Window.OK)
		{
			expressions.add(dialog.getExpression());
			tableViewer.refresh();
		}
	}

	private void remove()
	{
		Object[] elements = ((IStructuredSelection) tableViewer.getSelection()).toArray();
		for (Object element : elements)
		{
			expressions.remove(element.toString());
		}
		tableViewer.refresh();
	}

	private void updateButtonState()
	{
		boolean hasSelection = !tableViewer.getSelection().isEmpty();
		removeButton.setEnabled(hasSelection);
	}

	private void loadCloakingExpressions()
	{
		expressions.addAll(Arrays.asList(CloakingUtils.getCloakedFileTypes()));
	}

	private void saveCloakingExpressions()
	{
		CloakingUtils.setCloakedFileTypes(expressions.toArray(new String[expressions.size()]));
	}
}

/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.core.CoreStrings;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * Allows the user to edit the set of task tags and their priorities
 * 
 * @since 3.0
 */
public final class TasksPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	/**
	 * Delimiter used to separate task tag names and priorities in pref values.
	 */
	private static final String DELIMITER = ","; //$NON-NLS-1$

	private TableViewer fTasksTableViewer;
	private Button fCaseSensitiveButton;
	private Button fAddButton;
	private Button fEditButton;
	private Button fRemoveButton;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.TasksPreferencePage_Description);
		label.setFont(parent.getFont());
		label.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
		label = new Label(composite, SWT.NONE); // spacer
		label.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

		createTaskTableArea(composite);
		createCaseSensitiveArea(composite);

		updateButtonStates();

		return composite;
	}

	private void createCaseSensitiveArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		composite.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

		fCaseSensitiveButton = new Button(composite, SWT.CHECK);
		fCaseSensitiveButton.setFont(parent.getFont());
		fCaseSensitiveButton.setText(Messages.TasksPreferencePage_CaseSensitiveLabel);
		fCaseSensitiveButton.setSelection(getPreferenceStore().getBoolean(
				ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE));
		setButtonLayoutData(fCaseSensitiveButton);
	}

	/**
	 * @param parent
	 */
	private void createTaskTableArea(Composite parent)
	{
		fTasksTableViewer = new TableViewer(parent, SWT.BORDER | SWT.SINGLE);
		Table table = fTasksTableViewer.getTable();
		table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(parent.getFont());

		TableColumn tagNameColumn = new TableColumn(table, SWT.NONE);
		tagNameColumn.setText(Messages.TasksPreferencePage_TagNameColumnHeader);
		tagNameColumn.setWidth(100);
		TableColumn tagPriorityColumn = new TableColumn(table, SWT.NONE);
		tagPriorityColumn.setText(Messages.TasksPreferencePage_PriorityColumnHeader);
		tagPriorityColumn.setWidth(100);

		fTasksTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		fTasksTableViewer.setLabelProvider(new TaskLabelProvider());
		fTasksTableViewer.setComparator(new ViewerComparator());
		fTasksTableViewer.setInput(getTaskTags());

		fTasksTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				// Enable/disable buttons
				updateButtonStates();
			}
		});

		createTaskButtons(parent);
	}

	private void createTaskButtons(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(GridDataFactory.fillDefaults().create());
		composite.setLayout(GridLayoutFactory.fillDefaults().create());

		// Now create the buttons
		fAddButton = new Button(composite, SWT.PUSH);
		fAddButton.setText(StringUtil.ellipsify(CoreStrings.NEW));
		fAddButton.setLayoutData(GridDataFactory
				.swtDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(Math.max(fAddButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x,
						convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH)), SWT.DEFAULT).create());
		fAddButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TaskTag tag = new TaskTag("", IMarker.PRIORITY_NORMAL); //$NON-NLS-1$

				List<TaskTag> tags = new ArrayList<TaskTag>();
				TableItem[] items = fTasksTableViewer.getTable().getItems();
				for (TableItem anItem : items)
				{
					tags.add((TaskTag) anItem.getData());
				}

				// Open a dialog for user to enter the tag name and select priority!
				TaskTagInputDialog dialog = new TaskTagInputDialog(tag, tags, getShell());
				dialog.setTitle(Messages.TasksPreferencePage_NewTagTitle);
				if (dialog.open() == Window.OK)
				{
					// Insert task in our model and set the new input on the table!
					tags.add(dialog.getTaskTag());
					fTasksTableViewer.setInput(tags);
				}
			}
		});

		fEditButton = new Button(composite, SWT.PUSH);
		fEditButton.setText(StringUtil.ellipsify(CoreStrings.EDIT));
		fEditButton.setLayoutData(GridDataFactory
				.swtDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(Math.max(fEditButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x,
						convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH)), SWT.DEFAULT).create());
		fEditButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				List<TaskTag> tags = new ArrayList<TaskTag>();
				TableItem[] items = fTasksTableViewer.getTable().getItems();
				for (TableItem anItem : items)
				{
					tags.add((TaskTag) anItem.getData());
				}

				int index = fTasksTableViewer.getTable().getSelectionIndex();
				TableItem item = fTasksTableViewer.getTable().getItem(index);
				TaskTag tag = (TaskTag) item.getData();
				// Open a dialog for user to edit the tag name and priority!
				List<TaskTag> copy = new ArrayList<TaskTag>(tags);
				copy.remove(index);
				TaskTagInputDialog dialog = new TaskTagInputDialog(tag, copy, getShell());
				dialog.setTitle(Messages.TasksPreferencePage_EditTagTitle);
				if (dialog.open() == Window.OK)
				{
					tags.set(index, dialog.getTaskTag());
					fTasksTableViewer.setInput(tags);
				}
			}
		});

		fRemoveButton = new Button(composite, SWT.PUSH);
		fRemoveButton.setText(CoreStrings.REMOVE);
		fRemoveButton.setLayoutData(GridDataFactory
				.swtDefaults()
				.align(SWT.FILL, SWT.CENTER)
				.hint(Math.max(fRemoveButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x,
						convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH)), SWT.DEFAULT).create());
		fRemoveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// Remove first selected tag from table!
				int index = fTasksTableViewer.getTable().getSelectionIndex();
				fTasksTableViewer.getTable().remove(index);
			}
		});
	}

	private List<TaskTag> getTaskTags()
	{
		String rawTagNames = getPreferenceStore().getString(ICorePreferenceConstants.TASK_TAG_NAMES);
		String rawTagPriorities = getPreferenceStore().getString(ICorePreferenceConstants.TASK_TAG_PRIORITIES);
		return createTaskTags(rawTagNames, rawTagPriorities);
	}

	private void updateButtonStates()
	{
		boolean enable = !fTasksTableViewer.getSelection().isEmpty();
		fEditButton.setEnabled(enable);
		fRemoveButton.setEnabled(enable);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		List<String> tagNames = new ArrayList<String>();
		List<String> tagPriorities = new ArrayList<String>();

		TableItem[] items = fTasksTableViewer.getTable().getItems();
		for (TableItem item : items)
		{
			TaskTag tag = (TaskTag) item.getData();
			tagNames.add(tag.getName());
			tagPriorities.add(tag.getPriorityName());
		}

		getPreferenceStore().setValue(ICorePreferenceConstants.TASK_TAG_NAMES,
				StringUtil.join(DELIMITER, tagNames.toArray(new String[tagNames.size()])));
		getPreferenceStore().setValue(ICorePreferenceConstants.TASK_TAG_PRIORITIES,
				StringUtil.join(DELIMITER, tagPriorities.toArray(new String[tagPriorities.size()])));
		getPreferenceStore().setValue(ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE,
				fCaseSensitiveButton.getSelection());

		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();

		String rawTagNames = getPreferenceStore().getDefaultString(ICorePreferenceConstants.TASK_TAG_NAMES);
		String rawTagPriorities = getPreferenceStore().getDefaultString(ICorePreferenceConstants.TASK_TAG_PRIORITIES);
		List<TaskTag> tags = createTaskTags(rawTagNames, rawTagPriorities);
		fTasksTableViewer.setInput(tags);

		fCaseSensitiveButton.setSelection(getPreferenceStore().getDefaultBoolean(
				ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE));
	}

	private List<TaskTag> createTaskTags(String rawTagNames, String rawTagPriorities)
	{
		List<TaskTag> tags = new ArrayList<TaskTag>();
		String[] tagNames = rawTagNames.split(DELIMITER);
		String[] tagPriorities = rawTagPriorities.split(DELIMITER);
		for (int i = 0; i < tagNames.length; i++)
		{
			tags.add(new TaskTag(tagNames[i], tagPriorities[i]));
		}
		return tags;
	}

	private static class TaskLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			TaskTag tag = (TaskTag) element;
			switch (columnIndex)
			{
				case 0:
					return tag.getName();
				case 1:
					return tag.getPriorityName();
			}
			return null;
		}
	}
}

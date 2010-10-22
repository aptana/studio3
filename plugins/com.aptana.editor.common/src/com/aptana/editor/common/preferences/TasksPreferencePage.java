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

package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.tasks.TaskTag;

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
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.TasksPreferencePage_Description);
		label.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 400;
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		label = new Label(composite, SWT.NONE); // spacer
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		createTaskTableArea(composite);
		createCaseSensitiveArea(composite);

		return composite;
	}

	private void createCaseSensitiveArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);

		fCaseSensitiveButton = new Button(composite, SWT.CHECK);
		fCaseSensitiveButton.setFont(parent.getFont());
		fCaseSensitiveButton.setText(Messages.TasksPreferencePage_CaseSensitiveLabel);

		fCaseSensitiveButton.setSelection(getPreferenceStore()
				.getBoolean(IPreferenceConstants.TASK_TAGS_CASE_SENSITIVE));
		setButtonLayoutData(fCaseSensitiveButton);
	}

	/**
	 * @param parent
	 */
	private void createTaskTableArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 200;
		composite.setLayoutData(data);

		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		Table table = new Table(composite, SWT.BORDER | SWT.SINGLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn tagNameColumn = new TableColumn(table, SWT.NONE);
		tagNameColumn.setText(Messages.TasksPreferencePage_TagNameColumnHeader);
		tagNameColumn.setWidth(100);
		TableColumn tagPriorityColumn = new TableColumn(table, SWT.NONE);
		tagPriorityColumn.setText(Messages.TasksPreferencePage_PriorityColumnHeader);
		tagPriorityColumn.setWidth(100);
		table.setFont(parent.getFont());

		fTasksTableViewer = new TableViewer(table);
		table.setFont(parent.getFont());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		fTasksTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		TaskLabelProvider categoryLabelProvider = new TaskLabelProvider();
		fTasksTableViewer.setLabelProvider(categoryLabelProvider);
		fTasksTableViewer.setSorter(new ViewerSorter());

		fTasksTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				// Enable/disable buttons
				boolean enable = !event.getSelection().isEmpty();
				fEditButton.setEnabled(enable);
				fRemoveButton.setEnabled(enable);
			}
		});
		fTasksTableViewer.setInput(getTaskTags());

		createTaskButtons(composite);
	}

	private void createTaskButtons(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 100;
		composite.setLayoutData(data);

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		// Now create the buttons
		fAddButton = new Button(composite, SWT.PUSH);
		fAddButton.setText(Messages.TasksPreferencePage_AddButtonLabel);
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
					TaskTag aTag = (TaskTag) anItem.getData();
					tags.add(aTag);
				}

				// Open a dialog for user to enter the tag name and select priority!
				TaskTagInputDialog dialog = new TaskTagInputDialog(tag, tags, getShell());
				dialog.setTitle(Messages.TasksPreferencePage_NewTagTitle);
				if (dialog.open() == Window.OK)
				{
					TaskTag result = dialog.getTaskTag();
					// Insert task in our model and set the new input on the table!
					tags.add(result);

					fTasksTableViewer.setInput(tags);
				}
			}
		});

		fEditButton = new Button(composite, SWT.PUSH);
		fEditButton.setText(Messages.TasksPreferencePage_EditButtonLabel);
		fEditButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				List<TaskTag> tags = new ArrayList<TaskTag>();
				TableItem[] items = fTasksTableViewer.getTable().getItems();
				for (TableItem anItem : items)
				{
					TaskTag aTag = (TaskTag) anItem.getData();
					tags.add(aTag);
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
					TaskTag result = dialog.getTaskTag();
					tags.set(index, result);
					fTasksTableViewer.setInput(tags);
				}
			}
		});

		fRemoveButton = new Button(composite, SWT.PUSH);
		fRemoveButton.setText(Messages.TasksPreferencePage_RemoveButtonLabel);
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
		String rawTagNames = getPreferenceStore().getString(IPreferenceConstants.TASK_TAG_NAMES);
		String rawTagPriorities = getPreferenceStore().getString(IPreferenceConstants.TASK_TAG_PRIORITIES);
		return createTaskTags(rawTagNames, rawTagPriorities);
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

		getPreferenceStore().setValue(IPreferenceConstants.TASK_TAG_NAMES,
				StringUtil.join(DELIMITER, tagNames.toArray(new String[tagNames.size()])));

		getPreferenceStore().setValue(IPreferenceConstants.TASK_TAG_PRIORITIES,
				StringUtil.join(DELIMITER, tagPriorities.toArray(new String[tagPriorities.size()])));

		getPreferenceStore().setValue(IPreferenceConstants.TASK_TAGS_CASE_SENSITIVE,
				fCaseSensitiveButton.getSelection());

		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();

		String rawTagNames = getPreferenceStore().getDefaultString(IPreferenceConstants.TASK_TAG_NAMES);
		String rawTagPriorities = getPreferenceStore().getDefaultString(IPreferenceConstants.TASK_TAG_PRIORITIES);
		List<TaskTag> tags = createTaskTags(rawTagNames, rawTagPriorities);
		fTasksTableViewer.setInput(tags);

		fCaseSensitiveButton.setSelection(getPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.TASK_TAGS_CASE_SENSITIVE));
	}

	protected List<TaskTag> createTaskTags(String rawTagNames, String rawTagPriorities)
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

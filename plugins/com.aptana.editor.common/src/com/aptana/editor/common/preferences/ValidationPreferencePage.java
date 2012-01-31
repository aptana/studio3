/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.widgets.CListTable;

public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	// TODO Move this to a buildpath.ui plugin!
	/**
	 * Property names for columns.
	 */
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String BUILD = "build"; //$NON-NLS-1$
	private static final String RECONCILE = "reconcile"; //$NON-NLS-1$

	private ListViewer contentTypesViewer;
	private TableViewer validatorsViewer;
	private CListTable filterViewer;

	public ValidationPreferencePage()
	{
		super();
	}

	public void init(IWorkbench workbench)
	{
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent)
	{
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// the left side
		contentTypesViewer = new ListViewer(sash, SWT.BORDER | SWT.SINGLE);
		contentTypesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().create());
		contentTypesViewer.setContentProvider(ArrayContentProvider.getInstance());
		contentTypesViewer.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				if (element instanceof IContentType)
				{
					return ((IContentType) element).getName();
				}
				return super.getText(element);
			}
		});
		// Filter out content types that have no optional participants
		contentTypesViewer.addFilter(new EmptyContentTypeParticipantListFilter());

		List<IContentType> contentTypes = new ArrayList<IContentType>(getContentTypes());
		Collections.sort(contentTypes, new Comparator<IContentType>()
		{

			public int compare(IContentType o1, IContentType o2)
			{
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		contentTypesViewer.setInput(contentTypes);

		contentTypesViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				// updates the validators and filter expressions to the newly selected language
				if (validatorsViewer != null)
				{
					updateValidators();
					updateFilterExpressions();
				}
			}
		});

		// the right side
		Composite rightComp = new Composite(sash, SWT.NONE);
		rightComp.setLayout(GridLayoutFactory.fillDefaults().create());

		Control validators = createValidators(rightComp);
		validators.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Control filter = createFiltersComposite(rightComp);
		filter.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		sash.setWeights(new int[] { 1, 3 });
		return sash;
	}

	protected IBuildParticipantManager getBuildParticipantManager()
	{
		return BuildPathCorePlugin.getDefault().getBuildParticipantManager();
	}

	@Override
	protected void performDefaults()
	{
		Collection<IBuildParticipant> participants = getAllBuildParticipants();
		for (IBuildParticipant participant : participants)
		{
			participant.restoreDefaults();
		}
		validatorsViewer.refresh();
		updateFilterExpressions();

		super.performDefaults();
	}

	private Control createValidators(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Validators);
		group.setLayout(GridLayoutFactory.fillDefaults().margins(4, 4).create());

		Label label = new Label(group, SWT.WRAP);
		label.setLayoutData(GridDataFactory.fillDefaults().hint(300, 70).create());
		label.setText(Messages.ValidationPreferencePage_EnablingValidatorWarning);

		Table table = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// set up columns
		// Name column
		TableColumn name = new TableColumn(table, SWT.LEFT);
		name.setWidth(250);
		name.setText(Messages.ValidationPreferencePage_NameColumn);
		name.setToolTipText(Messages.ValidationPreferencePage_NameColumn);

		// Build column
		TableColumn build = new TableColumn(table, SWT.CENTER);
		build.setWidth(40);
		build.setText(Messages.ValidationPreferencePage_BuildColumn);
		build.setToolTipText(Messages.ValidationPreferencePage_BuildColumn);

		// Reconcile column
		TableColumn reconcile = new TableColumn(table, SWT.CENTER);
		reconcile.setWidth(55);
		reconcile.setText(Messages.ValidationPreferencePage_ReconcileColumn);
		reconcile.setToolTipText(Messages.ValidationPreferencePage_ReconcileColumn);

		// Now set up table viewer!
		validatorsViewer = new TableViewer(table);
		// validatorsViewer.setUseHashlookup(true);
		validatorsViewer.setColumnProperties(new String[] { NAME, BUILD, RECONCILE });

		// Assign the cell editors to the viewer
		validatorsViewer.setCellEditors(new CellEditor[] { null, new CheckboxCellEditor(table),
				new CheckboxCellEditor(table) });

		// Set the cell modifier for the viewer
		validatorsViewer.setCellModifier(new ParticipantCellModifier(validatorsViewer));

		// Now set up content/label providers
		validatorsViewer.setContentProvider(ArrayContentProvider.getInstance());
		validatorsViewer.setLabelProvider(new ParticipantLabelProvider());
		// Hide required participants
		validatorsViewer.addFilter(new RequiredParticipantFilter());
		// check the selected build participant, show it's filters
		validatorsViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateFilterExpressions();
			}
		});

		// Now set input
		updateValidators();

		return group;
	}

	private Control createFiltersComposite(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Filter);
		group.setLayout(GridLayoutFactory.fillDefaults().margins(4, 4).create());

		filterViewer = new CListTable(group, SWT.NONE);
		filterViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_SelectParticipant);
		final IInputValidator inputValidator = new IInputValidator()
		{

			public String isValid(String newText)
			{
				if (StringUtil.isEmpty(newText))
				{
					return Messages.ValidationPreferencePage_ERR_EmptyExpression;
				}
				return null;
			}
		};
		filterViewer.addListener(new CListTable.Listener()
		{

			public Object addItem()
			{
				InputDialog dialog = new InputDialog(getShell(), Messages.ValidationPreferencePage_Ignore_Title,
						Messages.ValidationPreferencePage_Ignore_Message, null, inputValidator);
				if (dialog.open() == Window.OK)
				{
					return dialog.getValue();
				}
				return null;
			}

			public Object editItem(Object item)
			{
				String expression = item.toString();
				InputDialog dialog = new InputDialog(getShell(), Messages.ValidationPreferencePage_Ignore_Title,
						Messages.ValidationPreferencePage_Ignore_Message, expression, inputValidator);
				if (dialog.open() == Window.OK)
				{
					return dialog.getValue();
				}
				// the dialog is canceled; returns the original item
				return item;
			}

			public void itemsChanged(List<Object> rawFilters)
			{
				// Save the filter expressions
				AbstractBuildParticipant participant = (AbstractBuildParticipant) getSelectedBuildParticipant();
				String[] filters = new String[rawFilters.size()];
				int i = 0;
				for (Object item : rawFilters)
				{
					filters[i++] = item.toString();
				}
				participant.setFilters(EclipseUtil.instanceScope(), filters);
			}
		});
		filterViewer.setEnabled(false);

		return group;
	}

	protected List<IBuildParticipant> getAllBuildParticipants()
	{
		return getBuildParticipantManager().getAllBuildParticipants();
	}

	protected Set<IContentType> getContentTypes()
	{
		return getBuildParticipantManager().getContentTypes();
	}

	private void updateValidators()
	{
		IContentType selected = getSelectedContentType();
		if (selected == null)
		{
			validatorsViewer.setInput(Collections.emptyList());
		}
		else
		{
			validatorsViewer.setInput(getBuildParticipantManager().getBuildParticipants(selected.getId()));
		}
	}

	private IBuildParticipant getSelectedBuildParticipant()
	{
		IStructuredSelection selection = (IStructuredSelection) validatorsViewer.getSelection();
		if (selection.isEmpty())
		{
			return null;
		}
		return (IBuildParticipant) selection.getFirstElement();
	}

	private void updateFilterExpressions()
	{
		IBuildParticipant participant = getSelectedBuildParticipant();
		if (participant != null)
		{
			filterViewer.setEnabled(true);
			filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_Description);
			List<String> expressions = participant.getFilters();
			filterViewer.setItems(expressions.toArray());
		}
		else
		{
			filterViewer.setEnabled(false);
			filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_SelectParticipant);
			filterViewer.setItems(ArrayUtil.NO_OBJECTS);
		}
	}

	private IContentType getSelectedContentType()
	{
		IStructuredSelection selection = (IStructuredSelection) contentTypesViewer.getSelection();
		if (selection.isEmpty())
		{
			return null;
		}
		return (IContentType) selection.getFirstElement();
	}

	/**
	 * This filters the content type list to only those who have at least one non-required build participant (so we can
	 * change it's enablement).
	 * 
	 * @author cwilliams
	 */
	private final class EmptyContentTypeParticipantListFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element)
		{
			if (element instanceof IContentType)
			{
				IContentType type = (IContentType) element;
				List<IBuildParticipant> participants = getBuildParticipantManager().getBuildParticipants(type.getId());
				if (CollectionsUtil.isEmpty(participants))
				{
					return false;
				}
				for (IBuildParticipant participant : participants)
				{
					if (!participant.isRequired())
					{
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Allows modifying cells of a table containing build participants.
	 * 
	 * @author cwilliams
	 */
	private static final class ParticipantCellModifier implements ICellModifier
	{
		private TableViewer tableViewer;

		private ParticipantCellModifier(TableViewer tableViewer)
		{
			this.tableViewer = tableViewer;
		}

		public void modify(Object element, String property, Object value)
		{
			if (element instanceof TableItem)
			{
				element = ((TableItem) element).getData();
			}
			IBuildParticipant participant = (IBuildParticipant) element;
			if (BUILD.equals(property))
			{
				participant.setEnabled(IBuildParticipant.BuildType.BUILD, ((Boolean) value).booleanValue());
			}
			else if (RECONCILE.equals(property))
			{
				participant.setEnabled(IBuildParticipant.BuildType.RECONCILE, ((Boolean) value).booleanValue());
			}
			tableViewer.refresh(participant);
		}

		public Object getValue(Object element, String property)
		{
			IBuildParticipant participant = (IBuildParticipant) element;
			if (BUILD.equals(property))
			{
				return participant.isEnabled(IBuildParticipant.BuildType.BUILD);
			}
			if (RECONCILE.equals(property))
			{
				return participant.isEnabled(IBuildParticipant.BuildType.RECONCILE);
			}
			return null;
		}

		public boolean canModify(Object element, String property)
		{
			return BUILD.equals(property) || RECONCILE.equals(property);
		}
	}

	/**
	 * Provides text and images for build participants.
	 * 
	 * @author cwilliams
	 */
	private static final class ParticipantLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		private static final String RED_X_ICON = "platform:/plugin/com.aptana.ui/icons/delete.gif"; //$NON-NLS-1$
		private static final String CHECKMARK_ICON = "platform:/plugin/com.aptana.ui/icons/ok.png"; //$NON-NLS-1$

		public String getColumnText(Object element, int columnIndex)
		{
			IBuildParticipant participant = (IBuildParticipant) element;
			if (columnIndex == 0)
			{
				return participant.getName();
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			IBuildParticipant participant = (IBuildParticipant) element;
			if (columnIndex == 1)
			{
				if (participant.isEnabled(IBuildParticipant.BuildType.BUILD))
				{
					return CommonEditorPlugin.getImage(CHECKMARK_ICON);
				}
				return CommonEditorPlugin.getImage(RED_X_ICON);
			}
			else if (columnIndex == 2)
			{
				if (participant.isEnabled(IBuildParticipant.BuildType.RECONCILE))
				{
					return CommonEditorPlugin.getImage(CHECKMARK_ICON);
				}
				return CommonEditorPlugin.getImage(RED_X_ICON);
			}
			return null;
		}
	}

	/**
	 * Hides required participants.
	 * 
	 * @author cwilliams
	 */
	private static final class RequiredParticipantFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element)
		{
			if (element instanceof IBuildParticipant)
			{
				IBuildParticipant participant = (IBuildParticipant) element;
				return !participant.isRequired();
			}
			return true;
		}
	}
}
